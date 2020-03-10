package com.teorange.magic.bottle.api.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 开屏广告
 * 
 * @author cjj
 * @date 2018-06-09 12:45:18
 */
@Data
@TableName("screen_ad")
public class ScreenAdEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 * key
	 */
	private String configKey;
	/**
	 * 图片地址
	 */
	private String imgUrl;
	/**
	 * 广告链接
	 */
	private String linkUrl;
	/**
	 * 创建时间
	 */
	private Date createDate;

	/**
	 * 是否开启 0：是 1：否
	 */
	private  Integer isShow;
}
