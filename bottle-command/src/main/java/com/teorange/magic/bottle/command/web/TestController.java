package com.teorange.magic.bottle.command.web;

import cn.teorange.framework.core.utils.R;
import com.teorange.magic.bottle.api.plugins.wordfilter.WordFilter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/6/3.
 */

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

  private WordFilter wordFilter;

  @GetMapping("/test")
  public R test(@RequestParam String param) {
    String ss = wordFilter.doFilter(param);
    return R.ok().put("data", ss);
  }

}
