package com.wootecam.luckyvickyauction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.service.auctioneer.Auctioneer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LazyAuctioneer implements Auctioneer {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${stream.key}")
    private String streamKey;

    @Override
    public void process(AuctionPurchaseRequestMessage message) {

        String messageType = "purchase";

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
