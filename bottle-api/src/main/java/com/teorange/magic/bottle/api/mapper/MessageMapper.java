package com.teorange.magic.bottle.api.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 消息表
 *
 * Created by kellen on 2018-05-28 22:03:30
 */
public interface MessageMapper extends BaseMapper<MessageEntity> {

    List<MessageEntity> queryPageList(Pagination page,  @Param("ew") Wrapper<MessageEntity> wrapper);

}
