package com.teorange.magic.bottle.admin.modules.support;
import java.util.Arrays;
import java.util.Map;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.FeedBackEntity;
import com.teorange.magic.bottle.api.service.FeedBackService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 意见反馈
 * created by cjj
 */
@RestController
@RequestMapping("support/feedback")
public class FeedBackController {
    @Autowired
    private FeedBackService feedBackService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("support:feedback:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("sidx", "create_time");
        params.put("order", "desc");
        PageUtils page = feedBackService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("support:feedback:info")
    public R info(@PathVariable("id") Long id){
        FeedBackEntity feedBack = feedBackService.selectById(id);

        return R.ok().put("feedBack", feedBack);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("support:feedback:save")
    public R save(@RequestBody FeedBackEntity feedBack){
        feedBackService.insert(feedBack);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("support:feedback:update")
    public R update(@RequestBody FeedBackEntity feedBack){
        feedBackService.updateById(feedBack);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("support:feedback:delete")
    public R delete(@RequestBody Long[] ids){
        feedBackService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
