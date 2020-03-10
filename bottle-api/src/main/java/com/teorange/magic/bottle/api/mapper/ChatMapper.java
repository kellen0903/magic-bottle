package com.teorange.magic.bottle.api.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by kellen on 2018/6/10.
 */

public interface ChatMapper extends BaseMapper<ChatEntity> {

    List<Map> goupByCount(@Param("countType") Integer countType);

}
