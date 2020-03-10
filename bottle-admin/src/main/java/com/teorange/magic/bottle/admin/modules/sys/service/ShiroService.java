package com.teorange.magic.bottle.admin.modules.sys.service;

import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserEntity;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserTokenEntity;
import java.util.Set;

/**
 * shiro相关接口
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-06 8:49
 */
public interface ShiroService {

  /**
   * 获取用户权限列表
   */
  Set<String> getUserPermissions(long userId);

  SysUserTokenEntity queryByToken(String token);

  /**
   * 根据用户ID，查询用户
   */
  SysUserEntity queryUser(Long userId);
}
