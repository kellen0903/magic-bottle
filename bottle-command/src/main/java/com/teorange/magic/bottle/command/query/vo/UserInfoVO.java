package com.teorange.magic.bottle.command.query.vo;

import com.teorange.magic.bottle.api.dto.UserNotifyCache;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Created by kellen on 2018/5/27. 用户信息视图
 */
@Data
public class UserInfoVO implements Serializable {


  /**
   * id
   */
  private Long id;
  /**
   * 用户昵称
   */
  private String nickName;
  /**
   * 最后一次登录ip地址
   */
  private String lastLoginIp;
  /**
   * 性别 1：男 2：女 3：未知
   */
  private Integer sex;
  /**
   * idfa
   */
  private String idfa;
  /**
   * ua
   */
  private String ua;
  /**
   * 手机名称
   */
  private String mobileName;
  /**
   * 手机型号
   */
  private String mobileVersion;
  /**
   * imei
   */
  private String imei;
  /**
   * 来源 1：ios  2:andriod
   */
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
  private Integer loginType;
  /**
   * 极光推送id
   */
  private String jpushId;
  /**
   * openId
   */
  private String openId;

  private Integer status;

  /**
   * 用户控制通知信息
   */
  private UserNotifyCache userNotifyConfig;

}
