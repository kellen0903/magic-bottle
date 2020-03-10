package com.teorange.magic.bottle.admin.modules.post;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import com.teorange.magic.bottle.api.service.ChatService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * created by cjj
 */
@RestController
@RequestMapping("post/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("post:chat:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = chatService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{chatId}")
    @RequiresPermissions("post:chat:info")
    public R info(@PathVariable("chatId") Long chatId){
        ChatEntity chat = chatService.selectById(chatId);

        return R.ok().put("chat", chat);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("post:chat:save")
    public R save(@RequestBody ChatEntity chat){
        chatService.insert(chat);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("post:chat:update")
    public R update(@RequestBody ChatEntity chat){
        chatService.updateById(chat);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("post:chat:delete")
    public R delete(@RequestBody Long[] chatIds){
        chatService.deleteBatchIds(Arrays.asList(chatIds));

        return R.ok();
    }

}
