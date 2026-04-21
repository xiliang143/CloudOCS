package com.cloudocs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableRedisRepositories
@MapperScan("com.cloudocs.**.mapper")
public class CloudDocsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudDocsApplication.class, args);
    }
}
