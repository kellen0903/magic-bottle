package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.TagEntity;
import com.teorange.magic.bottle.api.mapper.TagMapper;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class TagService extends ServiceImpl<TagMapper, TagEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    String tagName = MapUtil.getStr(params, "tagName");
    Page<TagEntity> page = this.selectPage(
            new Query<TagEntity>(params).getPage(),
            new EntityWrapper<TagEntity>().like(StringUtils.isNotBlank(tagName), "tag_name", tagName)
                    .orderBy("order_num")
    );
    return new PageUtils(page);
  }
}
