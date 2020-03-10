package com.teorange.magic.bottle.command.service;

import static com.teorange.magic.bottle.api.constant.GlobalConstant.POST_TIPOFF_SHIELD_COUNT;
import static com.teorange.magic.bottle.api.constant.GlobalConstant.USER_TIPOFF_CONFIG;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.teorange.magic.bottle.api.constant.GlobalConstant;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import com.teorange.magic.bottle.api.event.UserStatusUpdatedEvent;
import com.teorange.magic.bottle.api.mapper.TipOffLogMapper;
import com.teorange.magic.bottle.api.plugins.redis.SysConfigRedis;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.MessagePushService;
import com.teorange.magic.bottle.api.service.PostReplyService;
import com.teorange.magic.bottle.api.service.PostService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.scheduling.quartz.QuartzEventScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Created by kellen on 2018/6/3.
 */
@Service
@AllArgsConstructor
@Slf4j
public class TipOffService extends ServiceImpl<TipOffLogMapper, TipOffLogEntity> {

  private SysConfigRedis sysConfigRedis;

  private PostService postService;

  private MagicUserService magicUserService;

  private MessagePushService messagePushService;

  private QuartzEventScheduler quartzEventScheduler;

  private PostReplyService postReplyService;


  public PageUtils queryPage(Map<String, Object> params) {
    Page<TipOffLogEntity> page = this.selectPage(
        new Query<TipOffLogEntity>(params).getPage(),
        new EntityWrapper<>()
    );
    return new PageUtils(page);
  }


  /**
   * 处理被举报后的禁言、屏蔽操作 同一帖子如果被x个不同IP举报， 则对其他人自动屏蔽不显示 X天内触发Y次举报后，自动发送系统警告，
   * 然后再触发Z次举报给禁言N天（X/Y/Z/N可以系统配置，初始值X=1,Y=3,Z=5,N=7
   */
  public synchronized void postTipOff(TipOffLogEntity tipOffLogEntity) {
    log.info("开始处理被举报后的操作:{}", tipOffLogEntity);
    Integer itemType = tipOffLogEntity.getItemType();
    Long toUserId = tipOffLogEntity.getToUserId();
    //如果是帖子被举报，先处理
    Long itemId = tipOffLogEntity.getItemId();
    //查询该魔瓶被不同ip举报的次数
    Integer tipCount = this.baseMapper.queryTipOffCount(itemId);
    log.info("查询被不同ip举报的次数是:{}", tipCount);
    String shiledCount = sysConfigRedis.get(POST_TIPOFF_SHIELD_COUNT);
    log.info("系统配置的同一帖子如果被不同IP举报屏蔽的数量是:{}", shiledCount);
    Integer sysShieldCount = 5;
    if (StrUtil.isNotEmpty(shiledCount)) {
      sysShieldCount = Integer.valueOf(shiledCount);
    }
    //查询管理员列表，如果是管理员举报，直接屏蔽
    List<String> adminList = sysConfigRedis
        .getConfigObject(GlobalConstant.ADMIN_USER_LIST, List.class);
    boolean isAdminFlag = adminList.stream()
        .anyMatch(e -> e.equals(tipOffLogEntity.getUserId().toString()));
    log.info("查询到的管理员id列表:{}，当前用户是否是管理员:{}", adminList, isAdminFlag);
    if ((tipCount >= sysShieldCount) || isAdminFlag) {
      if (1 == itemType) {
        //更新帖子的状态为已屏蔽
        log.info("该贴子满足被屏蔽的条件，更新状态为已屏蔽,被举报数量:{},帖子id:{}", tipCount, itemId);
        postService
            .updateById(new PostEntity().setId(itemId).setShieldStatus(1));
      } else if (2 == itemType) {
        postReplyService.updateById(new PostReplyEntity().setId(itemId).setShieldStatus(1));
      }
    }
    this.doShut(toUserId, tipOffLogEntity.getUserId(), itemType, isAdminFlag);
  }


  /**
   * 真正禁言操作方法
   */
  public void doShut(Long toUid, Long fromUid, int itemType, boolean isAdminFlag) {
    //处理用户是否需要被禁言或者禁言提醒的操作
    String config = sysConfigRedis.get(USER_TIPOFF_CONFIG);
    if (null == config) {
      return;
    }
    Map<String, Object> configMap = JSONUtil.toBean(config, Map.class);
    //X天
    Integer day = MapUtil.getInt(configMap, "x");
    //y次
    Integer count = MapUtil.getInt(configMap, "y");
    //再被举报Z次
    Integer againCount = MapUtil.getInt(configMap, "z");
    //禁言n天
    Integer banDay = MapUtil.getInt(configMap, "n");
    //查询用户X天内被举报的次数
    Date nowDateStart = DateUtil.beginOfDay(new Date());
    Date startTime = DateUtil.offsetDay(nowDateStart, -day);
    Date endTime = DateUtil.endOfDay(new Date());
    List<TipOffLogEntity> logList = this.selectList(
        new EntityWrapper<TipOffLogEntity>().eq("to_user_id", toUid)
            .le("create_time", endTime)
            .ge("create_time", startTime));
    int dayCount = 0;
    if (!CollectionUtils.isEmpty(logList)) {
      Map<Long, List<TipOffLogEntity>> longListMap = logList.stream()
          .collect(Collectors.groupingBy(TipOffLogEntity::getItemId));
      //A发了一个帖子，被三个人举报，在用户禁言参数统计时应该算作一条，而不应该算作三条。评论同理。
      //会话举报是一个会话算一次。
      dayCount = longListMap.size();
    }
    log.info("查询到x天内被举报的次数:{}", dayCount);
    //禁言
    MagicUserEntity magicUserEntity = magicUserService.selectById(toUid);
    PushMessageDTO pushMessageDTO = new PushMessageDTO().setCreateTime(new Date())
        .setMsgId(
            IdWorker.getId())
        .setToUid(toUid)
        .setJpushId(magicUserEntity.getJpushId())
        .setToNickName(magicUserEntity.getNickName()).setFromUid(fromUid)
        .setFromSex(1).setToSex(magicUserEntity.getSex());
    //如果是管理员的会话举报则直接禁言
    if ((dayCount == (count + againCount)) || (3 == itemType && isAdminFlag)) {
      magicUserService
          .updateById(new MagicUserEntity().setId(toUid).setStatus(2));
      String content = "警告：您的帐号暂时被封禁" + banDay + "天，封禁原因：您的帐号收到多条合理举报，如若再次发现，将永久封禁。";
      pushMessageDTO.setContent(content).setMsgType(10);
      messagePushService.pushIM(pushMessageDTO);
      //发布禁言解除延迟事件
      Instant unBanTime = Instant.now().plus(banDay, ChronoUnit.DAYS);
      //发布定时事件
      quartzEventScheduler.schedule(unBanTime, new UserStatusUpdatedEvent(toUid, 1));
      //提醒
    } else if (dayCount == count) {
      String content = "警告：您的帐号收到多条合理举报，举报内容为恶意骚扰或不良内容，给用户带来不良影响。如若再次发现将会封禁处理";
      pushMessageDTO.setContent(content).setMsgType(8);
      messagePushService.pushIM(pushMessageDTO);
    }

  }


  public static void main(String[] args) {
    // String array = "[1012870289281667073, 1012878480845500418, 1012939887452581890, 1012939847141126145, 1017376287602204674, 1012956668326014977, 1012901580446285826]";
    // List<Long> list = JSONUtil.toBean(array, List.class);
    List<Long> list = Lists
        .newArrayList(1012870289281667073L, 1012878480845500418L, 1012901580446285826L);
    System.out.println(list.contains(1012901580446285826L));
  }

}
