package com.teorange.magic.bottle.api.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

/**
 * Created by kellen on 2018/5/27.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class CompleteUserInfoCommand {

  @TargetAggregateIdentifier
  private String openId;

  /**
   * 性别
   */
  private Integer sex;


  /**
   * 昵称
   */
  private String nickName;

  /**
   * 年龄描述
   */
  private String age;

}
