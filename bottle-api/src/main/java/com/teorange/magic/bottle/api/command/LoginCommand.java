package com.teorange.magic.bottle.api.command;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/5/21.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginCommand {

  @TargetAggregateIdentifier
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
   * 最后登录时间
   */
  private Date lastLoginTime;
  /**
   * 极光推送id
   */
  @NotEmpty(message = "jPushId不允许为空")
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
