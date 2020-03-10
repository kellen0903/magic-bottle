package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/9/25. 话题实体类
 */

@Data
@TableName("topic")
@Accessors(chain = true)
public class TopicEntity implements Serializable {


  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 标签id
   */
  private Long tagId;

  /**
   * 话题名称
   */
  private String topicName;

  private String createdBy;

  private String updatedBy;

  private Date createTime;

  private Date updateTime;

  private Integer orderNum;


}
