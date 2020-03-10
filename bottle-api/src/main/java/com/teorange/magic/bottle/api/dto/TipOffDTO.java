package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/6/3.
 */
@Data
@Accessors(chain = true)
public class TipOffDTO implements Serializable {

  /**
   * 举报目标id
   */
  @NotEmpty(message = "举报目标id不允许为空")
  private Long itemId;

  /**
   * 举报类型  1：魔瓶 2：评论  3：对话
   */
  @NotEmpty(message = "举报类型不允许为空")
  private Integer itemType;

  /**
   * 举报人id
   */
  private Long userId;

  /**
   * 举报人ip
   */
  private String ip;

  /**
   * 被举报人id
   */
  private Long toUserId;


}
