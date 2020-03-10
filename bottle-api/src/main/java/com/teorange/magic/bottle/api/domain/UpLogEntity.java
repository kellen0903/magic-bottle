package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/5/30.点赞日志
 */
@TableName("up_log")
@Data
@Accessors(chain = true)
public class UpLogEntity implements Serializable {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;

  private Long postId;

  private Date createTime;

  private Date updateTime;

  private Integer deleted;

}
