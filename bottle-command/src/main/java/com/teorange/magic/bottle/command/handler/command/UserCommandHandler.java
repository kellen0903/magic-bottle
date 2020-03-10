package com.teorange.magic.bottle.command.handler.command;

import com.teorange.magic.bottle.api.command.CreateUserCommand;
import com.teorange.magic.bottle.command.aggregate.UserAggregate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * Created by kellen on 2018/5/21.
 */
@Component
@Slf4j
@AllArgsConstructor
public class UserCommandHandler {


  private Repository<UserAggregate> userAggregateRepository;

  /**
   * 监听创建用户命令
   */
  @CommandHandler
  public void hanlde(CreateUserCommand command) {

    try {
      userAggregateRepository.newInstance(
          () -> new UserAggregate(command));
    } catch (Exception e) {
      log.error("handle CreateUserCommand error:{}", e);
    }

  }

}
