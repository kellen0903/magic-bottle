package com.teorange.magic.bottle.command.web;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.api.service.TagService;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/5/30.
 */
@RestController
@RequestMapping("/tag")
@AllArgsConstructor
@Slf4j
public class TagController {

  private TagService tagService;


  /**
   * 查询列表
   */
  @PostMapping("/page")
  public R page(@RequestBody Map<String, Object> param) {
    Integer page = MapUtil.getInt(param, "page");
    Integer limit = MapUtil.getInt(param, "limit");
    if (null == page) {
      param.put("page", "1");
    }
    if (null == limit) {
      param.put("limit", "100");
    }
    param.put("sidx", "order_num");
    param.put("order", "asc");
    PageUtils pageData = tagService.queryPage(param);
    return R.ok().put("data", pageData);
  }


}
