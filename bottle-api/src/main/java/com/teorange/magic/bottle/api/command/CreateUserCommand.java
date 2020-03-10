package com.teorange.magic.bottle.api.command;

import com.teorange.magic.bottle.api.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/5/21.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CreateUserCommand {

  private UserDTO userDTO;

}
