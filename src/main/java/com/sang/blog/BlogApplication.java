package com.sang.blog;


import com.google.gson.Gson;
import com.sang.blog.commom.utils.RedisUtils;
import com.sang.blog.commom.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

@Slf4j
@SpringBootApplication

public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public SnowflakeIdWorker createIdworker() {

        return new SnowflakeIdWorker(0, 0);
    }

    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisUtils createRedisUtils() {
        return new RedisUtils();
    }

    @Bean
    public Random createRandom() {

        return new Random();
    }

    @Bean
    public Gson createGson(){
        return new Gson();
    }

}
