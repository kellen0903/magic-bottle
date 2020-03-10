package com.teorange.magic.bottle.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserRoleEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与角色对应关系
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:34:46
 */
@Mapper
public interface SysUserRoleDao extends BaseMapper<SysUserRoleEntity> {
	
	/**
	 * 根据用户ID，获取角色ID列表
	 */
	List<Long> queryRoleIdList(Long userId);


	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);
}
