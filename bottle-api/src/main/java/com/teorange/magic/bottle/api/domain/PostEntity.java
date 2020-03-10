package com.teorange.magic.bottle.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 帖子（魔瓶表）
 *
 * Created by kellen on 2018-05-28 22:03:30
 */
@TableName("post")
@Data
@Accessors(chain = true)
public class PostEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   *
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long id;
  /**
   * 标题
   */
  private String title;
  /**
   * 用户id
   */
  @TableId(type = IdType.INPUT)
  // 将Long类型转换成String，防止前台出现精度丢失的问题
  @JSONField(serializeUsing = ToStringSerializer.class)
  private Long userId;
  /**
   * 星期
   */
  private String week;
  /**
   * 昵称
   */
  private String nickName;
  /**
   * 温度
   */
  private String temperature;
  /**
   * 天气
   */
  private String weather;
  /**
   * 发布状态 0：未发布 1：已发布
   */
  private Integer publishStatus;
  /**
   * 屏蔽状态 0：未屏蔽 1：已屏蔽
   */
  private Integer shieldStatus;
  /**
   * 是否可以评论 0：是 1：否
   */
  private Integer commentFlag;
  /**
   * 标签id
   */
  private Long tagId;
  /**
   * 标签名称
   */
  private String tagName;
  /**
   * 评论数量
   */
  private Long commentCount;
  /**
   * 点赞数量
   */
  private Long likeCount;
  /**
   * 图片，多张逗号隔开
   */
  private String images;
  /**
   * 内容
   */
  private String content;
  /**
   *
   */
  private Date createTime;
  /**
   *
   */
  private Date updateTime;


  private Integer deleted;
  /**
   * 置顶帖子广告链接
   */
  private String linkUrl;


  /**
   * 性别 1：男 2：女
   */
  private Integer sex;

  @TableField(exist = false)
  private String postUserName;

  @TableField(exist = false)
  private String postTagName;


  /**
   * 图片列表
   */
  @TableField(exist = false)
  private List<String> imageList = new ArrayList<>();

  /**
   * 评论列表
   */
  @TableField(exist = false)
  private List<PostReplyEntity> replyList = new ArrayList<>();

  /**
   * 是否点赞
   */
  @TableField(exist = false)
  private boolean isUp = false;


  /**
   * 置顶帖子 1:ios,2:android
   */
  private Integer osType;

  /**
   * 话题id
   */
  private Long topicId;

  /**
   * 话题名称
   */
  private String topicName;



}
