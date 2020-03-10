package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/7/29.
 */
@Data
@Accessors(chain = true)
public class UserNotifyCache implements Serializable {

  /**
   * 是否推送
   */
  private boolean nofityFlag = true;

  /**
   * 是否推送声音
   */
  private boolean soundFlag = true;

  /**
   * 未读消息数量
   */
  private int unReadCount = 0;

}
