package com.teorange.magic.bottle.api.command;

import com.teorange.magic.bottle.api.dto.UserDTO;
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
public class UpPostCommand {

  @TargetAggregateIdentifier
  private Long postId;

  private Long userId;

  //1 点赞 2取消点赞  3举报
  private Integer type;

  private UserDTO upUser;

}
