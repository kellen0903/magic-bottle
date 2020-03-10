package com.teorange.magic.bottle.api.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 用户表
 *
 * Created by kellen on 2018-05-20 22:59:11
 */
public interface MagicUserMapper extends BaseMapper<MagicUserEntity> {

  Integer selectCountByNickName(@Param("nickName") String nickName);

  List<Map> goupByCount(@Param("countType") Integer countType);

}
