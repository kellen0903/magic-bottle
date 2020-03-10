package com.teorange.magic.bottle.command.web;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.service.TopicService;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/9/25. 话题控制器
 */
@RestController
@RequestMapping("/topic")
@AllArgsConstructor
@Slf4j
public class TopicController {

  private TopicService topicService;


  /**
   * 分页查询话题列表
   */
  @PostMapping("/page")
  public R page(@RequestBody Map<String, Object> param) {
    log.info("分页查询话题列表:{}", param);
    //时间倒序
    param.put("sidx", "order_num");
    param.put("order", "asc");
    PageUtils page = topicService.queryPage(param);
    return R.ok().put("data", page);
  }

}
