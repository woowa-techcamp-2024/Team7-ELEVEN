package com.wootecam.luckyvickyauction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.service.auctioneer.Auctioneer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LazyAuctioneer implements Auctioneer {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String streamKey;

    public LazyAuctioneer(StringRedisTemplate redisTemplate,
                          @Value("${stream.key}")
                          String streamKey) {
        this.redisTemplate = redisTemplate;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper = objectMapper;
        this.streamKey = streamKey;
    }

    @Override
    public void process(AuctionPurchaseRequestMessage message) {

        String messageType = "purchase";

        try {
            String stringMessage = objectMapper.writeValueAsString(message);

            log.info("직렬화 후 영수증 값: {}", stringMessage);
            StringRecord record = StreamRecords
                    .string(Map.of(messageType, stringMessage))
                    .withStreamKey(streamKey);
            redisTemplate.opsForStream().add(record);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refund(AuctionRefundRequestMessage message) {
        String messageType = "refund";

        try {
            String stringMessage = objectMapper.writeValueAsString(message);

            StringRecord record = StreamRecords
                    .string(Map.of(messageType, stringMessage))
                    .withStreamKey(streamKey);
            redisTemplate.opsForStream().add(record);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
