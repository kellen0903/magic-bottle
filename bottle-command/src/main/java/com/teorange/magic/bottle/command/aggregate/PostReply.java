package com.teorange.magic.bottle.command.aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import cn.teorange.framework.core.utils.R;
import com.teorange.magic.bottle.api.command.DeleteSingleCommentCommand;
import com.teorange.magic.bottle.api.event.SingleCommentDeletedEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.EntityId;

/**
 * Created by kellen on 2018/5/24. 帖子回复
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@Slf4j
public class PostReply {

  /**
   * 回复id
   */
  @EntityId
  private Long replyId;


  private Long postId;
  /**
   * 回复用户id
   */
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
   * 被举报实体
   */
  private List<TipOffLog> tipOffLogList = new ArrayList<>();


  /**
   * 是否被屏蔽 0否 1是
   */
  private Integer shieldStatus;

  /**
   * 删除用户自己的某一条评论
   */
  @CommandHandler
  public R handle(DeleteSingleCommentCommand command) {
//    if (!fromUid.equals(command.getUserId())) {
//      return R.error("您没有权限删除此评论");
//    }
    apply(new SingleCommentDeletedEvent(command.getPostId(), command.getReplyId()));
    return R.ok();
  }


}
