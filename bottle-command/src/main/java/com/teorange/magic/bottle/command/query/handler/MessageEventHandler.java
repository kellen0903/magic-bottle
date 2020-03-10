package com.teorange.magic.bottle.command.query.handler;

import com.teorange.magic.bottle.api.event.MessageSendEvent;
import com.teorange.magic.bottle.api.service.MessagePushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

/**
 * Created by kellen on 2018/6/2.
 */
@Component
@AllArgsConstructor
@Slf4j
public class MessageEventHandler {

  private MessagePushService messagePushService;


  /**
   * 接收消息通知事件
   */
  @EventHandler
  public void on(MessageSendEvent event) {
    //推送极光
    messagePushService.pushIM(event.getMessageDTO());
  }

}
