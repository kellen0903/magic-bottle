package com.teorange.magic.bottle.api.service;

import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.LoginLogEntity;
import com.teorange.magic.bottle.api.mapper.LoginLogMapper;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class LoginLogService extends ServiceImpl<LoginLogMapper, LoginLogEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Page<LoginLogEntity> page = this.selectPage(
        new Query<LoginLogEntity>(params).getPage(),
        new EntityWrapper<>()
    );

    return new PageUtils(page);
  }

}
