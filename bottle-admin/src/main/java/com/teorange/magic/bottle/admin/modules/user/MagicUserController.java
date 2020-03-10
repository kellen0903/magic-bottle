package com.teorange.magic.bottle.admin.modules.user;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.service.MagicUserService;
import java.util.Arrays;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户表
 *
 * @author cjj
 * @date 2018-06-10
 */
@RestController
@RequestMapping("user/front-user")
public class MagicUserController {

  @Autowired
  private MagicUserService magicUserService;

  /**
   * 列表
   */
  @RequestMapping("/list")
  @RequiresPermissions("user:frontUser:list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = magicUserService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  @RequiresPermissions("user:frontUser:info")
  public R info(@PathVariable("id") Long id) {
    MagicUserEntity magicUser = magicUserService.selectById(id);

    return R.ok().put("magicUser", magicUser);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  @RequiresPermissions("user:frontUser:save")
  public R save(@RequestBody MagicUserEntity magicUser) {
    magicUserService.insert(magicUser);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  @RequiresPermissions("user:frontUser:update")
  public R update(@RequestBody MagicUserEntity magicUser) {
    magicUserService.updateById(magicUser);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  @RequiresPermissions("user:frontUser:delete")
  public R delete(@RequestBody Long[] ids) {
    magicUserService.deleteBatchIds(Arrays.asList(ids));

    return R.ok();
  }

  /**
   * 禁言
   */
  @RequestMapping("/ban/{id}")
  public R ban(@PathVariable("id") Long id) {
    MagicUserEntity magicUser = magicUserService.selectById(id);
    if (magicUser != null) {
      magicUser.setStatus(2);
      magicUserService.updateById(magicUser);
    }
    return R.ok();
  }

  /**
   * 解禁
   */
  @RequestMapping("/liftBan/{id}")
  public R liftBan(@PathVariable("id") Long id) {
    MagicUserEntity magicUser = magicUserService.selectById(id);
    if (magicUser != null) {
      magicUser.setStatus(1);
      magicUserService.updateById(magicUser);
    }
    return R.ok();
  }


  /**
   * 设备禁言
   */
  @RequestMapping("/banDevice/{id}")
  public R banDevice(@PathVariable("id") Long id) {
    MagicUserEntity magicUser = magicUserService.selectById(id);
    if (magicUser != null) {
      MagicUserEntity magicUserEntity = new MagicUserEntity().setDeviceStatus(2).setStatus(2);
      magicUserService.update(magicUserEntity,
          new EntityWrapper<MagicUserEntity>().eq("imei", magicUser.getImei()));
    }
    return R.ok();
  }

  /**
   * 设备解禁
   */
  @RequestMapping("/liftBanDevice/{id}")
  public R liftBanDevice(@PathVariable("id") Long id) {
    MagicUserEntity magicUser = magicUserService.selectById(id);
    if (magicUser != null) {
      MagicUserEntity magicUserEntity = new MagicUserEntity().setDeviceStatus(1).setStatus(1);
      magicUserService.update(magicUserEntity,
          new EntityWrapper<MagicUserEntity>().eq("imei", magicUser.getImei()));
    }
    return R.ok();
  }

}
