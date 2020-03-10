package com.teorange.magic.bottle.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by kellen on 2018/5/28.
 */
@Data
@Accessors(chain = true)
public class PostDTO implements Serializable {


  /**
   * 帖子昵称
   */
  private String nickName;
  /**
   * 帖子id
   */
  private Long postId;

  /**
   * 发帖人
   */
  private UserDTO postUser;


  /**
   * 星期
   */
  @NotEmpty(message = "星期不能为空")
  private String week;


  /**
   * 温度  20-25
   */
  @NotEmpty(message = "温度不能为空")
  private String temperature;


  /**
   * 天气
   */
  @NotEmpty(message = "天气不能为空")
  private String weather;

  /**
   * 标签id
   */
  @NotEmpty(message = "标签id不能为空")
  private Long tagId;

  /**
   * 标签昵称
   */
  @NotEmpty(message = "标签名称不能为空")
  private String tagName;


  /**
   * 图片
   */
  private List<String> imagesList = new ArrayList<>();


  /**
   * 帖子内容
   */
  @NotEmpty(message = "魔瓶内容不能为空")
  private String content;

  /**
   * 帖子状态 0：未发布 1：已发布
   */
  @NotEmpty(message = "发布状态不能为空")
  private Integer publishStatus;

  /**
   * 屏蔽状态 0：未屏蔽 1：已屏蔽
   */
  private Integer shieldStatus;


  /**
   * 是否允许评论  0：是 1：否
   */
  private Integer commentFlag;

  /**
   * 创建时间
   */
  private Date createTime;


  private Integer upType = 1;

  /**
   * 点赞数量
   */
  private Long likeCount;

  /**
   * 评论数量
   */
  private Long commentCount;

  /**
   * 话题id
   */
  private Long topicId;

  /**
   * 话题内容
   */
  private String topicName;


}
