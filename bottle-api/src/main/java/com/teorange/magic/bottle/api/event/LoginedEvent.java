package com.teorange.magic.bottle.api.event;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kellen on 2018/5/21.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginedEvent {

  private String openId;

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
   * 最后登录时间
   */
  private Date lastLoginTime;
  /**
   * 极光推送id
   */
  private String jpushId;

  /**
   * 旧极光id
   */
  private String oldJpushId;

  /**
   * 用户id
   */
  private Long id;

}
