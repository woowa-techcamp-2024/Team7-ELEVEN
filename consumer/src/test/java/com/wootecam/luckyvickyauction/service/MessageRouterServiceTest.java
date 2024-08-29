package com.wootecam.luckyvickyauction.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.receipt.command.CreateReceiptCommand;
import com.wootecam.luckyvickyauction.service.auctioneer.Auctioneer;
import com.wootecam.luckyvickyauction.service.receipt.ReceiptService;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.stream.MapRecord;

class MessageRouterServiceTest {

    private final Auctioneer auctioneer = mock();
    private final ReceiptService receiptService = mock();
    private final MessageRouterService messageRouterService = new MessageRouterService(auctioneer, receiptService);
    private final ObjectMapper objectMapper = objectMapper();

    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Nested
    class process메서드는 {
        @Test
        void 입찰요청이_실패하면_거래_실패_내역을_생성하는_로직을_처리한다() throws JsonProcessingException {
            // given
            MapRecord<String, Object, Object> redisMessage = mock();
            var message = AuctionPurchaseRequestMessage.builder()
                    .requestId("requestId")
                    .buyerId(1L)
                    .auctionId(1L)
                    .price(1000L)
                    .quantity(1L)
                    .requestTime(LocalDateTime.now())
                    .build();
            when(redisMessage.getValue()).thenReturn(Map.of("purchase", objectMapper.writeValueAsString(message)));
            Runnable postProcessLogic = mock();

            doThrow(new RuntimeException("purchase logic failed")).when(auctioneer).process(any(), any());

            // when
            messageRouterService.consume(redisMessage, postProcessLogic);

            // then
            var command = CreateReceiptCommand.builder()
                    .requestId(message.getRequestId())
                    .quantity(message.getQuantity())
                    .price(message.getPrice())
                    .auctionId(message.getAuctionId())
                    .buyerId(message.getBuyerId())
                    .status(ReceiptStatus.FAILED)
                    .build();
            verify(receiptService).createReceipt(eq(command), eq(postProcessLogic));
        }
    }
}