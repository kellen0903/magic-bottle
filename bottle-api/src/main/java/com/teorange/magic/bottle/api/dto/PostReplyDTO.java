package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 帖子回复
 *
 * Created by kellen on 2018-05-28 22:03:30
 */
@Data
public class PostReplyDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long replyId;

  @NotEmpty(message = "魔瓶id不允许为空")
  private Long postId;
  /**
   * 用户id
   */
  private Long fromUid;
  /**
   * 用户昵称
   */
  private String fromNickName;

  /**
   * 目标用户id
   */
  private Long toUid;

  /**
   * 对方的jpushId
   */
  private String toPushId;

  /**
   * 目标用户昵称
   */
  private String toNickName;
  /**
   * 回复内容
   */
  @NotEmpty(message = "内容不允许为空")
  private String content;
  /**
   *
   */
  private Date createTime;
  /**
   * 是否删除 0：否 1：是
   */
  private Integer deleted;

  /**
   * 删除类型 1删除自己的某条评论  2删除帖子所有评论
   */
  private Integer deleteType = 1;

  private Integer fromSex;

  private Integer toSex;

  /**
   * 是否被屏蔽 0否 1是
   */
  private Integer shieldStatus = 0;

  /**
   * 评论类型  1评论 2回复
   */
  private Integer commentType;
}
