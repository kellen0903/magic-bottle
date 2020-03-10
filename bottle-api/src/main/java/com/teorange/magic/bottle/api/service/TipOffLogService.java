package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
import com.teorange.magic.bottle.api.dto.TipOffReportDTO;
import com.teorange.magic.bottle.api.mapper.TipOffLogMapper;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * created by cjj
 */

@Service
public class TipOffLogService extends ServiceImpl<TipOffLogMapper, TipOffLogEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Long toUid = MapUtil.getLong(params, "toUid");
    Long fromUid = MapUtil.getLong(params, "fromUid");
    Integer itemType = MapUtil.getInt(params, "itemType");
    Date startTime = MapUtil.get(params, "startTime", Date.class);
    Date endTime = MapUtil.get(params, "endTime", Date.class);
    Page<TipOffLogEntity> page = this.selectPage(
        new Query<TipOffLogEntity>(params).getPage(),
        new EntityWrapper<TipOffLogEntity>()
            .eq(null != toUid, "to_user_id", toUid)
            .eq(null != fromUid, "user_id", fromUid)
            .eq(null != itemType, "item_type", itemType)
            .between(startTime != null && endTime != null, "createTime", startTime, endTime)
            .orderBy("create_time", false));
    return new PageUtils(page);
  }


  /**
   * 举报统计
   */
  public PageUtils countReport(Map<String, Object> params) {
    Page<TipOffReportDTO> page = new Query<TipOffReportDTO>(params).getPage();
    Integer num = MapUtil.getInt(params, "num");
    if (null == num) {
      num = 2;
    }
    params.put("num", num);
    page.setRecords(this.baseMapper.countReport(page, params));
    return new PageUtils(page);
  }


}
