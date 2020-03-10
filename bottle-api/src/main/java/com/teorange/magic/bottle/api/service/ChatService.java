package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import com.teorange.magic.bottle.api.mapper.ChatMapper;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;


@Service
public class ChatService extends ServiceImpl<ChatMapper, ChatEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Long chatId = MapUtils.getLong(params, "chatId");
    Long userId = MapUtils.getLong(params, "userId");
    Long postId = MapUtils.getLong(params, "postId");
    Integer status = MapUtil.getInt(params, "status");
    //分页查询条件
    Page<ChatEntity> page = new Query<ChatEntity>(params).getPage();
    Wrapper<ChatEntity> wrapper = new EntityWrapper<ChatEntity>()
        .eq(null != status, "status", status)
        .eq(null != chatId, "chat_id", chatId)
        .eq(null != postId, "post_id", postId)
        .eq(null != userId, "from_uid", userId)
        .or(null != userId, "to_uid={0}", userId)
        .orderBy("create_time", false);

    //分装分页
    List<ChatEntity> chatEntityList = baseMapper.selectPage(page, wrapper);
    page.setRecords(chatEntityList);

    return new PageUtils(page);
  }

  /**
   * 天、周、月、年统计注册数
   */
  public List<Map> goupByCount(Integer countType) {

    return this.baseMapper.goupByCount(countType);
  }

}
