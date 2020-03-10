package com.teorange.magic.bottle.api.event;

import com.teorange.magic.bottle.api.dto.MessageDTO;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/6/2.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageSendEvent {

  private PushMessageDTO messageDTO;

}
