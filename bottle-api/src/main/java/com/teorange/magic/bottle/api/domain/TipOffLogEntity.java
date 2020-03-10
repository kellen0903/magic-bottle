package com.teorange.magic.bottle.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/6/3.  举报记录实体类
 */
@Data
@TableName("tip_off_log")
@Accessors(chain = true)
public class TipOffLogEntity implements Serializable {


  /**
   * 主键
   */
  private Long id;

  /**
   * 举报目标id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long itemId;

  /**
   * 举报类型  1：魔瓶 2：评论  3：对话
   */
  private Integer itemType;

  /**
   * 举报人id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long userId;

  /**
   * 举报人ip
   */
  private String ip;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 被举报人id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long toUserId;


}
