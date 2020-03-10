package com.teorange.magic.bottle.command.web;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.HashUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.core.utils.IPUtils;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.Assert;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.UpdateGroup;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.domain.ChatCollectEntity;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
import com.teorange.magic.bottle.api.dto.ChatDTO;
import com.teorange.magic.bottle.api.dto.PushMessageDTO;
import com.teorange.magic.bottle.api.service.ChatCollectService;
import com.teorange.magic.bottle.api.service.ChatService;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.MessagePushService;
import com.teorange.magic.bottle.api.service.MessageService;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import com.teorange.magic.bottle.command.service.TipOffService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/6/13.
 */
@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {


  private ChatService chatService;

  private MessagePushService messagePushService;

  private ThreadPoolExecutor messageThreadPool;

  private MagicUserService magicUserService;

  private TipOffService tipOffService;

  private PostService postService;

  private ChatCollectService chatCollectService;

  private MessageService messageService;

  /**
   * 更新操作  打开对话 关闭对话  举报对话
   */
  @PostMapping("/update")
  @Login
  public R update(@RequestBody ChatDTO chatDTO, @LoginUser MagicUserEntity magicUserEntity,
      HttpServletRequest request) {
    //参数校验
    ValidatorUtils.validateEntity(chatDTO, UpdateGroup.class);
    Integer status = chatDTO.getStatus();
    //0：正常 1：关闭  2：举报
    //查询会话
    ChatEntity chatEntity = chatService.selectById(chatDTO.getChatId());
    if (null == chatEntity) {
      return R.error("会话不存在");
    }
    switch (status) {
      //打开对话
      case 0:
        if (chatEntity.getStatus() != 1) {
          return R.error("当前会话不允许打开");
        }
        if (!magicUserEntity.getId().equals(chatEntity.getHandleUid())) {
          return R.error("非法操作");
        }
        chatEntity = new ChatEntity().setChatId(chatEntity.getChatId()).setStatus(0)
            .setUpdateTime(new Date());
        chatService.updateById(chatEntity);
        break;
      //关闭会话
      case 1:
        if (chatEntity.getStatus() != 0) {
          return R.error("当前会话不允许关闭");
        }
        chatEntity = new ChatEntity()
            .setChatId(chatEntity.getChatId())
            .setStatus(1).setHandleUid(magicUserEntity.getId())
            .setHandleNickName(chatDTO.getFromNickName())
            .setUpdateTime(new Date());
        chatService.updateById(chatEntity);
        break;
      case 2:
        //举报
        if (chatEntity.getStatus() != 0) {
          return R.error("当前会话不允许举报");
        }
        chatEntity = new ChatEntity()
            .setChatId(chatEntity.getChatId())
            .setStatus(2).setHandleUid(magicUserEntity.getId())
            .setHandleNickName(chatDTO.getFromNickName())
            .setUpdateTime(new Date());
        chatService.updateById(chatEntity);
        Integer count = tipOffService.selectCount(
            new EntityWrapper<TipOffLogEntity>().eq("user_id", magicUserEntity.getId())
                .eq("to_user_id", chatDTO.getToUid()).eq("item_id", chatEntity.getChatId()));
        //一个用户对同一用户的会话只能举报一次
        if (count <= 0) {
          //添加举报记录
          String ip = IPUtils.getIpAddr(request);
          TipOffLogEntity entity = new TipOffLogEntity().setItemId(chatEntity.getChatId())
              .setCreateTime(new Date()).setUserId(magicUserEntity.getId()).setItemType(3).setIp(ip)
              .setToUserId(chatDTO.getToUid());
          boolean flag = tipOffService.insert(entity);
          if (flag) {
            messageThreadPool.execute(() -> tipOffService.postTipOff(entity));
          }
        }
        break;
      default:
        break;
    }
    //发送消息
    messageThreadPool.execute(() -> this.sendChatUpdateMsg(chatDTO, magicUserEntity));
    return R.ok();
  }


  /**
   * 查询单个
   */
  @PostMapping("/query")
  @Login
  public R query(@RequestBody ChatDTO chatDTO, @LoginUser MagicUserEntity magicUserEntity) {
    ChatEntity chatEntity;
    if (null != chatDTO.getChatId()) {
      chatEntity = chatService.selectById(chatDTO.getChatId());
    } else {
      Long fromUid = chatDTO.getFromUid();
      Long toUid = chatDTO.getToUid();
      Long postId = chatDTO.getPostId();
      String chatUid = fromUid > toUid ? (toUid.toString() + fromUid.toString() + postId.toString())
          : (fromUid.toString() + toUid.toString() + postId.toString());
      Long chatId = (long) HashUtil.fnvHash(chatUid);
      chatEntity = chatService.selectById(chatId);
      if (null == chatEntity) {
        chatEntity = new ChatEntity().setChatId(chatId).setStatus(0).setCreateTime(new Date());
      }
      Long userId = magicUserEntity.getId();
      if (userId.equals(fromUid)) {
        chatEntity.setFromSex(magicUserEntity.getSex());
        chatEntity.setToSex(magicUserService.selectById(toUid).getSex());
      } else {
        chatEntity.setToSex(magicUserEntity.getSex());
        chatEntity.setFromSex(magicUserService.selectById(fromUid).getSex());
      }
    }
    return R.ok().put("data", chatEntity);
  }


  /**
   * 发送对话状态变更消息
   */
  private void sendChatUpdateMsg(ChatDTO chatDTO, MagicUserEntity magicUserEntity) {
    //0：正常 1：关闭  2：举报
    Integer status = chatDTO.getStatus();
    //1 文字 2图片 3评论通知  4点赞通知  5会话关闭  6会话举报 7会话开启
    Integer msgType;
    String content;
    if (0 == status) {
      msgType = 7;
      content = chatDTO.getFromNickName() + "重新开启了对话";
    } else if (1 == status) {
      msgType = 5;
      content = chatDTO.getFromNickName() + "关闭了对话";
    } else {
      msgType = 6;
      content = chatDTO.getFromNickName() + "举报了对话";
    }
    PushMessageDTO pushMessageDTO = new PushMessageDTO().setFromUid(magicUserEntity.getId())
        .setFromNickName(chatDTO.getFromNickName()).setToUid(chatDTO.getToUid())
        .setToNickName(chatDTO.getToNickName()).setMsgId(IdWorker.getId())
        .setPostId(chatDTO.getPostId()).setTagName(chatDTO.getTagName()).setContent(content)
        .setMsgType(msgType).setChatId(chatDTO.getChatId()).setCreateTime(new Date());
    MagicUserEntity toUserInfo = magicUserService.selectById(chatDTO.getToUid());
    if (null != toUserInfo) {
      pushMessageDTO.setJpushId(toUserInfo.getJpushId());
    }
    messagePushService.pushIM(pushMessageDTO);
  }


  /**
   * 同步会话列表，只同步最近30条的
   */
  @PostMapping("/syncChat")
  @Login
  public R syncChat(@LoginUser MagicUserEntity magicUserEntity) {
    log.info("开始同步会话列表");
    Map<String, Object> paraMap = Maps.newHashMap();
    paraMap.put("page", "1");
    paraMap.put("limit", "30");
    paraMap.put("status", "0");
    paraMap.put("userId", magicUserEntity.getId());
    List<ChatEntity> filterList = Lists.newArrayList();
    List<ChatEntity> result = Lists.newArrayList();
    PageUtils page = chatService.queryPage(paraMap);
    //去掉删除的会话
    List<ChatEntity> chatEntityList = (List<ChatEntity>) page.getList();
    if (!CollectionUtils.isEmpty(chatEntityList)) {
      chatEntityList.forEach(e -> {
        if ((e.getFromUid().equals(magicUserEntity.getId()) && e.getFromDeleted() == 0)
            || (e.getToUid().equals(magicUserEntity.getId()) && e.getToDeleted() == 0)) {
          if (e.getFromUid().equals(magicUserEntity.getId())) {
            e.setToNickName(e.getToNickName());
          } else if (e.getToUid().equals(magicUserEntity.getId())) {
            e.setToNickName(e.getFromNickName());
          }
          //是否有收藏
          Integer collectStatus = chatCollectService.selectCount(
              new EntityWrapper<ChatCollectEntity>().eq("chat_id", e.getChatId())
                  .eq("user_id", magicUserEntity.getId()));
          e.setCollectStatus(collectStatus > 0 ? 1 : 0);
          filterList.add(e);
        }
      });
      List<Long> postIds = filterList.stream().map(ChatEntity::getPostId)
          .collect(Collectors.toList());
      List<PostEntity> postEntityList = postService.selectBatchIds(postIds);
      if (!CollectionUtils.isEmpty(postEntityList)) {
        Map<Long, PostEntity> postEntityMap = Maps.newHashMap();
        postEntityList.forEach(e -> postEntityMap.put(e.getId(), e));
        filterList.forEach(e -> {
          PostEntity postEntity = postEntityMap.get(e.getPostId());
          if (null != postEntity && 0 == postEntity.getDeleted()) {
            e.setTagName(postEntity.getTagName());
            result.add(e);
          }
        });
      }
      page.setList(result);
    }
    return R.ok().put("data", page);
  }


  /**
   * 删除会话
   */
  @PostMapping("/delete")
  @Login
  public R delete(@RequestBody ChatDTO chatDTO, @LoginUser MagicUserEntity magicUserEntity) {
    log.info("开始删除会话，会话id:{}", chatDTO.getChatId());
    //查询会话
    ChatEntity chatEntity = chatService.selectById(chatDTO.getChatId());
    if (null == chatEntity) {
      return R.error("会话不存在");
    }
    Long fromUid = chatEntity.getFromUid();
    if (magicUserEntity.getId().equals(fromUid)) {
      chatService.updateById(new ChatEntity().setChatId(chatDTO.getChatId()).setFromDeleted(1));
    } else {
      chatService.updateById(new ChatEntity().setChatId(chatDTO.getChatId()).setToDeleted(1));
    }
    return R.ok();
  }


  /**
   * 收藏会话
   */
  @PostMapping("/addCollect")
  @Login
  public R addCollect(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody ChatCollectEntity chatCollectDTO) {
    Long chatId = chatCollectDTO.getChatId();
    if (null == chatId) {
      return R.error("会话id不允许为空");
    }
    ChatCollectEntity chatCollectEntity = new ChatCollectEntity()
        .setChatId(chatCollectDTO.getChatId()).setUserId(magicUserEntity.getId())
        .setCreatedBy(magicUserEntity.getNickName()).setCreateTime(new Date());
    chatCollectService.insert(chatCollectEntity);
    return R.ok();
  }


  /**
   * 取消收藏会话
   */
  @PostMapping("/cancelCollect")
  @Login
  public R cancelCollect(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody ChatCollectEntity chatCollectDTO) {
    Long chatId = chatCollectDTO.getId();
    if (null == chatId) {
      return R.error("会话id不允许为空");
    }
    //查询收藏
    Integer count = chatCollectService
        .selectCount(new EntityWrapper<ChatCollectEntity>().eq("chat_id", chatId)
            .eq("user_id", magicUserEntity.getId()));
    if (count <= 0) {
      return R.error("您未收藏该会话");
    }
    //取消收藏
    chatCollectService.delete(new EntityWrapper<ChatCollectEntity>().eq("chat_id", chatId)
        .eq("user_id", magicUserEntity.getId()));
    return R.ok();
  }


  /**
   * 我的收藏列表
   */
  @PostMapping("/collectList")
  @Login
  public R collectList(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody Map<String, Object> param) {
    param.put("sidx", "create_time");
    param.put("order", "desc");
    param.put("userId", magicUserEntity.getId());
    PageUtils page = chatCollectService.queryPage(param);
    return R.ok().put("data", page);
  }


  /**
   * 查询会话对方是否有回复
   */
  @PostMapping("/chatReplyQuery")
  @Login
  public R chatReplyQuery(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody Map<String, Object> param) {
    Long postId = MapUtil.getLong(param, "postId");
    //对方id
    Long uid = MapUtil.getLong(param, "uid");
    Assert.isNull(postId, "魔瓶id不允许为空");
    Assert.isNull(uid, "uid不允许为空");
    Integer count = messageService.selectCount(new EntityWrapper<MessageEntity>()
        .eq("post_id", postId).eq("to_uid", magicUserEntity.getId()).eq("from_uid", uid)
    );
    //是否回复 0未回复 1已回复
    String isReply = "0";
    if (count > 0) {
      isReply = "1";
    }
    Map<String, Object> data = Maps.newHashMap();
    data.put("isReply", isReply);
    return R.ok().put("data", data);
  }


}
