package com.teorange.magic.bottle.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/5/31.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SingleCommentDeletedEvent {

  private Long postId;

  private Long replyId;


}
