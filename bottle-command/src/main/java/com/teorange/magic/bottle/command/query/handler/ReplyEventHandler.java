package com.teorange.magic.bottle.command.query.handler;

import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.axon.util.IdWorker;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.dto.PostReplyDTO;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import com.teorange.magic.bottle.api.enums.MessageEnum;
import com.teorange.magic.bottle.api.event.AllPostCommentDeletedEvent;
import com.teorange.magic.bottle.api.event.PostReplyEvent;
import com.teorange.magic.bottle.api.event.SingleCommentDeletedEvent;
import com.teorange.magic.bottle.api.service.MessagePushService;
import com.teorange.magic.bottle.api.service.PostReplyService;
import com.teorange.magic.bottle.api.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by kellen on 2018/5/31.
 */
@Component
@AllArgsConstructor
@Slf4j
public class ReplyEventHandler {

  private PostService postService;

  private PostReplyService postReplyService;

  private MessagePushService messagePushService;


  /**
   * 评论
   */
  @EventHandler
  @Transactional
  public void on(PostReplyEvent event) {
    log.info("评论事件入库参数:{}", event);
    PostReplyDTO postReplyDTO = event.getReplyDTO();
    PostEntity postEntity = postService.selectById(postReplyDTO.getPostId());
    postService.updateById(
        new PostEntity().setId(postReplyDTO.getPostId())
            .setCommentCount(postEntity.getCommentCount() + 1));

    PostReplyEntity postReplyEntity = new PostReplyEntity();
    BeanUtils.copyProperties(postReplyDTO, postReplyEntity);
    postReplyEntity.setId(postReplyDTO.getReplyId());
    postReplyService.insert(postReplyEntity);
    //自己给自己回复的不发送通知
    if (!postReplyDTO.getFromUid().equals(postReplyDTO.getToUid()) && 0 == postReplyDTO
        .getShieldStatus()) {
      //推送消息
      String content = postEntity.getContent();
      if (StrUtil.isNotEmpty(content)) {
        if (content.length() > 100) {
          content = StrUtil.subWithLength(content, 0, 100) + "...";
        }
      }
      PushMessageDTO pushMessageDTO = new PushMessageDTO().setContent(content)
          .setFromUid(postReplyDTO.getFromUid())
          .setFromNickName(
              postReplyDTO.getFromNickName() == null ? "" : postReplyDTO.getFromNickName())
          .setToUid(postReplyDTO.getToUid())
          .setToNickName(postReplyDTO.getToNickName()).setPostId(postReplyDTO.getPostId())
          .setMsgId(IdWorker.getId())
          .setMsgType(MessageEnum.REPLY_NOTIFY.getType()).setJpushId(postReplyDTO.getToPushId())
          .setTagName(postEntity.getTagName());
      //楼主回复的昵称取发帖的昵称
      if (postReplyDTO.getFromUid().equals(postEntity.getUserId())) {
        pushMessageDTO.setFromNickName(postEntity.getNickName());
      }
      messagePushService.pushIM(pushMessageDTO);
    }
  }


  /**
   * 删除单条评论
   */
  @EventHandler
  public void on(SingleCommentDeletedEvent event) {
    postReplyService.updateById(new PostReplyEntity().setId(event.getReplyId()).setDeleted(1));
    PostEntity postEntity = postService.selectById(event.getPostId());
    postService.updateById(
        new PostEntity().setId(event.getPostId())
            .setCommentCount(postEntity.getCommentCount() - 1));
  }


  /**
   * 删除帖子下面的所有评论
   */
  @EventHandler
  public void on(AllPostCommentDeletedEvent event) {
    postReplyService.update(new PostReplyEntity().setPostId(event.getPostId()),
        new EntityWrapper<PostReplyEntity>().eq("post_id", event.getPostId()));
    postService.updateById(
        new PostEntity().setId(event.getPostId())
            .setCommentCount(0L));
  }


}
