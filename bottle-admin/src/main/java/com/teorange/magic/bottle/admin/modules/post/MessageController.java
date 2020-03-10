package com.teorange.magic.bottle.admin.modules.post;

import java.util.Arrays;
import java.util.Map;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import com.teorange.magic.bottle.api.service.MessageService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 消息
 *
 * @author cjj
 * @date 2018-05-31
 */
@RestController
@RequestMapping("post/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("post:message:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = messageService.queryPageList(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("post:message:info")
    public R info(@PathVariable("id") Long id){
			MessageEntity message = messageService.selectById(id);

        return R.ok().put("message", message);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("post:message:save")
    public R save(@RequestBody MessageEntity message){
			messageService.insert(message);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("post:message:update")
    public R update(@RequestBody MessageEntity message){
			messageService.updateById(message);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("post:message:delete")
    public R delete(@RequestBody Long[] ids){
			messageService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
