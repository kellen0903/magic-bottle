package com.teorange.magic.bottle.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/5/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllPostCommentDeletedEvent {

  private Long postId;


  private Long userId;

}
