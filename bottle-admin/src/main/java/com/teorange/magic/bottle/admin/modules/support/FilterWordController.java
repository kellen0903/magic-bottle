package com.teorange.magic.bottle.admin.modules.support;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.admin.modules.sys.controller.AbstractController;
import com.teorange.magic.bottle.api.command.ResetWordCommand;
import com.teorange.magic.bottle.api.domain.FilterWordEntity;
import com.teorange.magic.bottle.api.service.FilterWordService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 敏感词
 *
 * @author cjj
 * @date 2018-06-03
 */
@RestController
@RequestMapping("support/filterWord")
public class FilterWordController extends AbstractController {

  @Autowired
  private FilterWordService filterWordService;

  @Autowired
  private CommandGateway commandGateway;

  /**
   * 列表
   */
  @RequestMapping("/list")
  @RequiresPermissions("support:filterword:list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = filterWordService.queryPageList(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  @RequiresPermissions("support:filterword:info")
  public R info(@PathVariable("id") Long id) {
    FilterWordEntity filterWord = filterWordService.selectById(id);

    return R.ok().put("filterWord", filterWord);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  @RequiresPermissions("support:filterword:save")
  public R save(@RequestBody FilterWordEntity filterWord) {

    List<FilterWordEntity> filterWordList = Arrays.asList(filterWord.getContent().split(","))
        .stream()
        .map(s -> {
          filterWord.setContent(s.trim());
          filterWord.setUserId(getUserId());
          filterWord.setCreateTime(new Date());
          return filterWord;
        }).collect(Collectors.toList());

    filterWordService.insertBatch(filterWordList);
    //重新初始化敏感词库
    commandGateway.send(new ResetWordCommand());
    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  @RequiresPermissions("support:filterword:update")
  public R update(@RequestBody FilterWordEntity filterWord) {

    filterWordService.updateById(filterWord);

    //重新初始化敏感词库
    commandGateway.send(new ResetWordCommand());

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  @RequiresPermissions("support:filterword:delete")
  public R delete(@RequestBody Long[] ids) {
    filterWordService.deleteBatchIds(Arrays.asList(ids));
    //重新初始化敏感词库
    commandGateway.send(new ResetWordCommand());
    return R.ok();
  }

}
