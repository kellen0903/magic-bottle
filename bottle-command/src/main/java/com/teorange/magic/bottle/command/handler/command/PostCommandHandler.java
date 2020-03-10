package com.teorange.magic.bottle.command.handler.command;

import com.teorange.magic.bottle.api.command.AddPostCommand;
import com.teorange.magic.bottle.command.aggregate.PostAggregate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.stereotype.Component;

/**
 * Created by kellen on 2018/5/28.
 */
@Component
@Slf4j
@AllArgsConstructor
public class PostCommandHandler {

  private Repository<PostAggregate> postAggregateRepository;

  /**
   * 监听发表帖子命令
   */
  @CommandHandler
  public void handle(AddPostCommand command) {
    try {
      postAggregateRepository.newInstance(
          () -> new PostAggregate(command));
    } catch (Exception e) {
      log.error("handle AddPostCommand error:{}", e);
    }
  }

}
