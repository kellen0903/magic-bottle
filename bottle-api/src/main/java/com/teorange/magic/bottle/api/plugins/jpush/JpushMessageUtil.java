package com.teorange.magic.bottle.api.plugins.jpush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.teorange.framework.redis.utils.RedisCacheUtil;
import com.teorange.magic.bottle.api.constant.GlobalConstant;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.dto.UserNotifyCache;
import com.teorange.magic.bottle.api.plugins.redis.SysConfigRedis;
import com.teorange.magic.bottle.api.service.MagicUserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 推送自定义消息 created by cjj
 */
@Component
@Slf4j
public class JpushMessageUtil {

  /**
   * 以下两个配置信息后面做成可配置
   **/
  private static final String APPKEY = "6708ca2a85e6cc2f34fa78d7";

  private static final String MASTERSECRET = "7cfd023382da948acc104ef7";

  private static JPushClient jpushClient = null;

  //保存离线的时长，最多支持10天（Ps：不填写时，默认是保存一天的离线消息 0：代表不保存离线消息）
  private static int timeToLive = 60 * 60 * 24;


  @Autowired
  private RedisCacheUtil redisCacheUtil;

  @Autowired
  private MagicUserService magicUserService;

  @Autowired
  private SysConfigRedis sysConfigRedis;

  /**
   * 自定义消息——生成推送对象
   */
  private Builder buildPushObject(List<String> userId, String msgContent) {
    //只推送到安卓和ios平台
    Builder builder = PushPayload.newBuilder();
    builder.setPlatform(Platform.android_ios())
        .setAudience(Audience.alias(userId))
        .setMessage(Message.newBuilder()
            //自定义消息内容 必填
            .setMsgContent(msgContent)
            //消息内容类型 可选
            .setContentType("text")
            // 消息标题 可选
            .setTitle("小魔瓶")
            //透传参数
            .addExtra("from", "透传参数")
            .build());
    return builder;
  }


  /**
   * 触发推送
   */
  public void sendPush(List<String> userId, String msgContent) {
    if (CollectionUtils.isEmpty(userId)) {
      return;
    }
    try {
      jpushClient = new JPushClient(MASTERSECRET, APPKEY, null, ClientConfig.getInstance());
//      boolean isGrayPerson = isGrayPerson(userId.get(0));
//      if(isGrayPerson){
//      }
      //生成推送的内容
      Builder builder = this.buildPushObject(userId, msgContent);
      buildNotify(userId.get(0), builder);
      PushPayload payload = builder.build();
      log.info("生成的推送内容{}", payload);
      //payload.resetOptionsTimeToLive(timeToLive);
      PushResult result = jpushClient.sendPush(payload);
      log.info("推送自定义结果:{}", result);
    } catch (APIConnectionException e) {
      // Connection error, should retry later
      log.error("Connection error, should retry later", e);

    } catch (APIRequestException e) {
      // Should review the error, and fix the request
      log.error("Should review the error, and fix the request", e);
      log.info("HTTP Status: " + e.getStatus());
      log.info("Error Code: " + e.getErrorCode());
      log.info("Error Message: " + e.getErrorMessage());
    }

  }


  /**
   * 推送ios通知
   */
  private void buildNotify(String alias, Builder builder) {
    //查询是否是ios设备
    MagicUserEntity userEntity = magicUserService.selectById(alias);
    if (2 == userEntity.getSourceType()) {
      return;
    }
    String alert = "收到了一条通知";
    UserNotifyCache userNotifyCache = redisCacheUtil
        .get(GlobalConstant.USER_NOTIFY_CONTROL_KEY + alias,
            UserNotifyCache.class);
    log.info("查询到用户通知参数：{}", userNotifyCache);
    if (null == userNotifyCache) {
      userNotifyCache = new UserNotifyCache().setUnReadCount(0);
    }
    Boolean nofityFlag = userNotifyCache.isNofityFlag();
    Boolean soundFlag = userNotifyCache.isSoundFlag();
    if (!nofityFlag) {
      log.info("关闭了通知开关，无需推送通知");
      return;
    }
    Integer badge = userNotifyCache.getUnReadCount() + 1;
    userNotifyCache.setUnReadCount(badge);
    redisCacheUtil.set(GlobalConstant.USER_NOTIFY_CONTROL_KEY + alias, userNotifyCache, null, null);
    builder
        .setNotification(Notification.newBuilder()
            .addPlatformNotification(
                IosNotification.newBuilder()
                    .setAlert(alert)
                    .setSound(soundFlag ? "default" : "")
                    .setBadge(badge)
                    .setContentAvailable(true)
                    .build()).build())
        .setOptions(Options.newBuilder()
            //true-推送生产环境 false-推送开发环境（测试使用参数）
            .setApnsProduction(true)
            .setTimeToLive(timeToLive)
            .build());
  }


  /**
   * 判断是否是灰度名单
   */
  public boolean isGrayPerson(String userId) {
    //查询管理员列表，如果是管理员举报，直接屏蔽
    List<String> grayList = sysConfigRedis
        .getConfigObject(GlobalConstant.GRAY_PEOPLE, List.class);
    return grayList.stream()
        .anyMatch(e -> e.equals(userId));
  }


}
