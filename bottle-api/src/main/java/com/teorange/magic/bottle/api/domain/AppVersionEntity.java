package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/6/10.
 */
@TableName("app_version")
@Data
@Accessors(chain = true)
public class AppVersionEntity implements Serializable {

  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 新版本昵称
   */
  private String newVersionName;
  /**
   * 新版本号
   */
  private Integer newVersion;

  /**
   * 最小支持版本号
   */
  private Integer minVersion;

  /**
   * 下载地址
   */
  private String url;

  /**
   * 更新说明
   */
  private String desc;

  /**
   * 文件大小
   */
  private String size;

  /**
   * md值
   */
  private String md5;

  private Date createTime;

  private Date updateTime;

  /**
   * 系统类型 ios android
   */
  private String osType;

  @TableField(exist = false)
  private boolean isUpdate = false;

  @TableField(exist = false)
  private boolean isForceUpdate = false;


}
