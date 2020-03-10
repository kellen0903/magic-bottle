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
 * 消息表
 *
 * Created by kellen on 2018-05-28 22:03:30
 */
@TableName("message")
@Data
@Accessors(chain = true)
public class MessageEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   *
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long msgId;
  /**
   * 魔瓶id
   */
  private Long postId;
  /**
   * 发送者id
   */
  private Long fromUid;
  /**
   * 接收者id
   */
  private Long toUid;
  /**
   * 发送者昵称
   */
  private String fromNickName;
  /**
   * 接收者昵称
   */
  private String toNickName;
  /**
   * 1 文字 2图片 3评论通知  4点赞通知
   */
  private Integer msgType;
  /**
   * 状态 0：未发送  1：已发送
   */
  private Integer status;
  /**
   * 消息内容
   */
  private String content;
  /**
   * 图片地址
   */
  private String imageUrl;
  /**
   * 创建时间
   */
  private Date createTime;

  private Date updateTime;

  private String jpushId;

  private Integer shieldStatus;

  /**
   * 会话id
   */
  private Long chatId;


  //对应帖子
  @TableField(exist = false)
  private String postContent;

  /**
   * 标签名称
   */
  private String tagName;

  private Integer fromSex;

  private Integer toSex;
}
