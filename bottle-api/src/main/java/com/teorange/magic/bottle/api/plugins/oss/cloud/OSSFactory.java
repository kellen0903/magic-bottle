/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.api.plugins.oss.cloud;


import cn.teorange.framework.core.constant.ConfigConstant;
import cn.teorange.framework.core.constant.Constant;
import com.teorange.magic.bottle.api.plugins.redis.SysConfigRedis;
import com.teorange.magic.bottle.api.service.SysConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 文件上传Factory
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-26 10:18
 */

@AllArgsConstructor
@Component
@Slf4j
public class OSSFactory {


  private SysConfigService sysConfigService;


  public CloudStorageService build() {
    //获取云存储配置信息
    CloudStorageConfig config = sysConfigService
        .getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY, CloudStorageConfig.class);
    log.info("从缓存中获取的配置信息:{}", config);
    if (config.getType() == Constant.CloudService.QINIU.getValue()) {
      return new QiniuCloudStorageService(config);
    } else if (config.getType() == Constant.CloudService.ALIYUN.getValue()) {
      return new AliyunCloudStorageService(config);
    } else if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
      return new QcloudCloudStorageService(config);
    }

    return null;
  }

}
