package com.teorange.magic.bottle.command.aggregate;

import java.util.Date;
import lombok.Data;

/**
 * Created by kellen on 2018/5/24. 举报
 */
@Data
public class TipOffLog {

  private Long itemId;

  private Integer itemType;

  private Long userId;

  private String ip;

  private Date createTime;

}
