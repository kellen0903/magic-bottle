package com.teorange.magic.bottle.api.command;

import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/6/2.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageCommand {

  private PushMessageDTO messageDTO;

}
