package com.teorange.magic.bottle.api.dto;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/6/2.
 */
@Data
@Accessors(chain = true)
public class PushMessageDTO {

  /**
   * 消息id
   */
  private Long msgId;


  /**
   * 发送者昵称
   */
  @NotEmpty(message = "发送者昵称不允许为空")
  private String fromNickName;


  /**
   * 接收者昵称
   */
  private String toNickName;

  /**
   * 发送者id
   */

  private Long fromUid;

  /**
   * 接收者id
   */
  @NotNull(message = "接收者id不允许为空")
  private Long toUid;

  /**
   * 发生内容
   */
  private String content;

  /**
   * 图片地址
   */
  private String imageUrl;

  /**
   * 消息类型 1 文字 2图片 3评论通知  4点赞通知  5会话关闭  6会话举报 7会话开启  8禁言提醒
   */
  @NotNull(message = "消息类型不允许为空")
  private Integer msgType;

  /**
   * 极光推送id
   */
  private String jpushId;

  /**
   * 魔瓶id
   */
  @NotNull(message = "魔瓶id不允许为空")
  private Long postId;


  private Date createTime = new Date();

  /**
   * 是否屏蔽 0否 1是
   */
  private Integer shieldStatus = 0;

  /**
   * 帖子标签
   */
  private String tagName;

  /**
   * 会话id
   */
  private Long chatId;

  /**
   * 是否是第一次会话
   */
  private boolean isFirstChat;

  private Integer fromSex;

  private Integer toSex;


}
