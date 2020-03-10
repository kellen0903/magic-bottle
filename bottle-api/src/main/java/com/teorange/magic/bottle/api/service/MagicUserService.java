package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.mapper.MagicUserMapper;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class MagicUserService extends ServiceImpl<MagicUserMapper, MagicUserEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    String id = MapUtil.getStr(params, "id");
    String nickName = MapUtil.getStr(params, "nickName");
    Integer status = MapUtil.getInt(params, "status");
    String imei = MapUtil.getStr(params, "imei");
    Page<MagicUserEntity> page = this.selectPage(
        new Query<MagicUserEntity>(params).getPage(),
        new EntityWrapper<MagicUserEntity>()
            .like(null != id, "id", id)
            .eq(null != status, "status", status)
            .eq(StrUtil.isNotEmpty(imei), "imei", imei)
            .like(StrUtil.isNotEmpty(nickName), "nick_name", nickName)
    );
    return new PageUtils(page);
  }


  /**
   * 条件查询单个
   */
  public MagicUserEntity query(MagicUserEntity entity) {
    EntityWrapper<MagicUserEntity> wrapper = new EntityWrapper<>();
    wrapper.setEntity(entity);
    return this.selectOne(wrapper);
  }


  /**
   * 条件查询单个
   */
  public Boolean checkNickName(String nickName) {
    Integer count = this.baseMapper.selectCountByNickName(nickName);
    return count > 0;
  }

  /**
   * 天、周、月、年统计注册数
   */
  public List<Map> goupByCount(Integer countType) {

    return this.baseMapper.goupByCount(countType);
  }

}
