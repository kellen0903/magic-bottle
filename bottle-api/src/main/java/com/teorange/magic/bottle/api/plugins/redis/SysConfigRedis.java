package com.teorange.magic.bottle.api.plugins.redis;

import cn.teorange.framework.core.exception.RRException;
import cn.teorange.framework.core.utils.RedisKeys;
import cn.teorange.framework.redis.utils.RedisCacheUtil;
import com.google.gson.Gson;
import com.teorange.magic.bottle.api.dto.SysConfigDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 系统配置Redis
 */
@Component
@AllArgsConstructor
@Slf4j
public class SysConfigRedis {

  private RedisCacheUtil redisUtils;

  public void saveOrUpdate(SysConfigDTO config) {
    if (config == null) {
      return;
    }
    String key = RedisKeys.getSysConfigKey(config.getKey());
    log.info("开始保存配置信息至缓存，key:{},value:{}", key, config);
    redisUtils.set(key, config, null, null);
  }

  public void delete(String configKey) {
    String key = RedisKeys.getSysConfigKey(configKey);
    redisUtils.delete(key);
  }

  public String get(String configKey) {
    String key = RedisKeys.getSysConfigKey(configKey);
    SysConfigDTO sysConfigDTO = redisUtils.get(key, SysConfigDTO.class);
    log.info("从缓存中获取的配置信息:{}", sysConfigDTO);
    if (sysConfigDTO == null) {
      return null;
    }
    return sysConfigDTO.getValue();
  }


  public <T> T getConfigObject(String key, Class<T> clazz) {
    String value = this.get(key);
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
