package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * Created by kellen on 2018/11/16.
 */
@Data
public class IosAlertDTO implements Serializable {

  private String title;

  private String subtitle;

  private String body;

}
