package com.teorange.magic.bottle.admin.modules.sys.service.impl;

import cn.teorange.framework.core.constant.Constant;
import cn.teorange.framework.core.exception.RRException;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.teorange.magic.bottle.admin.modules.sys.dao.SysUserDao;
import com.teorange.magic.bottle.admin.modules.sys.entity.RobotUserEntity;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserEntity;
import com.teorange.magic.bottle.admin.modules.sys.service.SysRoleService;
import com.teorange.magic.bottle.admin.modules.sys.service.SysUserRoleService;
import com.teorange.magic.bottle.admin.modules.sys.service.SysUserService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 系统用户
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:46:09
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements
    SysUserService {

  @Autowired
  private SysUserRoleService sysUserRoleService;
  @Autowired
  private SysRoleService sysRoleService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    String username = (String) params.get("username");
    Long createUserId = (Long) params.get("createUserId");

    Page<SysUserEntity> page = this.selectPage(
        new Query<SysUserEntity>(params).getPage(),
        new EntityWrapper<SysUserEntity>()
            .like(StringUtils.isNotBlank(username), "username", username)
            .eq(createUserId != null, "create_user_id", createUserId)
    );

    return new PageUtils(page);
  }

  @Override
  public List<String> queryAllPerms(Long userId) {
    return baseMapper.queryAllPerms(userId);
  }

  @Override
  public List<Long> queryAllMenuId(Long userId) {
    return baseMapper.queryAllMenuId(userId);
  }

  @Override
  public SysUserEntity queryByUserName(String username) {
    return baseMapper.queryByUserName(username);
  }

  @Override
  @Transactional
  public void save(SysUserEntity user) {
    user.setCreateTime(new Date());
    //sha256加密
    String salt = RandomStringUtils.randomAlphanumeric(20);
    user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
    user.setSalt(salt);
    this.insert(user);

    //检查角色是否越权
    checkRole(user);

    //保存用户与角色关系
    sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
  }

  @Override
  @Transactional
  public void update(SysUserEntity user) {
    if (StringUtils.isBlank(user.getPassword())) {
      user.setPassword(null);
    } else {
      user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
    }
    this.updateById(user);

    //检查角色是否越权
    checkRole(user);

    //保存用户与角色关系
    sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
  }

  @Override
  public void deleteBatch(Long[] userId) {
    this.deleteBatchIds(Arrays.asList(userId));
  }

  @Override
  public boolean updatePassword(Long userId, String password, String newPassword) {
    SysUserEntity userEntity = new SysUserEntity();
    userEntity.setPassword(newPassword);
    return this.update(userEntity,
        new EntityWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
  }

  @Override
  public void addRobotUser(RobotUserEntity user) {
    SysUserEntity sysUserEntity = new SysUserEntity();
    BeanUtils.copyProperties(user, sysUserEntity);
    //sha256加密
    String salt = RandomStringUtils.randomAlphanumeric(20);
    sysUserEntity.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
    sysUserEntity.setSalt(salt);
    sysUserEntity.setCreateTime(new Date());
    this.insert(sysUserEntity);
    List<Long> roleIdList = Lists.newArrayList();
    roleIdList.add(2L);
    //保存用户与角色关系
    sysUserRoleService.saveOrUpdate(sysUserEntity.getUserId(), roleIdList);
  }

  /**
   * 检查角色是否越权
   */
  private void checkRole(SysUserEntity user) {
    if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
      return;
    }
    //如果不是超级管理员，则需要判断用户的角色是否自己创建
    if (user.getCreateUserId() == Constant.SUPER_ADMIN) {
      return;
    }

    //查询用户创建的角色列表
    List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

    //判断是否越权
    if (!roleIdList.containsAll(user.getRoleIdList())) {
      throw new RRException("新增用户所选角色，不是本人创建");
    }
  }
}
