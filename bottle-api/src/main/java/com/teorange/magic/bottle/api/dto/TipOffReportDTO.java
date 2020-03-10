package com.teorange.magic.bottle.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import java.io.Serializable;
import lombok.Data;

/**
 * Created by kellen on 2018/10/22.
 */
@Data
public class TipOffReportDTO implements Serializable {

  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long userId;

  private Long count;

}
