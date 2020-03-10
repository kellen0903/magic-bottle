package com.teorange.magic.bottle.command.web;

import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.core.utils.IPUtils;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.AddGroup;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.command.CreateUserCommand;
import com.teorange.magic.bottle.api.command.LoginCommand;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.ScreenAdEntity;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.api.service.ScreenAdService;
import com.teorange.magic.bottle.command.utils.JwtUtils;
import com.teorange.magic.bottle.command.utils.StringUtil;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/5/20. 登陆，鉴权控制器
 */

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthenticationController {

  private MagicUserService userService;

  private CommandGateway commandGateway;

  private JwtUtils jwtUtils;


  private ScreenAdService screenAdService;


  /**
   * 用户登陆接口，不存在则注册
   */
  @PostMapping("/login")
  public R login(@RequestBody UserDTO userDTO, HttpServletRequest request) {
    log.info("login param:{}", userDTO);
    long startTime = System.currentTimeMillis();
    log.info("登陆接口开始时间:{}", startTime);
    String openId = userDTO.getOpenId();
    if (StringUtil.isEmpty(openId)) {
      return R.error("openId不允许为空");
    }
    Long userId;
    userDTO.setLastLoginIp(IPUtils.getIpAddr(request))
        .setLastLoginTime(new Date());
    //根据openId查询用户存不存在
    MagicUserEntity magicUser = userService
        .query(new MagicUserEntity().setOpenId(userDTO.getOpenId()));
    if (null == magicUser) {
      //发送命令注册用户
      ValidatorUtils.validateEntity(userDTO, AddGroup.class);
      userId = IdWorker.getId();
      userDTO.setCreateTime(new Date())
          .setId(userId);
      CreateUserCommand createUserCommand = new CreateUserCommand().setUserDTO(userDTO);
      try {
        commandGateway.sendAndWait(createUserCommand);
      } catch (Exception e) {
        log.error("send login command error:{}", e);
        return R.error("登陆失败，请稍后再试");
      }
    } else {
      userId = magicUser.getId();
      LoginCommand loginCommand = new LoginCommand();
      BeanUtils.copyProperties(userDTO, loginCommand);
      loginCommand.setId(magicUser.getId()).setOldJpushId(magicUser.getJpushId());
      try {
        commandGateway.sendAndWait(loginCommand);
      } catch (Exception e) {
        log.error("send login command error:{}", e);
        return R.error("登陆失败，请稍后再试");
      }
    }
    //生成token返回
    String token = jwtUtils.generateToken(openId);
    Map<String, Object> resultMap = Maps.newHashMap();
    //token
    resultMap.put("token", token);
    resultMap.put("userId", userId);
    long endTime = System.currentTimeMillis();
    log.info("登陆接口结束时间:{}", endTime);
    log.info("登陆接口耗时:{}", endTime - startTime);
    return R.ok().put("data", resultMap);
  }


  /**
   * 打开app初始化,更换快要过期的token,推送未读消息
   */
  @PostMapping("/init")
  public R init(HttpServletRequest httpServletRequest,
      @RequestBody UserDTO userDTO) {
    log.info("入参:{}", userDTO);
    long startTime = System.currentTimeMillis();
    log.info("app初始化开始时间:{}", startTime);
    String isChangeDevice = "0";
    String token = httpServletRequest.getHeader("token");
    Map<String, Object> data = Maps.newHashMap();
    //获取开屏广告
    String osType = userDTO.getOsType();
    if (StrUtil.isEmpty(osType)) {
      return R.error("手机类型不允许为空");
    }
    ScreenAdEntity screenAdEntity = screenAdService
        .selectOne(new EntityWrapper<ScreenAdEntity>().eq("config_key", osType));
    String screenAd = "";
    if (null != screenAdEntity) {
      screenAd = screenAdEntity.getImgUrl();
      data.put("isShow", screenAdEntity.getIsShow());
    }
    data.put("screenAd", screenAd);
    if (StrUtil.isNotEmpty(token)) {
      Claims claims = jwtUtils.getClaimByToken(token);
      String openId = claims.getSubject();
      //登陆
      LoginCommand command = new LoginCommand();
      BeanUtils.copyProperties(userDTO, command);
      command.setLastLoginIp(IPUtils.getIpAddr(httpServletRequest))
          .setSourceType("ios".equals(osType) ? 1 : 2).setOpenId(openId);
      String jpushId = commandGateway.sendAndWait(command);
      log.info("查询到的系统保存的jpushId:{}", jpushId);
      if (jwtUtils.isRefresh(claims.getExpiration())) {
        token = jwtUtils.generateToken(openId);
      }
      //是否切换设备  0否 1是
      isChangeDevice = userDTO.getJpushId().equals(jpushId) ? "0" : "1";
    }
    data.put("token", StringUtil.trim(token));
    data.put("isChangeDevice", isChangeDevice);
    long endTime = System.currentTimeMillis();
    log.info("app初始化结束时间:{}", endTime);
    log.info("app初始化接口耗时:{}", endTime - startTime);
    return R.ok().put("data", data);
  }

}
