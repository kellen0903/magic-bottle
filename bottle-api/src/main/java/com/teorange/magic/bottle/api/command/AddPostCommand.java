package com.teorange.magic.bottle.api.command;

import com.teorange.magic.bottle.api.dto.PostDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/5/28.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AddPostCommand {


  private PostDTO postDTO;

}
