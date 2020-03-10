package com.teorange.magic.bottle.admin.modules.support;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.AppVersionEntity;
import com.teorange.magic.bottle.api.service.AppVersionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 *  App升级管理
 * created by cjj
 */
@RestController
@RequestMapping("support/version")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("support:version:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = appVersionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("support:version:info")
    public R info(@PathVariable("id") Long id){
        AppVersionEntity appVersion = appVersionService.selectById(id);

        return R.ok().put("appVersion", appVersion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("support:version:save")
    public R save(@RequestBody AppVersionEntity appVersion){
        appVersionService.insert(appVersion);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("support:version:update")
    public R update(@RequestBody AppVersionEntity appVersion){
        appVersionService.updateById(appVersion);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("support:version:delete")
    public R delete(@RequestBody Long[] ids){
        appVersionService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }
}
