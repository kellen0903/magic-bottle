package com.teorange.magic.bottle.command.web;

import static com.teorange.magic.bottle.api.constant.GlobalConstant.FORBIDEN_MESSAGE;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.AddGroup;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.command.DeleteAllCommentCommand;
import com.teorange.magic.bottle.api.command.DeleteSingleCommentCommand;
import com.teorange.magic.bottle.api.command.ReplyCommand;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.dto.PostReplyDTO;
import com.teorange.magic.bottle.api.plugins.wordfilter.WordFilter;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.PostReplyService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/5/31.
 */
@RestController
@RequestMapping("/reply")
@AllArgsConstructor
@Slf4j
public class CommentController {

  private CommandGateway commandGateway;

  private PostReplyService postReplyService;

  private MagicUserService magicUserService;

  private WordFilter wordFilter;


  /**
   * 评论、回复帖子
   */
  @PostMapping("/add")
  @Login
  public R comment(@RequestBody PostReplyDTO replyDTO, @LoginUser MagicUserEntity userEntity) {
    ValidatorUtils.validateEntity(replyDTO, AddGroup.class);
    //用户是否被禁言
    if (2 == userEntity.getStatus()) {
      return R.error(FORBIDEN_MESSAGE);
    }
    Long replyId = IdWorker.getId();
    replyDTO.setFromUid(userEntity.getId());
    replyDTO.setFromNickName(userEntity.getNickName());
    replyDTO.setReplyId(replyId);
    replyDTO.setCreateTime(new Date());
    replyDTO.setFromSex(userEntity.getSex());
    if (null != replyDTO.getToUid()) {
      MagicUserEntity magicUserEntity = magicUserService.selectById(replyDTO.getToUid());
      if (null != magicUserEntity) {
        replyDTO.setToNickName(magicUserEntity.getNickName());
        replyDTO.setToSex(magicUserEntity.getSex());
        replyDTO.setToPushId(magicUserEntity.getJpushId());
      }
    }
    //敏感词过滤
    boolean isFilter = wordFilter.isContains(replyDTO.getContent());
    //已屏蔽
    if (isFilter) {
      replyDTO.setShieldStatus(1);
    }
    ReplyCommand replyCommand = new ReplyCommand(replyDTO.getPostId(), replyDTO);
    R r = commandGateway.sendAndWait(replyCommand);
    log.info("发表评论返回:{}", r);
    return r;
  }


  /**
   * 查看帖子回复列表
   */
  @PostMapping("/page")
  public R page(@RequestBody Map<String, Object> param) {
    //魔瓶id
    Long postId = MapUtil.getLong(param, "postId");
    if (Validator.isEmpty(postId)) {
      return R.error("魔瓶id不允许为空");
    }
    Integer page = MapUtil.getInt(param, "page");
    Integer limit = MapUtil.getInt(param, "limit");
    if (null == page) {
      param.put("page", "1");
    }
    if (null == limit) {
      param.put("limit", "100");
    }
    param.put("sidx", "create_time");
    param.put("shieldStatus", 0);
    param.put("order", "desc");
    param.put("deleted", 0);
    PageUtils pageData = postReplyService.queryPage(param);
    return R.ok().put("data", pageData);

  }


  /**
   * 删除帖子所有评论或者删除本评论
   */
  @PostMapping("/delete")
  @Login
  public R delete(@RequestBody PostReplyDTO postReplyDTO, @LoginUser MagicUserEntity userEntity) {
    Integer deleteType = postReplyDTO.getDeleteType();
    //删除自己某条评论
    if (1 == deleteType) {
      DeleteSingleCommentCommand command = new DeleteSingleCommentCommand(postReplyDTO.getPostId(),
          postReplyDTO.getReplyId(), userEntity.getId());
      return commandGateway.sendAndWait(command);
      //删除帖子下面所有回复
    }
    //删除帖子下的所有评论
    DeleteAllCommentCommand command = new DeleteAllCommentCommand(postReplyDTO.getPostId(),
        userEntity.getId());
    return commandGateway.sendAndWait(command);
  }


}
