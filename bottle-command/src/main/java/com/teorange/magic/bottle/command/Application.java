package com.teorange.magic.bottle.command;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * Created by kellen on 2018/3/3.
 */
@SpringBootApplication(scanBasePackages = "com.teorange.magic")
@MapperScan("com.teorange.magic.bottle.api.mapper.**")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class Application {

  @Bean//使用@Bean注入fastJsonHttpMessageConvert
  public HttpMessageConverters fastJsonHttpMessageConverters() {
    //1.需要定义一个Convert转换消息的对象
    FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
    //2.添加fastjson的配置信息，比如是否要格式化返回的json数据
    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
        SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullStringAsEmpty,
        SerializerFeature.WriteNullNumberAsZero);
    //3.在convert中添加配置信息
    fastConverter.setFastJsonConfig(fastJsonConfig);
    HttpMessageConverter<?> converter = fastConverter;
    return new HttpMessageConverters(converter);
  }


  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
