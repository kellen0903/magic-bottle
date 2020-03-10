package com.teorange.magic.bottle.admin;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.util.List;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by kellen on 2018/3/3.
 */
@SpringBootApplication(scanBasePackages = "com.teorange.magic")
@MapperScan({"com.teorange.magic.bottle.admin.modules.*.dao.**",
    "com.teorange.magic.bottle.api.mapper.**"})
public class Application extends WebMvcConfigurerAdapter {

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    super.configureMessageConverters(converters);

    // 初始化转换器
    FastJsonHttpMessageConverter fastConvert = new FastJsonHttpMessageConverter();
    // 初始化一个转换器配置
    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
    fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat);
    // 将配置设置给转换器并添加到HttpMessageConverter转换器列表中
    fastConvert.setFastJsonConfig(fastJsonConfig);

    converters.add(fastConvert);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
