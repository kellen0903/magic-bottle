package com.teorange.magic.bottle.command.web;

import static com.teorange.magic.bottle.api.constant.GlobalConstant.BLACK_CACHE_KEY;
import static com.teorange.magic.bottle.api.constant.GlobalConstant.CACHE_POST_CREATE_LIMIT_KEY;
import static com.teorange.magic.bottle.api.constant.GlobalConstant.FORBIDEN_MESSAGE;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.Assert;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.AddGroup;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.redis.utils.RedisCacheUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.command.AddPostCommand;
import com.teorange.magic.bottle.api.command.UpPostCommand;
import com.teorange.magic.bottle.api.constant.GlobalConstant;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.domain.UpLogEntity;
import com.teorange.magic.bottle.api.dto.BlackCache;
import com.teorange.magic.bottle.api.dto.PostDTO;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.plugins.wordfilter.WordFilter;
import com.teorange.magic.bottle.api.service.PostReplyService;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.api.service.SysConfigService;
import com.teorange.magic.bottle.api.service.UpLogService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import com.teorange.magic.bottle.command.service.TipOffService;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/5/28. 帖子控制器
 */
@RestController
@RequestMapping("/post")
@AllArgsConstructor
@Slf4j
public class PostController {


  private CommandGateway commandGateway;


  private PostService postService;

  private UpLogService upLogService;

  private PostReplyService postReplyService;

  private WordFilter wordFilter;

  private RedisCacheUtil redisCacheUtil;

  private SysConfigService sysConfigRedis;

  private TipOffService tipOffService;

  /**
   * 发表魔瓶
   */
  @PostMapping("/create")
  @Login
  public R create(@RequestBody PostDTO postDTO, @LoginUser MagicUserEntity magicUser) {
    log.info("发表魔瓶入参:{}", postDTO);
    ValidatorUtils.validateEntity(postDTO, AddGroup.class);
    Integer publishStatus = postDTO.getPublishStatus();
    //校验是否被禁言
    if (2 == magicUser.getStatus()) {
      return R.error(FORBIDEN_MESSAGE);
    }
    //只有发布到大海的魔瓶需要检验频率
    if (1 == publishStatus) {
      Boolean flag = redisCacheUtil
          .get(CACHE_POST_CREATE_LIMIT_KEY + magicUser.getId(), Boolean.class);
      if (null != flag && flag) {
        return R.error("3分钟之内最多分享一条到大海，请稍后再分享");
      }
    }
    String contecnt = postDTO.getContent();
    //敏感词过滤
    boolean isFilter = wordFilter.isContains(contecnt);
    //没有达到长度要求
    boolean isMinlength = false;
    String minLength = sysConfigRedis.getValue(GlobalConstant.POST_MIN_LENGTH);
    if (StrUtil.isNotEmpty(minLength) && NumberUtil.isNumber(minLength) && CollectionUtils
        .isEmpty(postDTO.getImagesList())) {
      isMinlength = contecnt.length() < Integer.valueOf(minLength);
    }
    if (!CollectionUtils.isEmpty(postDTO.getImagesList())) {
      List<String> imgList = postDTO.getImagesList();
      List<String> newImage = Lists.newArrayList();
      for (String s : imgList) {
        String image = s.replaceAll("p90fsuyqe.bkt.clouddn.com", "img.shaiquan.net");
        newImage.add(image);
      }
      postDTO.setImagesList(newImage);
    }
    //已屏蔽
    if (isFilter || isMinlength) {
      postDTO.setShieldStatus(1);
    } else {
      postDTO.setShieldStatus(0);
    }
    //魔瓶id
    Long postId = IdWorker.getId();
    postDTO.setPostId(postId);
    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(magicUser, userDTO);
    postDTO.setPostUser(userDTO);
    postDTO.setCreateTime(new Date());
    //星期取服务器时间
    postDTO.setWeek(DateUtil.thisDayOfWeekEnum().toChinese());
    postDTO.setLikeCount(0L);
    postDTO.setCommentCount(0L);
    AddPostCommand command = new AddPostCommand(postDTO);
    commandGateway.send(command);
    Long time = 180L;
    String timeLimit = sysConfigRedis.getValue(GlobalConstant.POST_TIME_LIMIT);
    if (StrUtil.isNotBlank(timeLimit)) {
      time = Long.valueOf(timeLimit);
    }
    if (1 == publishStatus) {
      redisCacheUtil
          .set(CACHE_POST_CREATE_LIMIT_KEY + magicUser.getId(), true, time, TimeUnit.SECONDS);
    }
    Map<String, Object> data = Maps.newHashMap();
    data.put("postId", postId);
    return R.ok().put("data", data);
  }


  /**
   * 分页查询魔瓶列表
   */
  @PostMapping("/page")
  public R page(@RequestBody Map<String, Object> param, @LoginUser MagicUserEntity magicUser) {
    log.info("查询魔瓶列表入参:{}", param);
    //时间倒序
    param.put("sidx", "create_time");
    param.put("order", "desc");
    param.put("deleted", 0);
    param.put("shieldStatus", 0);
    param.put("publishStatus", 1);
    param.put("isMy", false);
    if (null != magicUser) {
      param.put("userId", magicUser.getId());
      param.put("sourceType", magicUser.getSourceType());
      //查询被屏蔽的帖子和拉黑的名单
      BlackCache blackCache = redisCacheUtil
          .get(GlobalConstant.BLACK_CACHE_KEY + magicUser.getId(), BlackCache.class);
      if (null != blackCache) {
        param.put("shieldIds", blackCache.getPostIds());
        param.put("blackIds", blackCache.getTargetUids());
      }
    } else {
      param.put("sourceType", MapUtil.getInt(param, "sourceType"));
    }
    PageUtils page = postService.queryPage(param);
    if (null != magicUser) {
      this.buildUpData(page, magicUser.getId());
    }
    return R.ok().put("data", page);
  }


  /**
   * 查询魔瓶详情
   */
  @PostMapping("/query")
  public R query(@RequestBody PostDTO postDTO, @LoginUser MagicUserEntity magicUser) {
    PostEntity postEntity = postService.selectById(postDTO.getPostId());
    if (postEntity == null) {
      return R.ok("魔瓶不存在");
    }
    if (null != postEntity.getImages()) {
      postEntity.setImageList(JSON.parseArray(postEntity.getImages(), String.class));
    }
    //查询魔瓶回复列表
    List<PostReplyEntity> replyEntityList = postReplyService
        .selectList(new EntityWrapper<PostReplyEntity>().eq("post_id", postEntity.getId())
            .eq("deleted", 0).eq("shield_status", 0).orderBy("create_time", false));
    //构造评论昵称
    postService.buildReplyNickName(replyEntityList);
    //查询当前登陆用户的点赞状态
    if (null != magicUser) {
      //查询当前自己被屏蔽的回复
      List<PostReplyEntity> shieldList = postReplyService
          .selectList(new EntityWrapper<PostReplyEntity>().eq("post_id", postEntity.getId())
              .eq("deleted", 0).eq("shield_status", 1).eq("from_uid", magicUser.getId())
              .orderBy("create_time", false));
      if (!CollectionUtils.isEmpty(shieldList)) {
        replyEntityList = ListUtils.union(replyEntityList, shieldList);
        //排序
        Comparator<PostReplyEntity> comparator = Comparator
            .comparing(PostReplyEntity::getCreateTime);
        replyEntityList.sort(comparator.reversed());
      }
      EntityWrapper<UpLogEntity> entityWrapper = new EntityWrapper<>();
      entityWrapper
          .setEntity(new UpLogEntity().setUserId(magicUser.getId()).setPostId(postDTO.getPostId()));
      Integer count = upLogService.selectCount(entityWrapper);
      if (count > 0) {
        postEntity.setUp(true);
      }
    }
    postEntity.setReplyList(replyEntityList);
    return R.ok().put("data", postEntity);
  }


  /**
   * 点赞
   */
  @PostMapping("/up")
  @Login
  public R up(@RequestBody PostDTO postDTO, @LoginUser MagicUserEntity userEntity) {
    Long postId = postDTO.getPostId();
    Integer upType = postDTO.getUpType();
    if (Validator.isEmpty(postId)) {
      return R.error("魔瓶id不允许为空");
    }
    Boolean isUp = upLogService.isHasUp(userEntity.getId(), postId);
    if (isUp && 1 == upType) {
      return R.error("您已经点过赞了");
    }
    if (!isUp && 2 == upType) {
      return R.error("您还未点赞");
    }
    UserDTO upUser = new UserDTO();
    BeanUtils.copyProperties(userEntity, upUser);
    UpPostCommand command = new UpPostCommand(postId, userEntity.getId(), postDTO.getUpType(),
        upUser);
    commandGateway.send(command);
    return R.ok();
  }


  /**
   * 获取闪聊帖子列表  24小时异性发布的帖子 如果未登录 则随机获取50条
   */
  @PostMapping("/getQuickChatPost")
  public R getQuickChatPost(@LoginUser MagicUserEntity magicUserEntity) {
    log.info("获取闪聊帖子列表:{}", magicUserEntity);
    PageUtils page;
    if (null != magicUserEntity) {
      page = postService
          .getQuickChatPost(magicUserEntity.getSex(), magicUserEntity.getId());
      this.buildUpData(page, magicUserEntity.getId());
    } else {
      page = postService
          .getQuickChatPost(1, null);
    }
    log.info("获取闪聊帖子列表结果:{}", page.getList());
    return R.ok().put("data", page);
  }


  /**
   * 发布
   */
  @Login
  @PostMapping("/publish")
  public R publish(@LoginUser MagicUserEntity magicUserEntity, @RequestBody PostDTO postDTO) {
    Long postId = postDTO.getPostId();
    if (null == postId) {
      return R.error("魔瓶id不允许为空");
    }
    //校验是否被禁言
    if (2 == magicUserEntity.getStatus()) {
      return R.error(FORBIDEN_MESSAGE);
    }
    Boolean flag = redisCacheUtil
        .get(CACHE_POST_CREATE_LIMIT_KEY + magicUserEntity.getId(), Boolean.class);
    if (null != flag && flag) {
      return R.error("3分钟之内最多分享一条到大海，请稍后再分享");
    }
    PostEntity postEntity = postService.selectById(postId);
    if (null == postEntity || !postEntity.getUserId().equals(magicUserEntity.getId())) {
      return R.error("魔瓶不存在");
    }
    if (1 == postEntity.getPublishStatus()) {
      return R.error("该魔瓶已发布");
    }
    Long time = 180L;
    String timeLimit = sysConfigRedis.getValue(GlobalConstant.POST_TIME_LIMIT);
    if (StrUtil.isNotBlank(timeLimit)) {
      time = Long.valueOf(timeLimit);
    }
    postService.updateById(
        new PostEntity().setId(postId).setPublishStatus(1).setNickName(postDTO.getNickName()));
    redisCacheUtil
        .set(CACHE_POST_CREATE_LIMIT_KEY + magicUserEntity.getId(), true, time, TimeUnit.SECONDS);
    return R.ok();
  }


  /**
   * 删除魔瓶
   */
  @PostMapping("/delete")
  @Login
  public R delete(@LoginUser MagicUserEntity magicUserEntity, @RequestBody PostDTO postDTO) {
    Long postId = postDTO.getPostId();
    if (null == postId) {
      return R.error("魔瓶id不允许为空");
    }
    PostEntity postEntity = postService.selectById(postId);
    if (null == postEntity || !postEntity.getUserId().equals(magicUserEntity.getId())) {
      return R.error("魔瓶不存在");
    }
    postService.updateById(new PostEntity().setId(postId).setDeleted(1));
    return R.ok();
  }


  /**
   * 构造点赞数据
   */
  private void buildUpData(PageUtils page, Long userId) {
    List<PostEntity> postList = (List<PostEntity>) page.getList();
    if (CollectionUtils.isEmpty(postList)) {
      return;
    }
    //帖子id集合
    List<Long> postIds = postList.stream().map(PostEntity::getId).collect(Collectors.toList());
    //查询点赞
    List<UpLogEntity> upLogList = upLogService
        .selectList(new EntityWrapper<UpLogEntity>().eq("user_id", userId).in("post_id", postIds));
    if (CollectionUtils.isEmpty(upLogList)) {
      return;
    }
    Map<Long, UpLogEntity> upLogEntityMap = Maps.newHashMap();
    upLogList.forEach(e -> upLogEntityMap.put(e.getPostId(), e));
    postList.forEach(e -> {
      //增加帖子屏蔽和拉黑功能
      if (null != upLogEntityMap.get(e.getId())) {
        e.setUp(true);
      }
    });
    page.setList(postList);
  }


  /**
   * 查询魔瓶状态
   */
  @PostMapping("/queryPostStatus")
  @Login
  public R queryPostStatus(
      @RequestBody PostDTO post) {
    PostEntity postEntity = postService.selectById(post.getPostId());
    if (null == postEntity) {
      return R.ok("魔瓶不存在");
    }
    Map<String, Object> data = Maps.newHashMap();
    data.put("deleted", postEntity.getDeleted());
    data.put("shieldStatus", postEntity.getShieldStatus());
    data.put("publishStatus", postEntity.getPublishStatus());
    data.put("postUserId", postEntity.getUserId());
    data.put("tagName", postEntity.getTagName());
    data.put("createTime", postEntity.getCreateTime());
    data.put("content", postEntity.getContent());
    return R.ok().put("data", data);
  }


  /***
   * 屏蔽拉黑
   */
  @PostMapping("/addBlack")
  @Login
  public R addBlack(@RequestBody Map<String, Object> paramMap,
      @LoginUser MagicUserEntity magicUserEntity) {
    //1 屏蔽帖子 2加入黑名单
    Integer type = MapUtil.getInt(paramMap, "type");
    Long itemId = MapUtil.getLong(paramMap, "itemId");
    Assert.isNull(type, "类型不允许为空");
    //从缓存获取黑名单列表
    BlackCache blackCache = redisCacheUtil
        .get(BLACK_CACHE_KEY + magicUserEntity.getId(), BlackCache.class);
    if (null == blackCache) {
      blackCache = new BlackCache();
      Set<Long> ids = new HashSet<>();
      ids.add(itemId);
      if (1 == type) {
        blackCache.setPostIds(ids);
      } else {
        blackCache.setTargetUids(ids);
      }
    } else {
      Set<Long> ids;
      if (1 == type) {
        ids = blackCache.getPostIds();
        if (null == ids) {
          ids = new HashSet<>();
        }
        ids.add(itemId);
        blackCache.setPostIds(ids);
      } else {
        ids = blackCache.getTargetUids();
        if (null == ids) {
          ids = new HashSet<>();
        }
        ids.add(itemId);
        blackCache.setTargetUids(ids);
        //查询管理员列表，如果是管理员，直屏蔽则直接禁言
        List<String> adminList = sysConfigRedis
            .getConfigObject(GlobalConstant.ADMIN_USER_LIST, List.class);
        boolean isAdminFlag = adminList.stream()
            .anyMatch(e -> e.equals(magicUserEntity.getId().toString()));
        log.info("查询到的管理员id列表:{}，当前用户是否是管理员:{}", adminList, isAdminFlag);
        if (isAdminFlag) {
          tipOffService.doShut(itemId, magicUserEntity.getId(), 3, isAdminFlag);
        }
      }
    }
    redisCacheUtil.set(BLACK_CACHE_KEY + magicUserEntity.getId(), blackCache, null, null);
    return R.ok();
  }


}
