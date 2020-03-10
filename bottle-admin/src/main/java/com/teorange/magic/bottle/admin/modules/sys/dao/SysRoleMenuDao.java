package com.teorange.magic.bottle.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysRoleMenuEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色与菜单对应关系
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:33:46
 */
@Mapper
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenuEntity> {
	
	/**
	 * 根据角色ID，获取菜单ID列表
	 */
	List<Long> queryMenuIdList(Long roleId);

	/**
	 * 根据角色ID数组，批量删除
	 */
	int deleteBatch(Long[] roleIds);
}
