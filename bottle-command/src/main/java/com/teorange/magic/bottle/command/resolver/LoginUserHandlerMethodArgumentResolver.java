package com.teorange.magic.bottle.command.resolver;

import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.service.MagicUserService;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import com.teorange.magic.bottle.command.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 22:02
 */
@Component
public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Autowired
  private MagicUserService userService;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().isAssignableFrom(MagicUserEntity.class) && parameter
        .hasParameterAnnotation(LoginUser.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
      NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
    //获取用户open ID
    Object object = request
        .getAttribute(AuthorizationInterceptor.USER_KEY, RequestAttributes.SCOPE_REQUEST);
    if (object == null) {
      return null;
    }

    //获取用户信息
    MagicUserEntity user = userService.query(new MagicUserEntity().setOpenId((String) object));
    return user;
  }
}
