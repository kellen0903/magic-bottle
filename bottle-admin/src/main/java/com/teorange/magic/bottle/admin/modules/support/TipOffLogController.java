package com.teorange.magic.bottle.admin.modules.support;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
import com.teorange.magic.bottle.api.service.TipOffLogService;
import java.util.Arrays;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报记录 created by cjj
 */
@RestController
@RequestMapping("support/tipofflog")
public class TipOffLogController {

  @Autowired
  private TipOffLogService tipOffLogService;

  /**
   * 列表
   */
  @RequestMapping("/list")
  @RequiresPermissions("support:tipofflog:list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = tipOffLogService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  @RequiresPermissions("support:tipofflog:info")
  public R info(@PathVariable("id") Long id) {
    TipOffLogEntity tipOffLog = tipOffLogService.selectById(id);

    return R.ok().put("tipOffLog", tipOffLog);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  @RequiresPermissions("support:tipofflog:save")
  public R save(@RequestBody TipOffLogEntity tipOffLog) {
    tipOffLogService.insert(tipOffLog);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  @RequiresPermissions("support:tipofflog:update")
  public R update(@RequestBody TipOffLogEntity tipOffLog) {
    tipOffLogService.updateById(tipOffLog);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  @RequiresPermissions("support:tipofflog:delete")
  public R delete(@RequestBody Long[] ids) {
    tipOffLogService.deleteBatchIds(Arrays.asList(ids));

    return R.ok();
  }


  /**
   * 举报统计
   */
  @RequestMapping("/report")
  public R report(@RequestParam Map<String, Object> param) {
    //查询被举报次数大于N的用户列表
    PageUtils page = tipOffLogService.countReport(param);
    return R.ok().put("page", page);
  }

}

