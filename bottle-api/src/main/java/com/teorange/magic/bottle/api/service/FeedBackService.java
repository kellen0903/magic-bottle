package com.teorange.magic.bottle.api.service;

import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.FeedBackEntity;
import com.teorange.magic.bottle.api.mapper.FeedBackMapper;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class FeedBackService extends ServiceImpl<FeedBackMapper, FeedBackEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Page<FeedBackEntity> page = this.selectPage(
        new Query<FeedBackEntity>(params).getPage(),
        new EntityWrapper<>()
    );

    return new PageUtils(page);
  }

}
