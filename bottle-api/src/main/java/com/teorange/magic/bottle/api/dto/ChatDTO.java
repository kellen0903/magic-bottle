package com.teorange.magic.bottle.api.dto;

import cn.teorange.framework.core.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 会话表
 *
 * Created by kellen on 2018-06-13 21:12:49
 */
@TableName("chat")
@Data
@Accessors(chain = true)
public class ChatDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   *
   */
  @NotNull(message = "会话id不允许为空", groups = UpdateGroup.class)
  private Long chatId;
  /**
   * 发送人id
   */
  @NotNull(message = "fromUid不允许为空", groups = UpdateGroup.class)
  private Long fromUid;
  /**
   * 魔瓶id
   */
  @NotNull(message = "postId不允许为空", groups = UpdateGroup.class)
  private Long postId;
  /**
   * 接收人Id
   */
  @NotNull(message = "toUid不允许为空", groups = UpdateGroup.class)
  private Long toUid;
  /**
   *
   */
  @NotEmpty(message = "fromNickName不允许为空", groups = UpdateGroup.class)
  private String fromNickName;
  /**
   *
   */
  @NotEmpty(message = "toNickName不允许为空", groups = UpdateGroup.class)
  private String toNickName;
  /**
   * 0：正常 1：关闭  2：举报
   */
  @NotNull(message = "status不允许为空", groups = UpdateGroup.class)
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
  //@NotEmpty(message = "handleUid不允许为空", groups = UpdateGroup.class)
  private Long handleUid;
  /**
   * 举报或者关闭对话人昵称
   */
  //@NotEmpty(message = "handleNickName不允许为空", groups = UpdateGroup.class)
  private String handleNickName;

  private String tagName;
}
