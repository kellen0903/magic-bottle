package com.teorange.magic.bottle.command.query.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.teorange.framework.axon.util.IdWorker;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.UpLogEntity;
import com.teorange.magic.bottle.api.dto.PostDTO;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.enums.MessageEnum;
import com.teorange.magic.bottle.api.event.PostCreatedEvent;
import com.teorange.magic.bottle.api.event.PostUpedEvent;
import com.teorange.magic.bottle.api.service.MessagePushService;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.api.service.UpLogService;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Created by kellen on 2018/5/28. 魔瓶监听器
 */
@Component
@AllArgsConstructor
@Slf4j
public class PostEventHandler {

  private PostService postService;

  private UpLogService upLogService;

  private MessagePushService messagePushService;


  /**
   * 魔瓶入库
   */
  @EventHandler
  public void on(PostCreatedEvent event) {
    PostEntity postEntity = new PostEntity();
    PostDTO postDTO = event.getPostDTO();
    BeanUtils.copyProperties(postDTO, postEntity);
    postEntity.setId(postDTO.getPostId());
    postEntity.setCreateTime(new Date());
    postEntity.setSex(postDTO.getPostUser().getSex());
    postEntity.setUserId(postDTO.getPostUser().getId());
    postEntity.setNickName(postDTO.getNickName());
    postEntity.setTopicId(postDTO.getTopicId());
    postEntity.setTopicName(postDTO.getTopicName());
    List<String> images = postDTO.getImagesList();
    if (!CollectionUtils.isEmpty(images)) {
      postEntity.setImages(JSONUtil.toJsonStr(images));
    }
    postService.insert(postEntity);
  }


  /**
   * 点赞 、取消点赞
   */
  @EventHandler
  @Transactional
  public void on(PostUpedEvent event) {
    Integer type = event.getType();
    boolean isHasSendMsg = event.isHasSendMsg();
    PostDTO postEntity = event.getPostInfo();
    //发帖人
    UserDTO postUser = event.getPostUser();

    //点赞人
    UserDTO upUser = event.getUpUser();
    //点赞
    if (1 == type) {
      postService.updateById(
          new PostEntity().setId(event.getPostId()).setLikeCount(postEntity.getLikeCount() + 1));
      UpLogEntity upLogEntity = new UpLogEntity().setPostId(event.getPostId())
          .setUserId(event.getUserId()).setCreateTime(new Date());
      upLogService.insert(upLogEntity);
      //自己给自己点赞不推送消息
      if (!upUser.getId().equals(postUser.getId()) && !isHasSendMsg) {
        //推送消息
        String content = postEntity.getContent();
        if (StrUtil.isNotEmpty(content)) {
          if (content.length() > 100) {
            content = StrUtil.subWithLength(content, 0, 100) + "...";
          }
        }
        PushMessageDTO pushMessageDTO = new PushMessageDTO().setContent(content)
            .setFromUid(upUser.getId()).setFromNickName(upUser.getNickName())
            .setToUid(postUser.getId())
            .setToNickName(postUser.getNickName()).setPostId(event.getPostId())
            .setMsgId(IdWorker.getId())
            .setMsgType(MessageEnum.UP_NOTIFY.getType()).setJpushId(postUser.getJpushId())
            .setTagName(postEntity.getTagName());

        messagePushService.pushIM(pushMessageDTO);
      }
      //取消点赞
    } else if (2 == type) {
      postService.updateById(
          new PostEntity().setId(event.getPostId()).setLikeCount(postEntity.getLikeCount() - 1));
      EntityWrapper<UpLogEntity> upLogEntityEntityWrapper = new EntityWrapper<>();
      upLogEntityEntityWrapper
          .setEntity(new UpLogEntity().setPostId(event.getPostId()).setUserId(event.getUserId()));
      upLogService.delete(upLogEntityEntityWrapper);
    }
  }


}
