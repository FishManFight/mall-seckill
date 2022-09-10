package com.seckill.db.config;

import com.seckill.db.util.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfig {
    /**
     * 创建IdWorker
     *
     * @return
     */
    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
