package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.AppVersionEntity;
import com.teorange.magic.bottle.api.mapper.AppVersionMapper;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service
public class AppVersionService extends ServiceImpl<AppVersionMapper, AppVersionEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    String osType = MapUtil.getStr(params, "osType");
    Page<AppVersionEntity> page = this.selectPage(
        new Query<AppVersionEntity>(params).getPage(),
        new EntityWrapper<AppVersionEntity>().like(StringUtils.isNotBlank(osType),"os_Type", osType)
    );

    return new PageUtils(page);
  }

}
