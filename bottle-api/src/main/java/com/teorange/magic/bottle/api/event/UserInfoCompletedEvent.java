package com.teorange.magic.bottle.api.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/5/27.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UserInfoCompletedEvent {

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


  /**
   * 用户id
   */
  private Long userId;

}
