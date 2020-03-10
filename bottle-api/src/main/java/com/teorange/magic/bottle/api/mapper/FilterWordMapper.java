package com.teorange.magic.bottle.api.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.teorange.magic.bottle.api.domain.FilterWordEntity;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 敏感词
 * 
 * @author cjj
 * @date 2018-05-31 18:01:10
 */
public interface FilterWordMapper extends BaseMapper<FilterWordEntity> {

    List<FilterWordEntity> queryPageList(Pagination page, @Param("ew") Wrapper<FilterWordEntity> wrapper);
}
