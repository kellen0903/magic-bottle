package com.teorange.magic.bottle.admin.modules.sys.service.impl;

import cn.teorange.framework.core.constant.Constant;
import com.teorange.magic.bottle.admin.modules.sys.dao.SysMenuDao;
import com.teorange.magic.bottle.admin.modules.sys.dao.SysUserDao;
import com.teorange.magic.bottle.admin.modules.sys.dao.SysUserTokenDao;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysMenuEntity;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserEntity;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserTokenEntity;
import com.teorange.magic.bottle.admin.modules.sys.service.ShiroService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<SysMenuEntity> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(SysMenuEntity menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUserTokenEntity queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);
    }

    @Override
    public SysUserEntity queryUser(Long userId) {
        return sysUserDao.selectById(userId);
    }
}
