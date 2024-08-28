package com.wootecam.luckyvickyauction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.service.auctioneer.Auctioneer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageRouterService {

    private final Auctioneer auctioneer;
    private final ObjectMapper objectMapper;

    public MessageRouterService(Auctioneer auctioneer) {
        this.auctioneer = auctioneer;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.objectMapper = objectMapper;
    }

    public void consume(MapRecord<String, Object, Object> mapRecord, Runnable postProcess) {

        log.debug("MessageId: {}", mapRecord.getId());
        log.debug("Stream: {}", mapRecord.getStream());
        log.debug("Body: {}", mapRecord.getValue());

        Map<Object, Object> message = mapRecord.getValue();
        for (Object type : message.keySet()) {
            String messageType = (String) type;

            try {
                switch (messageType) {
                    case "purchase":
                        auctioneer.process(objectMapper.readValue((String) message.get(messageType),
                                AuctionPurchaseRequestMessage.class), postProcess);
                        break;
                    case "refund":
                        auctioneer.refund(objectMapper.readValue((String) message.get(messageType),
                                AuctionRefundRequestMessage.class), postProcess);
                        break;
                    default:
                        log.warn("Unknown message type: {}", messageType);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
