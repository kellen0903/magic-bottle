package com.teorange.magic.bottle.api.service;

import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.UpLogEntity;
import com.teorange.magic.bottle.api.mapper.UpLogMapper;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class UpLogService extends ServiceImpl<UpLogMapper, UpLogEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Page<UpLogEntity> page = this.selectPage(
        new Query<UpLogEntity>(params).getPage(),
        new EntityWrapper<UpLogEntity>()
    );
    return new PageUtils(page);
  }


  /**
   * 查询是否点过赞
   */
  public boolean isHasUp(Long userId, Long postId) {
    //是否点过赞
    EntityWrapper<UpLogEntity> entityWrapper = new EntityWrapper<>();
    UpLogEntity entity = new UpLogEntity();
    entity.setPostId(postId);
    entity.setUserId(userId);
    entityWrapper.setEntity(entity);
    return this.selectCount(entityWrapper) > 0;
  }

}
