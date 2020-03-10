package com.teorange.magic.bottle.admin.modules.post;

import java.util.Arrays;
import java.util.Map;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.service.PostReplyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




/**
 * 帖子回复
 *
 * @author cjj
 * @date 2018-05-31 18:01:10
 */
@RestController
@RequestMapping("post/postreply")
public class PostReplyController {
    @Autowired
    private PostReplyService postReplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("post:postreply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = postReplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("post:postreply:info")
    public R info(@PathVariable("id") Long id){
			PostReplyEntity postReply = postReplyService.selectById(id);

        return R.ok().put("postReply", postReply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("post:postreply:save")
    public R save(@RequestBody PostReplyEntity postReply){
			postReplyService.insert(postReply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("post:postreply:update")
    public R update(@RequestBody PostReplyEntity postReply){
			postReplyService.updateById(postReply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("post:postreply:delete")
    public R delete(@RequestBody Long[] ids){
			postReplyService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
