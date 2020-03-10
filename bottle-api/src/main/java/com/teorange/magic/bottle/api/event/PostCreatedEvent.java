package com.teorange.magic.bottle.api.event;

import com.teorange.magic.bottle.api.dto.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/5/28.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class PostCreatedEvent {

  private PostDTO postDTO;


}
