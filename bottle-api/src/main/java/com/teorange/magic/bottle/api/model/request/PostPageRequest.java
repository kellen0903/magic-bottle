package com.teorange.magic.bottle.api.model.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/5/28.
 */
@Data
public class PostPageRequest extends BasePageRequest {

  //@NotEmpty(message = "标签id不允许为空")
  private Long tagId;

  private Long userId;

}
