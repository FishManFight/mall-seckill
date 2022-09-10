package com.seckill.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    // 访问http://{localhost}:{port}/swagger-ui.html 可以看到API文档
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                // 是否开启 (true 开启  false隐藏,生产环境建议隐藏)
                .enable(true)
                .select()
                // 扫描所有方法作为api
                .apis(RequestHandlerSelectors.any())
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置文档标题(API名称)
                .title("秒杀商城")
                // 文档描述
                .description("")
                // 服务条款URL
                .termsOfServiceUrl("")
                // 版本号
                .version("1.0.0")
                .build();
    }
}