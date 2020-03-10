package com.teorange.magic.bottle.api.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.mapper.PostMapper;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service("postService")
@AllArgsConstructor
public class PostService extends ServiceImpl<PostMapper, PostEntity> {

  private PostReplyService replyService;

  private MagicUserService magicUserService;

  public PageUtils queryPage(Map<String, Object> params) {
    Long topicId = MapUtil.getLong(params, "topicId");
    Long tagId = MapUtil.getLong(params, "tagId");
    Long userId = MapUtil.getLong(params, "userId");
    Integer deleted = MapUtil.getInt(params, "deleted");
    String nickName = MapUtil.getStr(params, "nickName");
    Integer shieldStatus = MapUtil.getInt(params, "shieldStatus");
    Integer publishStatus = MapUtil.getInt(params, "publishStatus");
    Boolean isMy = MapUtil.getBool(params, "isMy");
    //屏蔽
    Set<Long> shieldIds = MapUtil.get(params, "shieldIds", Set.class);
    //黑名单
    Set<Long> blackIds = MapUtil.get(params, "blackIds", Set.class);
    Page<PostEntity> page = this.selectPage(
        new Query<PostEntity>(params).getPage(),
        new EntityWrapper<PostEntity>()
            .eq(null != tagId, "tag_id", tagId)
            .eq(null != topicId, "topic_id", topicId)
            .eq(null != userId && isMy, "user_id", userId)
            .eq(null != deleted, "deleted", deleted)
            .eq(publishStatus != null, "publish_status", publishStatus)
            .eq(shieldStatus != null, "shield_status", shieldStatus)
            .like(StringUtils.isNotBlank(nickName), "nick_name", nickName)
            .eq("os_type", 0)
            .notIn(!CollectionUtils.isEmpty(shieldIds), "id", shieldIds)
            .notIn(!CollectionUtils.isEmpty(blackIds), "user_id", blackIds));
    List<PostEntity> postEntityList = page.getRecords();
    List<PostEntity> upList = Lists.newArrayList();
    if (null != userId && !isMy) {
      //查询自己被屏蔽的
      List<PostEntity> shieldList = this
          .selectList(new EntityWrapper<PostEntity>().eq("user_id", userId).eq("shield_status", 1)
              .eq("deleted", 0)
              .eq("publish_status", 1)
              .eq(null != topicId, "topic_id", topicId)
              .eq(null != tagId, "tag_id", tagId));
      if (!CollectionUtils.isEmpty(shieldList)) {
        postEntityList = ListUtils.union(postEntityList, shieldList);
        //排序
        Comparator<PostEntity> comparator = Comparator.comparing(PostEntity::getCreateTime);
        postEntityList.sort(comparator.reversed());
        page.setTotal(page.getTotal() + shieldList.size() + upList.size());
        page.setRecords(postEntityList);
      }
    }
    //查询置顶帖子
    Integer osType = MapUtil.getInt(params, "sourceType");
    if (null != osType) {
      upList = this
          .selectList(new EntityWrapper<PostEntity>().eq("os_type", osType).eq("deleted", 0)
              .eq(null != tagId, "tag_id", tagId));
      if (!CollectionUtils.isEmpty(upList)) {
        postEntityList.addAll(0, upList);
        page.setTotal(page.getTotal() + upList.size());
        page.setRecords(postEntityList);
      }
    }
    return new PageUtils(page);
  }

  public PageUtils queryPageList(Map<String, Object> params) {
    Integer osType = MapUtil.getInt(params, "osType");
    Long userId = MapUtil.getLong(params, "userId");
    Long id = MapUtil.getLong(params, "id");
    Long tagId = MapUtil.getLong(params, "tagId");
    Integer shieldStatus = MapUtil.getInt(params, "shieldStatus");
    String content = MapUtil.getStr(params, "content");
    //分页查询条件
    Page<PostEntity> page = new Query<PostEntity>(params).getPage();
    Wrapper<PostEntity> wrapper = new EntityWrapper<PostEntity>()
        .where(null != id, "post.id ={0}", id)
        .eq(null != userId, "post.user_id", userId)
        .eq(null != tagId, "tag_id", tagId)
        .eq(null != shieldStatus, "shield_status", shieldStatus)
        .like(StrUtil.isNotEmpty(content), "content", content, SqlLike.DEFAULT)
        .in("os_type", osType != 0 ? new Integer[]{1, 2} : new Integer[]{osType})
        .orderBy("create_time", false);

    //分装分页
    List<PostEntity> postEntities = baseMapper.queryPageList(page, wrapper);
    postEntities.stream().forEach(x -> {
      if (null != x.getImages()) {
        x.setImageList(JSON.parseArray(x.getImages(), String.class));
      }
    });
    page.setRecords(postEntities);

    return new PageUtils(page);
  }


  /**
   * 闪聊获取24小时异性发布的帖子
   */
  public PageUtils getQuickChatPost(Integer sex, Long userId) {
    Map<String, Object> param = Maps.newHashMap();
    param.put("page", 1);
    param.put("limit", 50);
    param.put("sidx", "create_time");
    param.put("order", "desc");
    Date endTime = new Date();
    Date startTime = DateUtil.offsetHour(endTime, -24);
    //异性
    Integer targetSex;
    if (null != sex) {
      targetSex = sex == 1 ? 2 : 1;
    } else {
      targetSex = 2;
    }
    Page<PostEntity> page = this.selectPage(
        new Query<PostEntity>(param).getPage(),
        new EntityWrapper<PostEntity>().eq("deleted", 0).eq("publish_status", 1)
            .between("create_time", startTime, endTime)
            .eq("sex", targetSex)
            .eq("shield_status", 0)
            .eq("os_type", 0)
    );
    List<PostEntity> postEntityList = page.getRecords();
    if (!CollectionUtils.isEmpty(postEntityList) && userId != null) {
      //屏蔽自己发的帖子
      postEntityList = postEntityList.stream().filter(e -> !e.getUserId().equals(userId))
          .collect(Collectors.toList());
      this.generateReply(postEntityList);
      page.setRecords(postEntityList);
    }
    return new PageUtils(page);
  }


  /**
   * 构造回复
   */
  private void generateReply(List<PostEntity> postEntityList) {
    if (!CollectionUtils.isEmpty(postEntityList)) {
      //查询评论
      List<Long> postIds = postEntityList.stream().map(PostEntity::getId)
          .collect(Collectors.toList());
      List<PostReplyEntity> postReplyList = replyService.selectList(
          new EntityWrapper<PostReplyEntity>().in("post_id", postIds).eq("deleted", 0)
              .eq("shield_status", 0)
              .orderBy("create_time", false));
      if (!CollectionUtils.isEmpty(postReplyList)) {
        this.buildReplyNickName(postReplyList);
        Map<Long, List<PostReplyEntity>> postReplyEntityMap = postReplyList.stream()
            .collect(Collectors.groupingBy(PostReplyEntity::getPostId));
        postEntityList.forEach(e -> e.setReplyList(postReplyEntityMap.get(e.getId())));
      }
    }
  }


  /**
   * 构造评论昵称
   */
  public void buildReplyNickName(List<PostReplyEntity> replyEntityList) {
    Map<Long, String> fromUserNickNameMap = Maps.newHashMap();
    Map<Long, String> toUserNickNameMap = Maps.newHashMap();
    if (!CollectionUtils.isEmpty(replyEntityList)) {
      List<Long> fromUids = replyEntityList.stream().map(PostReplyEntity::getFromUid)
          .collect(Collectors.toList());
      List<Long> toUids = replyEntityList.stream().map(PostReplyEntity::getToUid)
          .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(fromUids)) {
        List<MagicUserEntity> fromUsers = magicUserService.selectBatchIds(fromUids);
        fromUsers.forEach(e -> fromUserNickNameMap.put(e.getId(), e.getNickName()));
      }
      if (!CollectionUtils.isEmpty(toUids)) {
        List<MagicUserEntity> toUsers = magicUserService.selectBatchIds(toUids);
        toUsers.forEach(e -> toUserNickNameMap.put(e.getId(), e.getNickName()));
      }
      replyEntityList.forEach(e -> {
        e.setFromNickName(fromUserNickNameMap.get(e.getFromUid()));
        e.setToNickName(toUserNickNameMap.get(e.getToUid()));
      });
    }
  }

  /**
   * 天、周、月、年统计注册数
   */
  public List<Map> goupByCount(Integer countType) {

    return this.baseMapper.goupByCount(countType);
  }
}

