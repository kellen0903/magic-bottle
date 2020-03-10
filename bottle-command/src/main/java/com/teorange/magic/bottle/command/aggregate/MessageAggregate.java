package com.teorange.magic.bottle.command.aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import com.teorange.magic.bottle.api.command.SendMessageCommand;
import com.teorange.magic.bottle.api.event.MessageSendEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

/**
 * Created by kellen on 2018/5/24. 消息聚合根
 */
@NoArgsConstructor
@Aggregate(repository = "messageAggregateRepository")
@ToString
@Slf4j
@Getter
@Setter
public class MessageAggregate {


  @AggregateIdentifier
  private Long msgId;

  /**
   * 魔瓶id
   */
  private Long postId;

  /**
   * 发送者id
   */
  private Long from;

  /**
   * 接收者id
   */
  private Long to;

  /**
   * 发送者昵称
   */
  private String fromNickName;

  /**
   * 接收者昵称
   */
  private String toNickName;

  /**
   * 1 文字 2图片 3评论通知  4点赞通知
   */
  private Integer msgType;


  /**
   * 状态 0：未发送  1：已发送，未读  2：已读
   */
  private Integer status;

  /**
   * 消息内容
   */
  private String content;

  /**
   * 图片消息地址
   */
  private String imageUrl;

  /**
   * 推送目标jpushId
   */
  private Long jpushId;


  @CommandHandler
  public MessageAggregate(SendMessageCommand command) {
    apply(new MessageSendEvent(command.getMessageDTO()));
  }


  @EventHandler
  public void on(MessageSendEvent event) {
    BeanUtils.copyProperties(event.getMessageDTO(), this);
  }


}
