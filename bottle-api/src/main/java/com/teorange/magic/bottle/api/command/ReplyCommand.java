package com.teorange.magic.bottle.api.command;

import com.teorange.magic.bottle.api.dto.PostReplyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by kellen on 2018/5/30.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReplyCommand {

  @TargetAggregateIdentifier
  private Long postId;

  private PostReplyDTO replyDTO;

}
