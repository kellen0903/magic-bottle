package com.teorange.magic.bottle.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会话表
 *
 * Created by kellen on 2018-06-13 21:12:49
 */
@TableName("chat")
@Data
@Accessors(chain = true)
public class ChatEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   *
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long chatId;
  /**
   * 发送人id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long fromUid;
  /**
   * 魔瓶id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long postId;
  /**
   * 接收人Id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long toUid;
  /**
   *
   */
  private String fromNickName;
  /**
   *
   */
  private String toNickName;
  /**
   * 0：正常 1：关闭  2：举报
   */
  private Integer status;
  /**
   *
   */
  private Date createTime;
  /**
   *
   */
  private Date updateTime;
  /**
   * 举报或者关闭对话人id
   */
  private Long handleUid;
  /**
   * 举报或者关闭对话人昵称
   */
  private String handleNickName;

  private Integer fromSex;

  private Integer toSex;

  private Integer fromDeleted;

  private Integer toDeleted;

  @TableField(exist = false)
  private String tagName;

  /**
   * 是否有收藏   0否  1是
   */
  @TableField(exist = false)
  private Integer collectStatus;
}
