package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.ScreenAdEntity;
import com.teorange.magic.bottle.api.mapper.ScreenAdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;


@Service("screenAdService")
public class ScreenAdService extends ServiceImpl<ScreenAdMapper, ScreenAdEntity> {

    public PageUtils queryPage(Map<String, Object> params) {
        String configKey = MapUtil.getStr(params, "configKey");
        Page<ScreenAdEntity> page = this.selectPage(
                new Query<ScreenAdEntity>(params).getPage(),
                new EntityWrapper<ScreenAdEntity>().like(StringUtils.isNotBlank(configKey), "config_key", configKey)
        );

        return new PageUtils(page);
    }

}
