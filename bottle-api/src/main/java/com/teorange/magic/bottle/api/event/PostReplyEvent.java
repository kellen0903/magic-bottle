package com.teorange.magic.bottle.api.event;

import com.teorange.magic.bottle.api.dto.PostReplyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/5/30.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostReplyEvent {



  private PostReplyDTO replyDTO;

}
