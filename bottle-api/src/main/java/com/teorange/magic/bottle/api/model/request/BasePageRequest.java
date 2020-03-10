package com.teorange.magic.bottle.api.model.request;

/**
 * Created by kellen on 2018/5/28.
 */

import java.io.Serializable;
import lombok.Data;

@Data
public class BasePageRequest implements Serializable {

  private Integer pag = 1;

  private Integer limit = 50;

}
