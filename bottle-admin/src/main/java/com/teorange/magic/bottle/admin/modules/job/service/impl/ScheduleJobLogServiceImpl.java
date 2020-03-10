/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.admin.modules.job.service.impl;

import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.admin.modules.job.dao.ScheduleJobLogDao;
import com.teorange.magic.bottle.admin.modules.job.entity.ScheduleJobLogEntity;
import com.teorange.magic.bottle.admin.modules.job.service.ScheduleJobLogService;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends
    ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements
    ScheduleJobLogService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    String jobId = (String) params.get("jobId");

    Page<ScheduleJobLogEntity> page = this.selectPage(
        new Query<ScheduleJobLogEntity>(params).getPage(),
        new EntityWrapper<ScheduleJobLogEntity>()
            .like(StringUtils.isNotBlank(jobId), "job_id", jobId)
    );

    return new PageUtils(page);
  }

}
