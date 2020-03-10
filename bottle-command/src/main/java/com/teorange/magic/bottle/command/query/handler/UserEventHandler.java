package com.teorange.magic.bottle.command.query.handler;

import cn.teorange.framework.axon.util.IdWorker;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.teorange.magic.bottle.api.domain.LoginLogEntity;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.event.LoginedEvent;
import com.teorange.magic.bottle.api.event.UserCreatedEvent;
import com.teorange.magic.bottle.api.event.UserInfoCompletedEvent;
import com.teorange.magic.bottle.api.event.UserStatusUpdatedEvent;
import com.teorange.magic.bottle.api.service.LoginLogService;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.MessagePushService;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by kellen on 2018/5/21.
 */
@Component
@AllArgsConstructor
@Slf4j
public class UserEventHandler {

  private MagicUserService magicUserService;

  private ThreadPoolExecutor messageThreadPool;

  private MessagePushService messagePushService;

  private LoginLogService loginLogService;

  /**
   * 注册
   */
  @EventHandler
  public void on(UserCreatedEvent event) {
    log.info("接收到注册事件:{}", event);
    MagicUserEntity magicUserEntity = new MagicUserEntity();
    BeanUtils.copyProperties(event.getUserDTO(), magicUserEntity);
    //更新imei查询该设备是否有被禁言
    Integer isDeviceBan = magicUserService.selectCount(
        new EntityWrapper<MagicUserEntity>().eq("imei", magicUserEntity.getImei())
            .eq("device_status", 2));
    //如果该设备被禁言，则新注册账号也被禁言
    if (isDeviceBan > 0) {
      log.info("新注册会员处于设备禁言状态!");
      magicUserEntity.setDeviceStatus(2);
      magicUserEntity.setStatus(2);
    }
    //unBindOtherJpushId(event.getUserDTO().getJpushId(), event.getUserDTO().getOpenId());
    magicUserService.insert(magicUserEntity);
    saveLoginLog(event.getUserDTO());
  }


  /**
   * 登陆后更新
   */
  @EventHandler
  public void on(LoginedEvent event) {
    log.info("接收到登陆事件:{}", event);
    String oldJpushId = event.getOldJpushId();
    String newJpushId = event.getJpushId();
    //查询用户原来的jpushId
    EntityWrapper<MagicUserEntity> wrapper = new EntityWrapper<>();
    wrapper.setEntity(new MagicUserEntity().setOpenId(event.getOpenId()));
    MagicUserEntity data = new MagicUserEntity();
    BeanUtils.copyProperties(event, data);
    //解除其他账号绑定的jpushId
    //this.unBindOtherJpushId(event.getJpushId(), event.getOpenId());
    magicUserService.update(data, wrapper);
    if (null != oldJpushId) {
      //判断是否是不同设备登陆,不同的jpushId ，对应不同的设备
      if (!newJpushId.equals(oldJpushId)) {
        log.info("登陆切换设备，JpushId:{},新的jpushid:{},用户id:{}原开始给原设备发送消息", oldJpushId, newJpushId,
            event.getId());
        //给之前的设备发送下线通知
        MessageEntity messageEntity = new MessageEntity().setFromUid(event.getId())
            .setToUid(event.getId()).setJpushId(newJpushId)
            .setCreateTime(new Date()).setMsgType(9).setContent("您已在另外一台设备登陆")
            .setMsgId(IdWorker.getId());
        messageThreadPool.execute(() -> messagePushService.pushMessage(messageEntity));
      }
    }
    //推送未读消息
//    messageThreadPool.execute(
//        () -> messagePushService.pushUnReadMessage(event.getId(), event.getJpushId()));
    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(event, userDTO);
    saveLoginLog(userDTO);
  }


  /**
   * 查询这个jpushId是否被其他账号绑定,如果有则将其他的账号jpushId置为空
   */
  private void unBindOtherJpushId(String jpushId, String openId) {
    log.info("登陆之前开始解除其他已绑定的jpushId");
    MagicUserEntity entity = magicUserService
        .query(new MagicUserEntity().setJpushId(jpushId));
    log.info("查询到的其他绑定的用户信息:{}", entity);
    if (null != entity && !openId.equals(entity.getOpenId())) {
      magicUserService.updateById(new MagicUserEntity().setJpushId(null).setId(entity.getId()));
    }
  }


  /**
   * 保存登陆日志
   */
  private void saveLoginLog(UserDTO userDTO) {
    LoginLogEntity loginLogEntity = new LoginLogEntity();
    BeanUtils.copyProperties(userDTO, loginLogEntity);
    loginLogEntity.setUserId(userDTO.getId()).setIp(userDTO.getLastLoginIp())
        .setCreateTime(new Date());
    loginLogService.insert(loginLogEntity);
  }

  /**
   * 完善资料
   */
  @EventHandler
  public void on(UserInfoCompletedEvent event) {
    log.info("接收到完善资料事件:{}", event);
    EntityWrapper<MagicUserEntity> wrapper = new EntityWrapper<>();
    wrapper.setEntity(new MagicUserEntity().setId(event.getUserId()));
    MagicUserEntity data = new MagicUserEntity();
    BeanUtils.copyProperties(event, data);
    magicUserService.update(data, wrapper);
  }

  /**
   * 更新会员状态
   */
  @EventHandler
  public void on(UserStatusUpdatedEvent event) {
    log.info("接收到解除会员禁言事件:{}", event);
    MagicUserEntity userEntity = magicUserService.selectById(event.getUserId());
    //查询当前设备是否被禁言
    Integer isDeviceBan = magicUserService.selectCount(
        new EntityWrapper<MagicUserEntity>().eq("imei", userEntity.getImei())
            .eq("device_status", 2));
    //如果该设备被禁言，则新注册账号也被禁言
    if (isDeviceBan > 0) {
      log.info("将要解禁的账户正处于设备禁言状态，无法解除禁言");
      return;
    }
    magicUserService
        .updateById(new MagicUserEntity().setId(event.getUserId()).setStatus(event.getStatus()));
  }


}
