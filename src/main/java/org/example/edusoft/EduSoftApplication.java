// 整个程序的入口点
package org.example.edusoft;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.mybatis.spring.annotation.MapperScan;
import cn.dev33.satoken.SaManager;

@SpringBootApplication
@MapperScan("org.example.edusoft.mapper")  // 告诉 Spring 和 MyBatis 去指定包路径下扫描所有的 Mapper 接口，并将它们注册为 Spring 容器中的 Bean
@EnableConfigurationProperties({org.example.edusoft.common.properties.FsServerProperties.class})
public class EduSoftApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduSoftApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

}


