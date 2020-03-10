package com.teorange.magic.bottle.command.config;

import cn.hutool.core.thread.ThreadUtil;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kellen on 2018/6/4. 线程池配置
 */
@Configuration
public class ThreadPoolConfiguration {

  @Bean
  public ThreadPoolExecutor messageThreadPool() {
    // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
    int poolSize = (int) (Runtime.getRuntime().availableProcessors() / (1 - 0.9));
    ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory("message-thread-pool", true);
    return new ThreadPoolExecutor(poolSize, poolSize,
        0L, TimeUnit.MILLISECONDS, //
        new LinkedBlockingQueue<>(), threadFactory);
  }

}
