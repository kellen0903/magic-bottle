package com.teorange.magic.bottle.admin.modules.sys.entity;

import cn.teorange.framework.core.validator.group.AddGroup;
import cn.teorange.framework.core.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class RobotUserEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 用户ID
   */
  private Long userId;

  /**
   * 用户名
   */
  @NotBlank(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private String username;

  /**
   * 密码
   */
  @NotBlank(message = "密码不能为空", groups = AddGroup.class)
  private String password;

  /**
   * 盐
   */
  private String salt;

  /**
   * 手机号
   */
  private String mobile;

  /**
   * 状态  0：禁用   1：正常
   */
  private Integer status;

  /**
   * 创建者ID
   */
  private Long createUserId;

  /**
   * 创建时间
   */
  private Date createTime;

}
