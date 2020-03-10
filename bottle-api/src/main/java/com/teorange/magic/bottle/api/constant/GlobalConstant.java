package com.teorange.magic.bottle.api.constant;

/**
 * Created by kellen on 2018/5/28.
 */
public class GlobalConstant {

  /**
   * 不屏蔽
   */
  public static final Integer SHIELDSTATUS_NO = 0;

  /**
   * 已屏蔽
   */
  public static final Integer SHIELDSTATUS_YES = 1;

  /**
   * 七牛云token key
   */
  public static final String QINIU_TOKEN_KEY = "qiniu_token_key";

  public static final String CACHE_POST_CREATE_LIMIT_KEY = "CACHE_POST_CREATE_LIMIT_KEY:";

  /**
   * 帖子被不同ip举报自动屏蔽次数配置key
   */
  public static final String POST_TIPOFF_SHIELD_COUNT = "post_tipoff_shield_count";

  /**
   * 用户被举报禁言配置key
   */
  public static final String USER_TIPOFF_CONFIG = "user_tipoff_config";

  /**
   * 帖子最少长度
   */
  public static final String POST_MIN_LENGTH = "post_min_length";


  /**
   * 未读消息key
   */
  public static final String UN_READ_MSG = "un_read_msg";


  public static final String FORBIDEN_MESSAGE = "您的帐号暂时处理封禁状态";


  /**
   * 黑名单key
   */
  public static final String BLACK_CACHE_KEY = "black:cache:key";

  /**
   * 用户通知控制key
   */
  public static final String USER_NOTIFY_CONTROL_KEY = "user_notify_control_key";


  /**
   * 管理员列表
   */
  public static final String ADMIN_USER_LIST = "admin_user_list";

  /**
   * 灰度名单
   */
  public static final String GRAY_PEOPLE = "gray_people";

  /**
   * 发帖时间间隔
   */
  public static final String POST_TIME_LIMIT = "post_time_limit";
}
