package com.teorange.magic.bottle.admin.modules.post;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.alibaba.fastjson.JSON;
import com.teorange.magic.bottle.admin.modules.sys.controller.AbstractController;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.api.service.TagService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;





/**
 * 帖子
 *
 * @author cjj
 * @date 2018-05-31 18:01:10
 */
@RestController
@RequestMapping("post/post")
public class PostController extends AbstractController{
    @Autowired
    private PostService postService;
    @Autowired
    private TagService tagService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("post:post:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = postService.queryPageList(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("post:post:info")
    public R info(@PathVariable("id") Long id){
			PostEntity post = postService.selectById(id);
        if (null != post.getImages()) {
            post.setImageList(JSON.parseArray(post.getImages(), String.class));
        }
        return R.ok().put("post", post);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("post:post:save")
    public R save(@RequestBody PostEntity post){
        Optional<Long> tagId = Optional.ofNullable(post.getTagId());
        if (tagId.isPresent()) {
            tagService.selectById(tagId.get());
        }
        post.setTagName(tagId.isPresent()? Optional.ofNullable(tagService.selectById(tagId.get()).getTagName()).orElse(null) : "");
        Long postId = IdWorker.getId();
        post.setId(postId);
        post.setUserId(getUserId());
        post.setCreateTime(new Date());
        postService.insert(post);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("post:post:update")
    public R update(@RequestBody PostEntity post){
			postService.updateById(post);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("post:post:delete")
    public R delete(@RequestBody Long[] ids){
			postService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
