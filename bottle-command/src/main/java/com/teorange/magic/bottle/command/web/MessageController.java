package com.teorange.magic.bottle.command.web;

import static com.teorange.magic.bottle.api.constant.GlobalConstant.FORBIDEN_MESSAGE;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import com.teorange.magic.bottle.api.plugins.wordfilter.WordFilter;
import com.teorange.magic.bottle.api.service.ChatService;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.MessagePushService;
import com.teorange.magic.bottle.api.service.MessageService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/6/2. 消息控制器
 */
@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {


  private MagicUserService userService;

  private MessagePushService messagePushService;

  private WordFilter wordFilter;

  private ThreadPoolExecutor messageThreadPool;

  private MessageService messageService;

  private ChatService chatService;

  /**
   * 发消息
   */
  @PostMapping("/send")
  @Login

  public R sendMessage(@RequestBody PushMessageDTO messageDTO,
      @LoginUser MagicUserEntity magicUser) {
    log.info("sendMessage request data:{}", messageDTO);
    ValidatorUtils.validateEntity(messageDTO);
    //用户是否被禁言
    if (2 == magicUser.getStatus()) {
      return R.error(FORBIDEN_MESSAGE);
    }
    String content = messageDTO.getContent();
    Long msgId = IdWorker.getId();
    if (StrUtil.isNotEmpty(content)) {
      //敏感词过滤
      boolean isFilter = wordFilter.isContains(content);
      if (isFilter) {
        messageDTO.setShieldStatus(1);
      }
    }
    messageDTO
        .setContent(content.replaceAll("p90fsuyqe.bkt.clouddn.com", "img.shaiquan.net"));
    messageDTO.setFromUid(magicUser.getId());
    messageDTO.setCreateTime(new Date());
    messageDTO.setFromSex(magicUser.getSex());
    if (StrUtil.isEmpty(messageDTO.getFromNickName())) {
      messageDTO.setFromNickName(magicUser.getNickName());
    }
    messageDTO.setMsgId(msgId);
    MagicUserEntity toUserInfo = userService.selectById(messageDTO.getToUid());
    if (null != toUserInfo) {
      messageDTO.setJpushId(toUserInfo.getJpushId());
      messageDTO.setToSex(toUserInfo.getSex());
      if (StrUtil.isEmpty(messageDTO.getToNickName())) {
        messageDTO.setToNickName(toUserInfo.getNickName());
      }
    }
    Long fromUid = messageDTO.getFromUid();
    Long toUid = messageDTO.getToUid();
    Long postId = messageDTO.getPostId();
    String chatUid = fromUid > toUid ? (toUid.toString() + fromUid.toString() + postId.toString())
        : (fromUid.toString() + toUid.toString() + postId.toString());
    Long chatId = (long) HashUtil.fnvHash(chatUid);
    messageDTO.setChatId(chatId);
    //查询会话状态，如果已关闭则不允许发消息
    ChatEntity chatEntity = chatService.selectById(chatId);
    if (null != chatEntity && 0 != chatEntity.getStatus()) {
      return R.error("会话已关闭");
    }
    log.info("将发送消息的内容:{}", messageDTO);
    //异步发送消息
    messageThreadPool.execute(() -> messagePushService.pushChatMessage(messageDTO));
    Map<String, Object> data = Maps.newHashMap();
    data.put("msgId", msgId);
    data.put("chatId", chatId);
    return R.ok().put("data", data);
  }


  /**
   * 消息已读通知接口
   */
  @PostMapping("/notify")
  @Login
  public R notify(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody PushMessageDTO messageDTO) {
    Long msgId = messageDTO.getMsgId();
    if (Validator.isEmpty(msgId)) {
      return R.error("msgId不允许为空");
    }
    //更新消息状态为已接收
    messageService.update(new MessageEntity().setStatus(1).setUpdateTime(new Date()),
        new EntityWrapper<MessageEntity>().eq("msg_id", msgId)
            .eq("to_uid", magicUserEntity.getId()));
    return R.ok();
  }


  /**
   * 获取系统消息
   */
  @PostMapping("/getSystemMsg")
  @Login
  public R getSystemMsg(@LoginUser MagicUserEntity magicUserEntity) {
    List<MessageEntity> messageEntityList = messageService.selectList(
        new EntityWrapper<MessageEntity>()
            .eq("to_uid", magicUserEntity.getId())
            .in("msg_type", Lists.newArrayList("8", "9", "10")));
    return R.ok().put("data", messageEntityList);
  }


  /**
   * 推送未读消息
   */
  @PostMapping("/pushOfflineMsg")
  @Login
  public R pushOfflineMsg(@LoginUser MagicUserEntity magicUserEntity) {
    messageThreadPool.execute(
        () -> messagePushService
            .pushUnReadMessage(magicUserEntity.getId(), magicUserEntity.getJpushId()));
    return R.ok();
  }

  public static void main(String[] args) {
    String img = "http://p90fsuyqe.bkt.clouddn.com/1530289070770/-128255489/2080X1560";
    String str = img.replaceAll("p90fsuyqe.bkt.clouddn.com", "img.shaiquan.net");
    System.out.println(str);
  }


}
