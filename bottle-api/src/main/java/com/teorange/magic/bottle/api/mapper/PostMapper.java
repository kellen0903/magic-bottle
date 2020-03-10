package com.teorange.magic.bottle.api.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.teorange.magic.bottle.api.domain.PostEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;


/**
 * 帖子（魔瓶表）
 *
 * Created by kellen on 2018-05-28 22:03:30
 */
public interface PostMapper extends BaseMapper<PostEntity> {

  List<PostEntity> queryPageList(Pagination page, @Param("ew") Wrapper<PostEntity> wrapper);


  List<PostEntity> queryPostByFront(Pagination page, @Param("param") Map<String, Object> param);

  List<Map> goupByCount(@Param("countType") Integer countType);
}
