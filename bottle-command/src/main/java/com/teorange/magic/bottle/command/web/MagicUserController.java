package com.teorange.magic.bottle.command.web;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.redis.utils.RedisCacheUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.command.CompleteUserInfoCommand;
import com.teorange.magic.bottle.api.constant.GlobalConstant;
import com.teorange.magic.bottle.api.domain.AppVersionEntity;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.dto.UserNotifyCache;
import com.teorange.magic.bottle.api.plugins.wordfilter.WordFilter;
import com.teorange.magic.bottle.api.service.AppVersionService;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import com.teorange.magic.bottle.command.query.vo.UserInfoVO;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户表
 *
 * Created by kellen on 2018-05-20 22:59:11
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class MagicUserController {

  private MagicUserService userService;

  private CommandGateway commandGateway;

  private PostService postService;

  private WordFilter wordFilter;

  private AppVersionService appVersionService;

  private StringRedisTemplate redisTemplate;

  private RedisCacheUtil redisCacheUtil;


  /**
   * 完善资料
   */
  @PostMapping("/completeInfo")
  @Login
  public R completeInfo(@RequestBody UserDTO userDTO, @LoginUser MagicUserEntity userEntity) {
    log.info("完善资料入参；{}", userDTO);
    String age = userDTO.getAge();
    String nickName = userDTO.getNickName();
    //判断昵称是否被占用
    if (StrUtil.isNotEmpty(nickName)) {
      boolean isRepeat = userService.checkNickName(nickName);
      if (isRepeat) {
        return R.error("昵称已被占用");
      }
    }
    Integer sex = userDTO.getSex();
    CompleteUserInfoCommand command = new CompleteUserInfoCommand(userEntity.getOpenId(), sex,
        nickName, age);
    commandGateway.send(command);
    return R.ok().put("data", Maps.newHashMap());
  }


  /**
   * 查询用户信息
   */
  @PostMapping("/findUserInfo")
  @Login
  public R findUserInfo(@LoginUser MagicUserEntity userEntity) {
    UserInfoVO userInfo = new UserInfoVO();
    BeanUtils.copyProperties(userEntity, userInfo);
    UserNotifyCache userNotifyCache = redisCacheUtil
        .get(GlobalConstant.USER_NOTIFY_CONTROL_KEY + userEntity.getId(),
            UserNotifyCache.class);
    if (null == userNotifyCache) {
      userNotifyCache = new UserNotifyCache();
    }
    userInfo.setUserNotifyConfig(userNotifyCache);
    return R.ok().put("data", userInfo);
  }


  /**
   * 我的魔瓶
   */
  @PostMapping("/myPost")
  @Login
  public R myPost(@LoginUser MagicUserEntity userEntity, @RequestBody Map<String, Object> param) {
    param.put("userId", userEntity.getId());
    param.put("deleted", 0);
    param.put("sidx", "create_time");
    param.put("order", "desc");
    param.put("isMy", true);
    PageUtils page = postService.queryPage(param);
    return R.ok().put("data", page);
  }


  /**
   * 检查是昵称，是否有敏感词和是否重复
   */
  @PostMapping("/checkNickName")
  //@Login
  public R checkNickName(@RequestBody Map<String, Object> param) {
    log.info("checkNickName param:{}", param);
    String nickName = MapUtil.getStr(param, "nickName");
    if (StrUtil.isEmpty(nickName)) {
      return R.error("昵称不允许为空");
    }
    Map<String, Object> data = Maps.newHashMap();
    boolean isSensitive = wordFilter.isContains(nickName);
    data.put("isSensitive", isSensitive);
    boolean isRepeat = userService.checkNickName(nickName);
    data.put("isRepeat", isRepeat);
    return R.ok().put("data", data);
  }

  /**
   * 检查版本更新
   */
  @PostMapping("/checkVersion")
  public R checkUpdate(@RequestBody Map<String, Object> data) {
    //版本升级
    Integer version = MapUtil.getInt(data, "currentVersion");
    AppVersionEntity appVersionEntity = appVersionService
        .selectOne(new EntityWrapper<AppVersionEntity>().eq("os_type", "android"));
    if (null != appVersionEntity) {
      Integer newVersion = appVersionEntity.getNewVersion();
      Integer min = appVersionEntity.getMinVersion();
      log.info("最新版本号{}", newVersion);
      if (version < newVersion) {
        appVersionEntity.setUpdate(true);
      }
      if (version < min) {
        appVersionEntity.setForceUpdate(true);
      }
    }
    data.put("appVersion", appVersionEntity);
    return R.ok().put("data", data);
  }


  /**
   * 更新未读数量
   */
  @PostMapping("/updateUnReadMsg")
  @Login
  public R updateUnReadMsg(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody Map<String, Object> param) {
    log.info("更新未读数量入参:{}", param);
    //未读消息缓存
    Integer count = MapUtil.getInt(param, "count");
    if (null == count) {
      return R.error("数量不能为空");
    }
    UserNotifyCache userNotifyCache = redisCacheUtil
        .get(GlobalConstant.USER_NOTIFY_CONTROL_KEY + magicUserEntity.getId(),
            UserNotifyCache.class);

    if (null == userNotifyCache) {
      userNotifyCache = new UserNotifyCache().setUnReadCount(count);
    }
    userNotifyCache.setUnReadCount(count);
    redisCacheUtil
        .set(GlobalConstant.USER_NOTIFY_CONTROL_KEY + magicUserEntity.getId(), userNotifyCache,
            null, null);
    //redisTemplate.opsForValue().set(UN_READ_MSG + magicUserEntity.getId(), count.toString());
    return R.ok();
  }


  /**
   * 用户消息通知控制修改
   */
  @PostMapping("/config")
  @Login
  public R config(@LoginUser MagicUserEntity magicUserEntity,
      @RequestBody Map<String, Object> paramMap) {
    log.info("用户消息通知修改入参:{}", paramMap);
    /**
     * 是否推送
     */
    Boolean nofityFlag = MapUtil.getBool(paramMap, "nofityFlag");

    /**
     * 是否推送声音
     */
    Boolean soundFlag = MapUtil.getBool(paramMap, "soundFlag");

    if (null == nofityFlag && null == soundFlag) {
      return R.error("参数不全");
    }

    UserNotifyCache userNotifyCache = redisCacheUtil
        .get(GlobalConstant.USER_NOTIFY_CONTROL_KEY + magicUserEntity.getId(),
            UserNotifyCache.class);
    log.info("读取到缓存中用户的消息通知信息:{}", userNotifyCache);
    if (null == userNotifyCache) {
      userNotifyCache = new UserNotifyCache().setUnReadCount(0);
    }
    if (null != nofityFlag) {
      userNotifyCache.setNofityFlag(nofityFlag);
    }

    if (null != soundFlag) {
      userNotifyCache.setSoundFlag(soundFlag);
    }
    log.info("放入缓存中用户通知信息:{}", userNotifyCache);
    redisCacheUtil
        .set(GlobalConstant.USER_NOTIFY_CONTROL_KEY + magicUserEntity.getId(), userNotifyCache,
            null, null);
    return R.ok();

  }


  @GetMapping("/test")
  public R test() {

    return R.ok().put("data", new UserNotifyCache());

  }


}
