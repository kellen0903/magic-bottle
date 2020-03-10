package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/5/21.
 */
@Data
@Accessors(chain = true)
public class UserDTO implements Serializable {

  private Long id;

  /**
   * 最后一次登录ip地址
   */
  private String lastLoginIp;

  /**
   * /** idfa
   */
  private String idfa;
  /**
   * ua
   */
  private String ua;
  /**
   * 手机名称
   */
  @NotEmpty(message = "手机名称不允许为空")
  private String mobileName;
  /**
   * 手机型号
   */
  @NotEmpty(message = "A手机型号不允许为空")
  private String mobileVersion;
  /**
   * imei
   */
  private String imei;
  /**
   * 来源 1：ios  2:andriod
   */
  @NotEmpty(message = "APP来源不允许为空")
  private Integer sourceType;
  /**
   * 年龄描述
   */
  private String age;
  /**
   * 最后登录时间
   */
  private Date lastLoginTime;
  /**
   * 登录方式 1：微信 2:QQ
   */
  @NotEmpty(message = "登陆方式不允许为空")
  private Integer loginType;
  /**
   * 极光推送id
   */
  @NotEmpty(message = "jPushId不允许为空")
  private String jpushId;
  /**
   * openId
   */
  @NotEmpty(message = "openId不允许为空")
  private String openId;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 性别
   */
  private Integer sex;


  /**
   * 昵称
   */
  private String nickName;


  private String osType;

  private String currentVersion;

}
