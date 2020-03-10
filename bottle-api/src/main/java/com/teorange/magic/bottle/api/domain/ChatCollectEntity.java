package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kellen on 2018/9/25. 话题实体类
 */

@Data
@TableName("chat_collect")
@Accessors(chain = true)
public class ChatCollectEntity implements Serializable {


  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 用户id
   */
  private Long userId;

  /**
   * 会话id
   */
  private Long chatId;

  private String createdBy;

  private String updatedBy;

  private Date createTime;

  private Date updateTime;


}
