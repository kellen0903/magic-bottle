package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/6/10.
 */
@Data
@Accessors(chain = true)
public class FeedBackDTO implements Serializable {

  @NotEmpty(message = "反馈内容不允许为空")
  private String content;

  private String imgUrl;

  @NotEmpty(message = "联系方式不允许为空")
  private String link;

}
