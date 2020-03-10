package com.teorange.magic.bottle.command.config;

import com.teorange.magic.bottle.command.interceptor.AuthorizationInterceptor;
import com.teorange.magic.bottle.command.resolver.LoginUserHandlerMethodArgumentResolver;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-04-20 22:30
 */
@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private AuthorizationInterceptor authorizationInterceptor;
  private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

  @Override
  public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {

  }

  @Override
  public void configureContentNegotiation(
      ContentNegotiationConfigurer contentNegotiationConfigurer) {

  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {

  }

  @Override
  public void configureDefaultServletHandling(
      DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {

  }

  @Override
  public void addFormatters(FormatterRegistry formatterRegistry) {

  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {

  }

  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {

  }

  @Override
  public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {

  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {

  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(loginUserHandlerMethodArgumentResolver);
  }

  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {

  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> list) {

  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> list) {

  }

  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

  }

  @Override
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

  }

  @Override
  public Validator getValidator() {
    return null;
  }

  @Override
  public MessageCodesResolver getMessageCodesResolver() {
    return null;
  }
}