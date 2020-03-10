package com.teorange.magic.bottle.command.aggregate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.EntityId;

/**
 * Created by kellen on 2018/6/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UpLog {

  @EntityId
  private Long userId;
}
