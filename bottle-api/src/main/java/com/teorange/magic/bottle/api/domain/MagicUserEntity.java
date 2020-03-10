package com.teorange.magic.bottle.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户表
 *
 * Created by kellen on 2018-05-20 22:59:11
 */
@TableName("magic_user")
@Data
@Accessors(chain = true)
public class MagicUserEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
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
   * 密码
   */
  private String password;
  /**
   * 登录方式 1：微信 2:QQ
   */
  private Integer loginType;
  /**
   * 极光推送id
   */
  //@TableField(strategy = FieldStrategy.IGNORED)
  private String jpushId;
  /**
   * 创建时间
   */
  private Date createTime;
  /**
   * 是否删除 1：是 2：否
   */
  private Integer deleted;
  /**
   * openId
   */
  private String openId;
  /**
   * 1：正常 2：禁言
   */
  private Integer status;

  /**
   * 设备禁言状态 1正常 2禁言
   */
  private Integer deviceStatus;

}
