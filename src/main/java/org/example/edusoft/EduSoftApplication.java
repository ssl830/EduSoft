// 整个程序的入口点

package org.example.edusoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.mybatis.spring.annotation.MapperScan;

import org.example.edusoft.Config.FileServerProperties;

@SpringBootApplication
@MapperScan("org.example.edusoft.file.mapper")  // 告诉 Spring 和 MyBatis 去指定包路径下扫描所有的 Mapper 接口，并将它们注册为 Spring 容器中的 Bean
@EnableConfigurationProperties({FileServerProperties.class})
public class EduSoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduSoftApplication.class, args);
    }

}
