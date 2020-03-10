package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import java.util.Set;
import lombok.Data;

/**
 * Created by kellen on 2018/7/24.
 */
@Data
public class BlackCache implements Serializable {

  /**
   * 帖子id
   */
  private Set<Long> postIds;

  /**
   * 黑名单id
   */
  private Set<Long> targetUids;

}
