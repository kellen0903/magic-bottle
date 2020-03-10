package com.teorange.magic.bottle.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by kellen on 2018/6/2.
 */
@AllArgsConstructor
@Getter
public enum MessageEnum {

  TEXT(1, "文字"),
  IMAGE(2, "图片"),
  REPLY_NOTIFY(3, "评论通知"),
  UP_NOTIFY(4, "点赞通知");

  private Integer type;

  private String name;

}
