package com.wootecam.luckyvickyauction.infra.repository.receipt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.RepositoryTest;
import com.wootecam.luckyvickyauction.domain.entity.Receipt;
import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReceiptCoreRepositoryTest extends RepositoryTest {

    @Autowired
    ReceiptCoreRepository receiptCoreRepository;

    @Nested
    class 거래내역_조회_시에 {

        @Test
        void 거래내역의_id로_거래내역을_조회한다() {
            // given
            Receipt receipt = Receipt.builder()
                    .id(UUID.randomUUID().toString())
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            Receipt saved = receiptCoreRepository.save(receipt);

            // when
            Receipt found = receiptCoreRepository.findById(saved.getId()).get();

            // then
            assertAll(
                    () -> assertThat(found.getId()).isEqualTo(saved.getId()),
                    () -> assertThat(found.getProductName()).isEqualTo(saved.getProductName()),
                    () -> assertThat(found.getPrice()).isEqualTo(saved.getPrice()),
                    () -> assertThat(found.getQuantity()).isEqualTo(saved.getQuantity()),
                    () -> assertThat(found.getReceiptStatus()).isEqualTo(saved.getReceiptStatus()),
                    () -> assertThat(found.getAuctionId()).isEqualTo(saved.getAuctionId()),
                    () -> assertThat(found.getSellerId()).isEqualTo(saved.getSellerId()),
                    () -> assertThat(found.getBuyerId()).isEqualTo(saved.getBuyerId()),
                    () -> assertThat(found.getCreatedAt()).isEqualTo(saved.getCreatedAt()),
                    () -> assertThat(found.getUpdatedAt()).isEqualTo(saved.getUpdatedAt())
            );
        }

        @Test
        void 거래내역_id에_해당하는_거래내역이_없으면_empty를_반환한다() {
            // given
            UUID notExistId = UUID.randomUUID();

            // when
            boolean found = receiptCoreRepository.findById(notExistId.toString()).isPresent();

            // then
            assertThat(found).isFalse();
        }
    }

    @Nested
    class 거래내역_저장_시에 {

        @Test
        void 거래내역을_저장한다() {
            // given
            Receipt receipt = Receipt.builder()
                    .id(UUID.randomUUID().toString())
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // when
            Receipt saved = receiptCoreRepository.save(receipt);

            // then
            assertAll(
                    () -> assertThat(saved.getProductName()).isEqualTo(receipt.getProductName()),
                    () -> assertThat(saved.getPrice()).isEqualTo(receipt.getPrice()),
                    () -> assertThat(saved.getQuantity()).isEqualTo(receipt.getQuantity()),
                    () -> assertThat(saved.getReceiptStatus()).isEqualTo(receipt.getReceiptStatus()),
                    () -> assertThat(saved.getAuctionId()).isEqualTo(receipt.getAuctionId()),
                    () -> assertThat(saved.getSellerId()).isEqualTo(receipt.getSellerId()),
                    () -> assertThat(saved.getBuyerId()).isEqualTo(receipt.getBuyerId()),
                    () -> assertThat(saved.getCreatedAt()).isEqualTo(receipt.getCreatedAt()),
                    () -> assertThat(saved.getUpdatedAt()).isEqualTo(receipt.getUpdatedAt())
            );
        }

        @Test
        void 이미_존재하는_id면_거래내역을_수정한다() {
            // given
            Receipt receipt = Receipt.builder()
                    .id(UUID.randomUUID().toString())
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            receiptCoreRepository.save(receipt);

            // when
            receipt.markAsRefund();
            Receipt saved = receiptCoreRepository.save(receipt);

            // then
            Receipt savedReceipt = receiptCoreRepository.findById(saved.getId()).get();
            assertThat(savedReceipt.getReceiptStatus()).isEqualTo(ReceiptStatus.REFUND);
        }
    }
}
