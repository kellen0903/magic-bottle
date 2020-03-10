package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登陆日志表
 *
 * Created by kellen on 2018-06-23 15:23:50
 */
@TableName("login_log")
@Accessors(chain = true)
@Data
public class LoginLogEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 用户id
   */
  private Long userId;
  /**
   *
   */
  private String openId;
  /**
   *
   */
  private String idfa;
  /**
   *
   */
  private String ua;
  /**
   *
   */
  private String mobileName;
  /**
   *
   */
  private String mobileVersion;
  /**
   *
   */
  private String imei;
  /**
   * 来源 1：ios  2:andriod
   */
  private Integer sourceType;
  /**
   * 登录方式 1：微信 2:QQ
   */
  private Integer loginType;
  /**
   *
   */
  private String jpushId;
  /**
   *
   */
  private Date createTime;
  /**
   *
   */
  private String ip;

}
