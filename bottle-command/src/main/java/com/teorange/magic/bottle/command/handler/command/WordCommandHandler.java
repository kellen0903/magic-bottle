package com.teorange.magic.bottle.command.handler.command;

import com.teorange.magic.bottle.api.command.ResetWordCommand;
import com.teorange.magic.bottle.api.plugins.wordfilter.WordFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

/**
 * Created by kellen on 2018/8/16.
 */
@Component
@Slf4j
@AllArgsConstructor
public class WordCommandHandler {

  private WordFilter wordFilter;


  @CommandHandler
  public void handle(ResetWordCommand command) {
    wordFilter.resetInit();
  }

}
