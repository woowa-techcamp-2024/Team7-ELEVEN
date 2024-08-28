package com.wootecam.luckyvickyauction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.luckyvickyauction.dto.receipt.command.CreateReceiptCommand;
import com.wootecam.luckyvickyauction.service.auctioneer.Auctioneer;
import com.wootecam.luckyvickyauction.service.receipt.ReceiptService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageRouterService {

    private final Auctioneer auctioneer;
    private final ObjectMapper objectMapper;
    private final ReceiptService receiptService;

    public MessageRouterService(Auctioneer auctioneer, ReceiptService receiptService) {
        this.auctioneer = auctioneer;
        this.receiptService = receiptService;

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
                        purchase(messageType, message, postProcess);
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

    private void purchase(String messageType, Map<Object, Object> message, Runnable postProcess)
            throws JsonProcessingException {
        var objectMessage = objectMapper.readValue((String) message.get(messageType),
                AuctionPurchaseRequestMessage.class);
        try {
            auctioneer.process(objectMessage, postProcess);
        } catch (RuntimeException e) {
            // 경매 입찰(구매) 요청이 실패하면 구매 실패 거래 내역을 생성한다.
            var command = CreateReceiptCommand.builder()
                    .requestId(objectMessage.getRequestId())
                    .quantity(objectMessage.getQuantity())
                    .price(objectMessage.getPrice())
                    .auctionId(objectMessage.getAuctionId())
                    .buyerId(objectMessage.getBuyerId())
                    .status(ReceiptStatus.FAILED)
                    .build();
            receiptService.createReceipt(command, postProcess);
        }
    }
}
