package com.wootecam.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.core.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.core.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.core.service.auctioneer.Auctioneer;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRouterService {

    private final Auctioneer auctioneer;
    private final ObjectMapper objectMapper;

    @Transactional
    public void consume(MapRecord<String, Object, Object> mapRecord, Runnable postProcess) {

        log.debug("MessageId: {}", mapRecord.getId());
        log.debug("Stream: {}", mapRecord.getStream());
        log.debug("Body: {}", mapRecord.getValue());

        Map<Object, Object> message = mapRecord.getValue();
        for (Object type : message.keySet()) {
            String messageType = (String) type;

            switch (messageType) {
                case "purchase":
                    auctioneer.process(objectMapper.convertValue(message.get(messageType),
                            AuctionPurchaseRequestMessage.class));
                    break;
                case "refund":
                    auctioneer.refund(objectMapper.convertValue(message.get(messageType),
                            AuctionRefundRequestMessage.class));
                    break;
                default:
                    log.warn("Unknown message type: {}", messageType);
            }
        }
        postProcess.run();
    }
}
