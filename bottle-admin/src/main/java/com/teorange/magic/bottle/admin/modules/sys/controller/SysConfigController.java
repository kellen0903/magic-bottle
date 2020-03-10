/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.admin.modules.sys.controller;


import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.admin.annotation.SysLog;
import com.teorange.magic.bottle.api.domain.SysConfigEntity;
import com.teorange.magic.bottle.api.service.SysConfigService;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月4日 下午6:55:53
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {

  @Autowired
  private SysConfigService sysConfigService;

  /**
   * 所有配置列表
   */
  @GetMapping("/list")
  @RequiresPermissions("sys:config:list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = sysConfigService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 配置信息
   */
  @GetMapping("/info/{id}")
  @RequiresPermissions("sys:config:info")
  public R info(@PathVariable("id") Long id) {
    SysConfigEntity config = sysConfigService.selectById(id);

    return R.ok().put("config", config);
  }

  /**
   * 保存配置
   */
  @SysLog("保存配置")
  @PostMapping("/save")
  @RequiresPermissions("sys:config:save")
  public R save(@RequestBody SysConfigEntity config) {
    ValidatorUtils.validateEntity(config);

    sysConfigService.save(config);

    return R.ok();
  }

  /**
   * 修改配置
   */
  @SysLog("修改配置")
  @PostMapping("/update")
  @RequiresPermissions("sys:config:update")
  public R update(@RequestBody SysConfigEntity config) {
    ValidatorUtils.validateEntity(config);

    sysConfigService.update(config);

    return R.ok();
  }

  /**
   * 删除配置
   */
  @SysLog("删除配置")
  @PostMapping("/delete")
  @RequiresPermissions("sys:config:delete")
  public R delete(@RequestBody Long[] ids) {
    sysConfigService.deleteBatch(ids);

    return R.ok();
  }

}
