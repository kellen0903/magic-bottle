package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/6/2.
 */
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MessageDTO implements Serializable {

  /**
   *
   */
  private Long msgId;
  /**
   * 魔瓶id
   */
  @NotEmpty(message = "魔瓶id不允许为空")
  private Long postId;
  /**
   * 发送者id
   */
  private Long from;
  /**
   * 接收者id
   */
  @NotEmpty(message = "接收者id不允许为空")
  private Long to;
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
  @NotEmpty(message = "消息类型不允许为空")
  private Integer type;
  /**
   * 状态 0：未发送  1：已发送
   */
  private Integer status;
  /**
   * 消息内容
   */
  @NotEmpty(message = "内容不允许为空")
  private String content;
  /**
   * 图片地址
   */
  private String imageUrl;
  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 极光推送id
   */
  private String toJpushId;

  private Long chatId;

}
