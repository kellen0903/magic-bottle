package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Created by kellen on 2018/5/30.标签
 */
@Data
@TableName("tag")
public class TagEntity implements Serializable {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String tagName;

  private Integer orderNum;

  private Date createTime;

  private Date updateTime;

  private Integer deleted;

  /**
   * 标签描述
   */
  private String tagDesc;

  /**
   * ios标签描述
   */
  private String tagIosDesc;

  /**
   * android标签描述
   */
  private String tagAndroidDesc;

  /**
   * 是否可以发图片  0是 1否
   */
  private Integer imgFlag;

}
