package com.teorange.magic.bottle.api.event;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/6/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdatedEvent implements Serializable {

  private Long userId;

  private Integer status;

}
