package com.teorange.magic.bottle.api.event;

import com.teorange.magic.bottle.api.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/5/21.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserCreatedEvent {

  private UserDTO userDTO;

}
