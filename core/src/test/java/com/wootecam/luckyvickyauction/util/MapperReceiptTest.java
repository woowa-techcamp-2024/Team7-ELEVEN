package com.wootecam.luckyvickyauction.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wootecam.luckyvickyauction.domain.entity.Receipt;
import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import com.wootecam.luckyvickyauction.infra.entity.receipt.ReceiptEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/112">#112</a>
 */
abstract class MapperReceiptTest {

    @Nested
    class 거래내역_영속성_엔티티를_도메인_엔티티로_변환하면 {

        @Test
        void 정보가_동일하다() {
            // given
            ReceiptEntity entity = ReceiptEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .auctionId(2L)
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .sellerId(3L)
                    .buyerId(4L)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .updatedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now().plusHours(1))
                    .build();

            // when
            Receipt domainEntity = Mapper.convertToReceipt(entity);

            // then
            assertAll(
                    () -> assertEquals(entity.getId(), domainEntity.getId()),
                    () -> assertEquals(entity.getAuctionId(), domainEntity.getAuctionId()),
                    () -> assertEquals(entity.getProductName(), domainEntity.getProductName()),
                    () -> assertEquals(entity.getPrice(), domainEntity.getPrice()),
                    () -> assertEquals(entity.getQuantity(), domainEntity.getQuantity()),
                    () -> assertEquals(entity.getSellerId(), domainEntity.getSellerId()),
                    () -> assertEquals(entity.getBuyerId(), domainEntity.getBuyerId()),
                    () -> assertEquals(entity.getReceiptStatus(), domainEntity.getReceiptStatus()),
                    () -> assertEquals(entity.getCreatedAt(), domainEntity.getCreatedAt()),
                    () -> assertEquals(entity.getUpdatedAt(), domainEntity.getUpdatedAt())
            );
        }
    }

    @Nested
    class 거래내역_도메인_엔티티를_영속성_엔티티로_변환하면 {

        @Test
        void 정보가_동일하다() {
            // given
            Receipt domainEntity = Receipt.builder()
                    .id(UUID.randomUUID().toString())
                    .auctionId(2L)
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .sellerId(3L)
                    .buyerId(4L)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .updatedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now().plusHours(1))
                    .build();

            // when
            ReceiptEntity entity = Mapper.convertToReceiptEntity(domainEntity);

            // then
            assertAll(
                    () -> assertEquals(domainEntity.getId(), entity.getId()),
                    () -> assertEquals(domainEntity.getAuctionId(), entity.getAuctionId()),
                    () -> assertEquals(domainEntity.getProductName(), entity.getProductName()),
                    () -> assertEquals(domainEntity.getPrice(), entity.getPrice()),
                    () -> assertEquals(domainEntity.getQuantity(), entity.getQuantity()),
                    () -> assertEquals(domainEntity.getSellerId(), entity.getSellerId()),
                    () -> assertEquals(domainEntity.getBuyerId(), entity.getBuyerId()),
                    () -> assertEquals(domainEntity.getReceiptStatus(), entity.getReceiptStatus()),
                    () -> assertEquals(domainEntity.getCreatedAt(), entity.getCreatedAt()),
                    () -> assertEquals(domainEntity.getUpdatedAt(), entity.getUpdatedAt())
            );
        }
    }
}
