package com.wootecam.luckyvickyauction.context;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() throws IOException {
        redisServer = RedisServer.newRedisServer()
                .port(redisPort)
                .setting("requirepass " + redisPassword)
                .build();

        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

}
