package com.teorange.magic.bottle.api.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by kellen on 2018/5/31.删除帖子下面所有评论
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DeleteAllCommentCommand {

  @TargetAggregateIdentifier
  private Long postId;


  private Long userId;

}
