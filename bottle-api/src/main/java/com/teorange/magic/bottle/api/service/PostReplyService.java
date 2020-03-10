package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.mapper.PostReplyMapper;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service("postReplyService")
public class PostReplyService extends ServiceImpl<PostReplyMapper, PostReplyEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Integer deleted = MapUtil.getInt(params, "deleted");
    Integer shieldStatus = MapUtil.getInt(params, "shieldStatus");
    Long id = MapUtil.getLong(params, "id");
    Long postId = MapUtil.getLong(params, "postId");
    Page<PostReplyEntity> page = this.selectPage(
        new Query<PostReplyEntity>(params).getPage(),
        new EntityWrapper<PostReplyEntity>().eq(postId != null, "post_id", params.get("postId"))
            .eq(deleted != null, "deleted", deleted).eq(id != null, "id", id)
            .eq(shieldStatus != null, "shield_status", shieldStatus)
            .orderBy("create_time", false)
    );
    return new PageUtils(page);
  }

}
