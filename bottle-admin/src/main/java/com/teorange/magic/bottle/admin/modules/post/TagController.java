package com.teorange.magic.bottle.admin.modules.post;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.teorange.magic.bottle.api.domain.TagEntity;
import com.teorange.magic.bottle.api.domain.TopicEntity;
import com.teorange.magic.bottle.api.service.TagService;
import com.teorange.magic.bottle.api.service.TopicService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 标签管理
 *
 * @author cjj
 * @date 2018-05-31 18:01:10
 */
@RestController
@RequestMapping("post/tag")
@Slf4j
public class TagController {

  @Autowired
  private TagService tagService;

  @Autowired
  private TopicService topicService;

  /**
   * 列表
   */
  @RequestMapping("/list")
  @RequiresPermissions("post:tag:list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = tagService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  @RequiresPermissions("post:tag:info")
  public R info(@PathVariable("id") Long id) {
    TagEntity tag = tagService.selectById(id);

    return R.ok().put("tag", tag);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  @RequiresPermissions("post:tag:save")
  public R save(@RequestBody TagEntity tag) {
    tag.setCreateTime(new Date());
    tagService.insert(tag);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  @RequiresPermissions("post:tag:update")
  public R update(@RequestBody TagEntity tag) {
    tag.setUpdateTime(new Date());
    tagService.updateById(tag);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  @RequiresPermissions("post:tag:delete")
  public R delete(@RequestBody Long[] ids) {
    tagService.deleteBatchIds(Arrays.asList(ids));

    return R.ok();
  }

  /**
   * 标签下拉选择器
   */
  @RequestMapping("/querList")
  public R querList() {
    List<TagEntity> tagEntities = tagService
        .selectList(new EntityWrapper<TagEntity>().eq("deleted", 0));
    return R.ok().put("list", tagEntities);
  }


  /**
   * 查询标签下的话题
   */
  @RequestMapping("/queryTopic/{id}")
  public R queryTopic(@PathVariable("id") Long id) {
    List<TopicEntity> topicList = topicService
        .selectList(new EntityWrapper<TopicEntity>().eq("tag_id", id));
    return R.ok().put("topicList", topicList);
  }

  /**
   * 分页查询标签下的话题
   */
  @RequestMapping("/topicPage")
  public R topicPage(@RequestParam Map<String, Object> params) {
    PageUtils page = topicService.queryPage(params);
    return R.ok().put("page", page);
  }


  /**
   * 保存话题
   */
  @PostMapping("/saveTopic")
  public R saveTopic(@RequestBody Map<String, Object> paramMap) {
    log.info("保存话题入参:{}", paramMap);
    Long tagId = MapUtil.getLong(paramMap, "tagId");
    List<Integer> deleteArray = MapUtil.get(paramMap, "deleteArray", List.class);
    log.info("将要删除的话题id:{}", deleteArray);
    //将要删除的话题
    if (!CollectionUtils.isEmpty(deleteArray)) {
      topicService.deleteBatchIds(deleteArray);
    }
    //将要保存的话题
    List<String> addArray = MapUtil.get(paramMap, "addArray", List.class);
    if (!CollectionUtils.isEmpty(addArray)) {
      List<TopicEntity> entityList = Lists.newArrayList();
      addArray.forEach(e -> {
        TopicEntity topicEntity = new TopicEntity()
            .setTopicName(e).setCreateTime(new Date())
            .setUpdateTime(new Date()).setCreatedBy("system").setUpdatedBy("system")
            .setTagId(tagId);
        entityList.add(topicEntity);
      });
      topicService.insertBatch(entityList);
    }
    return R.ok();

  }

}
