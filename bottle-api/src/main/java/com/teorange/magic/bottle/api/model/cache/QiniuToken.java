package com.teorange.magic.bottle.api.model.cache;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Created by kellen on 2018/5/30.
 */
@Data
public class QiniuToken implements Serializable {

  private String token;

  private Date expireTime;

}
