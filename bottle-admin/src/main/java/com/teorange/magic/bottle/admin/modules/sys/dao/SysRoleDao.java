package com.teorange.magic.bottle.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysRoleEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:33:33
 */
@Mapper
public interface SysRoleDao extends BaseMapper<SysRoleEntity> {
	
	/**
	 * 查询用户创建的角色ID列表
	 */
	List<Long> queryRoleIdList(Long createUserId);
}
