package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.FilterWordEntity;
import com.teorange.magic.bottle.api.mapper.FilterWordMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("filterWordService")
public class FilterWordService extends ServiceImpl<FilterWordMapper, FilterWordEntity> {

    public PageUtils queryPage(Map<String, Object> params) {
        Page<FilterWordEntity> page = this.selectPage(
                new Query<FilterWordEntity>(params).getPage(),
                new EntityWrapper<FilterWordEntity>()
        );

        return new PageUtils(page);
    }


    public PageUtils queryPageList(Map<String, Object> params) {

        String content = MapUtil.getStr(params, "content");

        //分页查询条件
        Page<FilterWordEntity> page = new Query<FilterWordEntity>(params).getPage();
        Wrapper<FilterWordEntity> wrapper = new EntityWrapper<FilterWordEntity>()
                .like(StringUtils.isNotBlank(content), "content", content);

        //封装分页
        List<FilterWordEntity> filterWordEntityList = baseMapper.queryPageList(page, wrapper);
        page.setRecords(filterWordEntityList);

        return new PageUtils(page);
    }


}
