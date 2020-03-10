/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.api.service;

import cn.teorange.framework.core.exception.RRException;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.teorange.magic.bottle.api.domain.SysConfigEntity;
import com.teorange.magic.bottle.api.dto.SysConfigDTO;
import com.teorange.magic.bottle.api.mapper.SysConfigDao;
import com.teorange.magic.bottle.api.plugins.redis.SysConfigRedis;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfigEntity> implements
    SysConfigService {

  @Autowired
  private SysConfigRedis sysConfigRedis;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    String key = (String) params.get("key");

    Page<SysConfigEntity> page = this.selectPage(
        new Query<SysConfigEntity>(params).getPage(),
        new EntityWrapper<SysConfigEntity>()
            .like(StringUtils.isNotBlank(key), "key", key)
            .eq("status", 1)
    );

    return new PageUtils(page);
  }

  @Override
  public void save(SysConfigEntity config) {
    this.insert(config);
    SysConfigDTO sysConfigDTO = new SysConfigDTO();
    BeanUtils.copyProperties(config, sysConfigDTO);
    sysConfigRedis.saveOrUpdate(sysConfigDTO);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void update(SysConfigEntity config) {
    this.updateById(config);
    SysConfigDTO sysConfigDTO = new SysConfigDTO();
    BeanUtils.copyProperties(config, sysConfigDTO);
    sysConfigRedis.saveOrUpdate(sysConfigDTO);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateValueByKey(String key, String value) {
    baseMapper.updateValueByKey(key, value);
    sysConfigRedis.delete(key);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(Long[] ids) {
    for (Long id : ids) {
      SysConfigEntity config = this.selectById(id);
      sysConfigRedis.delete(config.getKey());
    }

    this.deleteBatchIds(Arrays.asList(ids));
  }

  @Override
  public String getValue(String key) {
    String value = sysConfigRedis.get(key);
    if (value == null) {
      SysConfigEntity config = baseMapper.queryByKey(key);
      SysConfigDTO sysConfigDTO = new SysConfigDTO();
      BeanUtils.copyProperties(config, sysConfigDTO);
      sysConfigRedis.saveOrUpdate(sysConfigDTO);
      value = config.getValue();
    }
    return value;
  }

  @Override
  public <T> T getConfigObject(String key, Class<T> clazz) {
    String value = getValue(key);
    if (StringUtils.isNotBlank(value)) {
      return new Gson().fromJson(value, clazz);
    }

    try {
      return clazz.newInstance();
    } catch (Exception e) {
      throw new RRException("获取参数失败");
    }
  }
}
