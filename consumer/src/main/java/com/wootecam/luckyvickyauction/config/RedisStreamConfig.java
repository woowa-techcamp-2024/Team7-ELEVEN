package com.wootecam.luckyvickyauction.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.UUID;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RedisStreamConfig {

    @Getter
    @Value("${stream.key}")
    private String streamKey;

    @Getter
    @Value("${stream.consumer.groupName}")
    private String consumerGroupName;

    @Getter
    private String consumerName = UUID.randomUUID().toString();

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
