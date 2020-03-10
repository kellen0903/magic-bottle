package com.teorange.magic.bottle.api.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import com.teorange.magic.bottle.api.plugins.jpush.JpushMessageUtil;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Created by kellen on 2018/6/2.
 */
@Service
@AllArgsConstructor
@Slf4j
public class MessagePushService {

  private MessageService messageService;

  private JpushMessageUtil jpushMessageUtil;

  private ChatService chatService;

  private MagicUserService magicUserService;


  /**
   * 通用的方法，message消息入库并推送给极光
   */
  public void pushMessage(PushMessageDTO pushMessageDTO) {
    //消息通知插入message表
    this.saveMessage(pushMessageDTO);
    //极光推送
    jpushMessageUtil
        .sendPush(Lists.newArrayList(pushMessageDTO.getToUid().toString()),
            pushMessageDTO.getContent());
  }


  /**
   * 发送消息，不入库
   */
  public void pushMessage(MessageEntity pushMessageDTO) {
    //极光推送
    jpushMessageUtil
        .sendPush(Lists.newArrayList(pushMessageDTO.getToUid().toString()),
            JSONUtil.toJsonStr(pushMessageDTO));
  }


  /**
   * 发送im自定义消息
   */
  public void pushIM(PushMessageDTO pushMessageDTO) {
    this.saveMessage(pushMessageDTO);
    jpushMessageUtil
        .sendPush(Lists.newArrayList(pushMessageDTO.getToUid().toString()),
            JSONUtil.toJsonStr(pushMessageDTO));
  }


  /**
   * 发送im自定义消息
   */
  public void pushChatMessage(PushMessageDTO pushMessageDTO) {
    this.saveChatMessage(pushMessageDTO);
    jpushMessageUtil
        .sendPush(Lists.newArrayList(pushMessageDTO.getToUid().toString()),
            JSONUtil.toJsonStr(pushMessageDTO));
  }


  /**
   * 保存消息
   */
  private void saveMessage(PushMessageDTO pushMessageDTO) {
    MessageEntity messageEntity = new MessageEntity()
        .setContent(pushMessageDTO.getContent())
        .setCreateTime(pushMessageDTO.getCreateTime()).setFromUid(pushMessageDTO.getFromUid())
        .setFromNickName(pushMessageDTO.getFromNickName())
        .setMsgId(pushMessageDTO.getMsgId())
        .setPostId(pushMessageDTO.getPostId()).setToUid(pushMessageDTO.getToUid())
        .setToNickName(pushMessageDTO.getToNickName()).setMsgType(
            pushMessageDTO.getMsgType()).setJpushId(pushMessageDTO.getJpushId())
        .setChatId(pushMessageDTO.getChatId()).setTagName(pushMessageDTO.getTagName())
        .setFromSex(pushMessageDTO.getFromSex()).setToSex(pushMessageDTO.getToSex());
    messageService.insert(messageEntity);
  }


  /**
   * 保存会话消息
   */
  private void saveChatMessage(PushMessageDTO pushMessageDTO) {
    Long chatId = pushMessageDTO.getChatId();
    //查询是否存在
    ChatEntity chatEntity = chatService.selectById(chatId);
    if (null == chatEntity) {
      //保存会话id
      chatEntity = new ChatEntity();
      BeanUtils.copyProperties(pushMessageDTO, chatEntity);
      chatEntity.setChatId(chatId).setCreateTime(new Date());
      chatService.insert(chatEntity);
    }
    pushMessageDTO.setChatId(chatId);
    this.saveMessage(pushMessageDTO);
  }


  /**
   * 打开app推送未读消息
   */
  public void pushUnReadMessage(Long userId, String jpushId) {
    List<MessageEntity> messageEntityList = messageService
        .selectList(new EntityWrapper<MessageEntity>().eq("to_uid", userId).eq("status", 0));
    if (!CollectionUtils.isEmpty(messageEntityList)) {
      messageEntityList.forEach(e -> {
        e.setJpushId(jpushId);
        this.pushMessage(e);
      });
    }
  }

}
