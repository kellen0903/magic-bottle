package com.teorange.magic.bottle.admin.modules.support;

import java.util.Arrays;
import java.util.Map;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.ScreenAdEntity;
import com.teorange.magic.bottle.api.service.ScreenAdService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * 开屏广告
 *
 * @author cjj
 * @date 2018-06-09 12:45:18
 */
@RestController
@RequestMapping("support/screenAd")
public class ScreenAdController {
    @Autowired
    private ScreenAdService screenAdService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("support:screenad:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = screenAdService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("support:screenad:info")
    public R info(@PathVariable("id") Long id){
			ScreenAdEntity screenAd = screenAdService.selectById(id);

        return R.ok().put("screenAd", screenAd);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("support:screenad:save")
    public R save(@RequestBody ScreenAdEntity screenAd){
			screenAdService.insert(screenAd);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("support:screenad:update")
    public R update(@RequestBody ScreenAdEntity screenAd){
			screenAdService.updateById(screenAd);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("support:screenad:delete")
    public R delete(@RequestBody Long[] ids){
			screenAdService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
