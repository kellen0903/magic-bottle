package com.teorange.magic.bottle.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 帖子回复表
 *
 * Created by kellen on 2018-05-28 22:03:30
 */
@TableName("post_reply")
@Data
@Accessors(chain = true)
public class PostReplyEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   *
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long id;

  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long postId;
  /**
   * 回复用户id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long fromUid;
  /**
   * 回复用户昵称
   */
  private String fromNickName;
  /**
   * 回复内容
   */
  private String content;

  /**
   * 目标用户id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long toUid;


  /**
   * 目标用户昵称
   */
  private String toNickName;
  /**
   *
   */
  private Date createTime;
  /**
   * 是否删除 0：否 1：是
   */
  private Integer deleted;

  private Integer fromSex;

  private Integer toSex;

  /**
   * 是否被屏蔽 0否 1是
   */
  private Integer shieldStatus;

  /**
   * 评论类型  1评论 2回复
   */
  private Integer commentType;
}
