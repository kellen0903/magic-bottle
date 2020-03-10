package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.ChatCollectEntity;
import com.teorange.magic.bottle.api.mapper.ChatCollectMapper;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class ChatCollectService extends ServiceImpl<ChatCollectMapper, ChatCollectEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Long userId = MapUtil.getLong(params, "userId");
    Page<ChatCollectEntity> page = this.selectPage(
        new Query<ChatCollectEntity>(params).getPage(),
        new EntityWrapper<ChatCollectEntity>().eq(null != userId, "user_id", userId)
    );
    return new PageUtils(page);
  }
}
