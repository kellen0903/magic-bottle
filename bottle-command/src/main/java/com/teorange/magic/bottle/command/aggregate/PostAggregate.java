package com.teorange.magic.bottle.command.aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import cn.hutool.core.lang.Validator;
import cn.teorange.framework.core.utils.R;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.command.AddPostCommand;
import com.teorange.magic.bottle.api.command.DeleteAllCommentCommand;
import com.teorange.magic.bottle.api.command.ReplyCommand;
import com.teorange.magic.bottle.api.command.UpPostCommand;
import com.teorange.magic.bottle.api.dto.PostDTO;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.event.AllPostCommentDeletedEvent;
import com.teorange.magic.bottle.api.event.PostCreatedEvent;
import com.teorange.magic.bottle.api.event.PostReplyEvent;
import com.teorange.magic.bottle.api.event.PostUpedEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateMember;
import org.axonframework.commandhandling.model.ForwardMatchingInstances;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

/**
 * Created by kellen on 2018/5/21. 帖子聚合根
 */
@NoArgsConstructor
@Aggregate(repository = "postAggregateRepository")
@ToString
@Slf4j
@Getter
@Setter
public class PostAggregate {

  /**
   * 帖子id
   */
  @AggregateIdentifier
  private Long postId;

  /**
   * 帖子标题
   */
  private String title;

  /**
   * 发帖人
   */
  private UserDTO postUser;


  /**
   * 星期
   */
  private String week;


  /**
   * 温度  20-25
   */
  private String temperature;


  /**
   * 天气
   */
  private String weather;

  /**
   * 帖子状态 0：未发布 1：已发布
   */
  private Integer publishStatus;

  /**
   * 屏蔽状态 0：未屏蔽 1：已屏蔽
   */
  private Integer shieldStatus;


  /**
   * 是否允许评论  0：是 1：否
   */
  private Integer commentFlag;

  /**
   * 标签id
   */
  private Long tagId;

  /**
   * 标签昵称
   */
  private String tagName;

  /**
   * 评论数
   */
  private Long commentCount;

  /**
   * 点赞数
   */
  private Long likeCount;

  /**
   * 图片
   */
  private List<String> imagesList = new ArrayList<>();


  /**
   * 评论列表
   */
  @AggregateMember(eventForwardingMode = ForwardMatchingInstances.class)
  private List<PostReply> postReplyList = new ArrayList<>();


  /**
   * 点赞列表
   */
  private Set<Long> uplogSet = new HashSet<>();

  /**
   * 帖子内容
   */
  private String content;

  /**
   * 昵称
   */
  private String nickName;

  /**
   * 创建时间
   */
  private Date createTime;


  /**
   * 发表帖子
   */
  public PostAggregate(AddPostCommand command) {
    apply(new PostCreatedEvent(command.getPostDTO()));
  }


  @EventHandler
  public void on(PostCreatedEvent event) {
    BeanUtils.copyProperties(event.getPostDTO(), this);
  }


  @CommandHandler
  public void handle(UpPostCommand command) {
    PostDTO postDTO = new PostDTO();
    BeanUtils.copyProperties(this, postDTO);
    boolean existFlag = this.uplogSet.contains(command.getUserId());
    apply(new PostUpedEvent(command.getPostId(), command.getUserId(), command.getType(),
        this.postUser, postDTO, command.getUpUser(), existFlag));
  }


  @EventHandler
  public void on(PostUpedEvent event) {
    if (1 == event.getType()) {
      this.likeCount++;
      if (null == this.uplogSet) {
        this.uplogSet = new HashSet<>();
      }
      this.uplogSet.add(event.getUserId());
    } else if (2 == event.getType()) {
      this.likeCount--;
    }
  }


  @CommandHandler
  public R handle(ReplyCommand command) {
    if (1 == commentFlag) {
      return R.error("此魔瓶不允许评论");
    }
    //回复帖子
    if (Validator.isEmpty(command.getReplyDTO().getToUid())) {
      command.getReplyDTO().setToUid(this.postUser.getId());
      command.getReplyDTO().setToNickName(this.postUser.getNickName());
      command.getReplyDTO().setToPushId(this.postUser.getJpushId());
    }
    apply(new PostReplyEvent(command.getReplyDTO()));
    Map<String, Object> data = Maps.newHashMap();
    data.put("replyId", command.getReplyDTO().getReplyId());
    return R.ok().put("data", data);
  }


  @EventHandler
  public void on(PostReplyEvent event) {
    PostReply postReply = new PostReply();
    BeanUtils.copyProperties(event.getReplyDTO(), postReply);
    this.postReplyList.add(postReply);
    this.commentCount++;
  }


  @CommandHandler
  public R handle(DeleteAllCommentCommand command) {
//    if (!this.postUser.getId().equals(command.getUserId())) {
//      return R.error("您没有权限");
//    }
    apply(new AllPostCommentDeletedEvent(this.postId, command.getUserId()));
    return R.ok();
  }


  @EventHandler
  public void on(AllPostCommentDeletedEvent event) {
    this.postReplyList.clear();
  }
}
