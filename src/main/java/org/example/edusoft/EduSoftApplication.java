// 整个程序的入口点
package org.example.edusoft;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import cn.dev33.satoken.SaManager;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.example.edusoft.mapper")  // 告诉 Spring 和 MyBatis 去指定包路径下扫描所有的 Mapper 接口，并将它们注册为 Spring 容器中的 Bean
@EnableConfigurationProperties({org.example.edusoft.common.properties.FsServerProperties.class})
// @ComponentScan(basePackages = {
//     "org.example.edusoft",
//     "org.example.edusoft.controller",
//     "org.example.edusoft.controller.ai"
// })
public class EduSoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduSoftApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有源
        config.addAllowedOriginPattern("*");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有方法
        config.addAllowedMethod("*");

        // 允许携带cookie
        config.setAllowCredentials(true);

        // 暴露所有响应头
        config.addExposedHeader("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}


