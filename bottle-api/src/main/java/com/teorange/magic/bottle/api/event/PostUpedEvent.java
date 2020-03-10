package com.teorange.magic.bottle.api.event;

import com.teorange.magic.bottle.api.dto.PostDTO;
import com.teorange.magic.bottle.api.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/5/30.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostUpedEvent {

  private Long postId;

  private Long userId;

  // 1 点赞 2取消点赞 3举报
  private Integer type;

  private UserDTO postUser;

  private PostDTO postInfo;

  private UserDTO upUser;

  //是否需要发送消息推送
  private boolean isHasSendMsg = true;
}
