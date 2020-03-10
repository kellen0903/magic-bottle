package com.teorange.magic.bottle.command.interceptor;


import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.core.exception.RRException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 权限(Token)验证
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:38
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {


  @Autowired
  private JwtUtils jwtUtils;

  public static final String USER_KEY = "openId";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    Login annotation;
    if (handler instanceof HandlerMethod) {
      annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
    } else {
      return true;
    }
    //获取用户凭证
    String token = request.getHeader(jwtUtils.getHeader());
    if (annotation == null && StrUtil.isEmpty(token)) {
      return true;
    }
    if (StringUtils.isBlank(token)) {
      token = request.getParameter(jwtUtils.getHeader());
    }
    //凭证为空
    if (StringUtils.isBlank(token)) {
      throw new RRException(jwtUtils.getHeader() + "不能为空", HttpStatus.UNAUTHORIZED.value());
    }

    Claims claims = jwtUtils.getClaimByToken(token);
    if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
      throw new RRException(jwtUtils.getHeader() + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
    }
    request.setAttribute(USER_KEY, claims.getSubject());

    return true;
  }

  /**
   * 将某个对象转换成json格式并发送到客户端
   */
  public static void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
    response.setContentType("application/json; charset=utf-8");
    PrintWriter writer = response.getWriter();
    writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
        SerializerFeature.WriteDateUseDateFormat));
    writer.close();
    response.flushBuffer();
  }
}
