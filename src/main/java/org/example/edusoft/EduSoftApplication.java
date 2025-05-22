// 整个程序的入口点

package org.example.edusoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.example.edusoft.config.FileServerProperties;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("org.example.edusoft.file.mapper")
@EnableConfigurationProperties({FileServerProperties.class, org.example.edusoft.common.properties.FsServerProperties.class})
@MapperScan("org.example.edusoft.mapper")
public class EduSoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduSoftApplication.class, args);
    }

}
