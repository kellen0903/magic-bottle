/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.admin.modules.oss.controller;

import cn.teorange.framework.core.constant.ConfigConstant;
import cn.teorange.framework.core.constant.Constant;
import cn.teorange.framework.core.exception.RRException;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.AliyunGroup;
import cn.teorange.framework.core.validator.group.QcloudGroup;
import cn.teorange.framework.core.validator.group.QiniuGroup;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.teorange.magic.bottle.admin.modules.oss.entity.SysOssEntity;
import com.teorange.magic.bottle.admin.modules.oss.service.SysOssService;
import com.teorange.magic.bottle.api.service.SysConfigService;
import com.teorange.magic.bottle.api.plugins.oss.cloud.CloudStorageConfig;
import com.teorange.magic.bottle.api.plugins.oss.cloud.OSSFactory;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 12:13:26
 */
@RestController
@RequestMapping("sys/oss")
public class SysOssController {

  @Autowired
  private SysOssService sysOssService;
  @Autowired
  private SysConfigService sysConfigService;

  @Autowired
  private OSSFactory ossFactory;

  private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;

  /**
   * 列表
   */
  @GetMapping("/list")
  @RequiresPermissions("sys:oss:all")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = sysOssService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 云存储配置信息
   */
  @GetMapping("/config")
  @RequiresPermissions("sys:oss:all")
  public R config() {
    CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);

    return R.ok().put("config", config);
  }


  /**
   * 保存云存储配置信息
   */
  @PostMapping("/saveConfig")
  @RequiresPermissions("sys:oss:all")
  public R saveConfig(@RequestBody CloudStorageConfig config) {
    //校验类型
    ValidatorUtils.validateEntity(config);

    if (config.getType() == Constant.CloudService.QINIU.getValue()) {
      //校验七牛数据
      ValidatorUtils.validateEntity(config, QiniuGroup.class);
    } else if (config.getType() == Constant.CloudService.ALIYUN.getValue()) {
      //校验阿里云数据
      ValidatorUtils.validateEntity(config, AliyunGroup.class);
    } else if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
      //校验腾讯云数据
      ValidatorUtils.validateEntity(config, QcloudGroup.class);
    }

    sysConfigService.updateValueByKey(KEY, new Gson().toJson(config));

    return R.ok();
  }


  /**
   * 上传文件
   */
  @PostMapping("/upload")
  //@RequiresPermissions("sys:oss:all")
  public R upload(@RequestParam("file") MultipartFile file) throws Exception {
    HashMap<String, Object> returnMap = new HashMap<>();
    String fsizeStr = "";
    if (file.isEmpty()) {
      throw new RRException("上传文件不能为空");
    }

    //上传文件
    String suffix = file.getOriginalFilename()
        .substring(file.getOriginalFilename().lastIndexOf("."));
    //如果是七牛云返回url、fsize、type
    String url = ossFactory.build().uploadSuffix(file.getBytes(), suffix);
    Map<String, Object> resultMap = JSON.parseObject(url, Map.class);
    if (resultMap.get("type").equals("qiniu")) {
      url = resultMap.get("url").toString();
      long fsize = Long.valueOf(resultMap.get("fsize").toString()).longValue();
      DecimalFormat df = new DecimalFormat("#.00");
      if (fsize < 1024) {
        fsizeStr = df.format((double) fsize) + "B";
      } else if (fsize < 1048576) {
        fsizeStr = df.format((double) fsize / 1024) + "K";
      } else if (fsize < 1073741824) {
        fsizeStr = df.format((double) fsize / 1048576) + "M";
      } else {
        fsizeStr = df.format((double) fsize / 1073741824) + "G";
      }
    }
    //保存文件信息
    SysOssEntity ossEntity = new SysOssEntity();
    ossEntity.setUrl(url);
    ossEntity.setCreateDate(new Date());
    sysOssService.insert(ossEntity);

    returnMap.put("url", url);
    returnMap.put("fsize", fsizeStr);
    return R.ok(returnMap);
  }


  /**
   * 删除
   */
  @PostMapping("/delete")
  @RequiresPermissions("sys:oss:all")
  public R delete(@RequestBody Long[] ids) {
    sysOssService.deleteBatchIds(Arrays.asList(ids));

    return R.ok();
  }

}
