/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.admin.modules.job.controller;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.admin.modules.job.entity.ScheduleJobLogEntity;
import com.teorange.magic.bottle.admin.modules.job.service.ScheduleJobLogService;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.2.0 2016-11-28
 */
@RestController
@RequestMapping("/sys/scheduleLog")
public class ScheduleJobLogController {

  @Autowired
  private ScheduleJobLogService scheduleJobLogService;

  /**
   * 定时任务日志列表
   */
  @GetMapping("/list")
  @RequiresPermissions("sys:schedule:log")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = scheduleJobLogService.queryPage(params);

    return R.ok().put("page", page);
  }

  /**
   * 定时任务日志信息
   */
  @GetMapping("/info/{logId}")
  public R info(@PathVariable("logId") Long logId) {
    ScheduleJobLogEntity log = scheduleJobLogService.selectById(logId);

    return R.ok().put("log", log);
  }
}
