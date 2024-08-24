package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
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
    public void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime) {

        String messageType = "purchase";

        var message = new AuctionPurchaseRequestMessage(
                UUID.randomUUID().toString(),
                buyerInfo.id(),
                price,
                auctionId,
                quantity,
                requestTime
        );

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
