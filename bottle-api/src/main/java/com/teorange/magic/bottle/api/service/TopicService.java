package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.TopicEntity;
import com.teorange.magic.bottle.api.mapper.TopicMapper;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class TopicService extends ServiceImpl<TopicMapper, TopicEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Long tagId = MapUtil.getLong(params, "tagId");
    Page<TopicEntity> page = this.selectPage(
        new Query<TopicEntity>(params).getPage(),
        new EntityWrapper<TopicEntity>().eq(null != tagId, "tag_id", tagId)
    );
    return new PageUtils(page);
  }
}
