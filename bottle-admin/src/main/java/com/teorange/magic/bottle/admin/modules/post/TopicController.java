package com.teorange.magic.bottle.admin.modules.post;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.domain.TopicEntity;
import com.teorange.magic.bottle.api.service.TopicService;
import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by kellen on 2018-11-01 20:49:18.
 */
@RestController
@RequestMapping("17chf/topic")
public class TopicController {

  @Autowired
  private TopicService topicService;

  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = topicService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    TopicEntity topic = topicService.selectById(id);

    return R.ok().put("topic", topic);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody TopicEntity topic) {
    topicService.insert(topic);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody TopicEntity topic) {
    topicService.updateById(topic);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    topicService.deleteBatchIds(Arrays.asList(ids));

    return R.ok();
  }

}
