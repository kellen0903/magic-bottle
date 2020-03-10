package com.teorange.magic.bottle.admin.modules.sys.controller;

import cn.teorange.framework.core.constant.Constant;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import com.teorange.magic.bottle.admin.annotation.SysLog;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysRoleEntity;
import com.teorange.magic.bottle.admin.modules.sys.service.SysRoleMenuService;
import com.teorange.magic.bottle.admin.modules.sys.service.SysRoleService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月8日 下午2:18:33
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {

  @Autowired
  private SysRoleService sysRoleService;
  @Autowired
  private SysRoleMenuService sysRoleMenuService;

  /**
   * 角色列表
   */
  @GetMapping("/list")
  @RequiresPermissions("sys:role:list")
  public R list(@RequestParam Map<String, Object> params) {
    //如果不是超级管理员，则只查询自己创建的角色列表
    if (getUserId() != Constant.SUPER_ADMIN) {
      params.put("createUserId", getUserId());
    }

    PageUtils page = sysRoleService.queryPage(params);

    return R.ok().put("page", page);
  }

  /**
   * 角色列表
   */
  @GetMapping("/select")
  @RequiresPermissions("sys:role:select")
  public R select() {
    Map<String, Object> map = new HashMap<>();

    //如果不是超级管理员，则只查询自己所拥有的角色列表
    if (getUserId() != Constant.SUPER_ADMIN) {
      map.put("create_user_id", getUserId());
    }
    List<SysRoleEntity> list = sysRoleService.selectByMap(map);

    return R.ok().put("list", list);
  }

  /**
   * 角色信息
   */
  @GetMapping("/info/{roleId}")
  @RequiresPermissions("sys:role:info")
  public R info(@PathVariable("roleId") Long roleId) {
    SysRoleEntity role = sysRoleService.selectById(roleId);

    //查询角色对应的菜单
    List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
    role.setMenuIdList(menuIdList);

    return R.ok().put("role", role);
  }

  /**
   * 保存角色
   */
  @SysLog("保存角色")
  @PostMapping("/save")
  @RequiresPermissions("sys:role:save")
  public R save(@RequestBody SysRoleEntity role) {
    ValidatorUtils.validateEntity(role);

    role.setCreateUserId(getUserId());
    sysRoleService.save(role);

    return R.ok();
  }

  /**
   * 修改角色
   */
  @SysLog("修改角色")
  @PostMapping("/update")
  @RequiresPermissions("sys:role:update")
  public R update(@RequestBody SysRoleEntity role) {
    ValidatorUtils.validateEntity(role);

    role.setCreateUserId(getUserId());
    sysRoleService.update(role);

    return R.ok();
  }

  /**
   * 删除角色
   */
  @SysLog("删除角色")
  @PostMapping("/delete")
  @RequiresPermissions("sys:role:delete")
  public R delete(@RequestBody Long[] roleIds) {
    sysRoleService.deleteBatch(roleIds);

    return R.ok();
  }
}
