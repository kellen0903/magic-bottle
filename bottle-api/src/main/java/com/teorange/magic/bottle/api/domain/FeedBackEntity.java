package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/6/10.
 */
@TableName("feed_back")
@Data
@Accessors(chain = true)
public class FeedBackEntity implements Serializable {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String content;

  private String imgUrl;

  private String link;

  private Date createTime;

  private String createBy;

}
