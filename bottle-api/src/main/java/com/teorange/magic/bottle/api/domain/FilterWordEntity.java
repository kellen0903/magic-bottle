package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 敏感词
 * 
 * @author cjj
 * @date 2018-05-31 18:01:10
 */
@TableName("filter_word")
@Data
@Accessors(chain = true)
public class FilterWordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 词语内容
	 */
	private String content;
	/**
	 * 创建人id
	 */
	private Long userId;
	/**
	 * 创建时间
	 */
	private Date createTime;

	/***
	 * 创建人
	 */
	@TableField(exist = false)
	private String createUserName;
}
