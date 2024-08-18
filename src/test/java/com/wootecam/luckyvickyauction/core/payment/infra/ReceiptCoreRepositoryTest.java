package com.wootecam.luckyvickyauction.core.payment.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.RepositoryTest;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import java.time.ZonedDateTime;
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
            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .bidStatus(BidStatus.BID)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            BidHistory saved = receiptCoreRepository.save(bidHistory);

            // when
            BidHistory found = receiptCoreRepository.findById(saved.getId()).get();

            // then
            assertAll(
                    () -> assertThat(found.getId()).isEqualTo(saved.getId()),
                    () -> assertThat(found.getProductName()).isEqualTo(saved.getProductName()),
                    () -> assertThat(found.getPrice()).isEqualTo(saved.getPrice()),
                    () -> assertThat(found.getQuantity()).isEqualTo(saved.getQuantity()),
                    () -> assertThat(found.getBidStatus()).isEqualTo(saved.getBidStatus()),
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
            Long notExistId = 1L;

            // when
            boolean found = receiptCoreRepository.findById(notExistId).isPresent();

            // then
            assertThat(found).isFalse();
        }
    }

    @Nested
    class 거래내역_저장_시에 {

        @Test
        void 거래내역을_저장한다() {
            // given
            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .bidStatus(BidStatus.BID)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();

            // when
            BidHistory saved = receiptCoreRepository.save(bidHistory);

            // then
            assertAll(
                    () -> assertThat(saved.getId()).isEqualTo(bidHistory.getId()),
                    () -> assertThat(saved.getProductName()).isEqualTo(bidHistory.getProductName()),
                    () -> assertThat(saved.getPrice()).isEqualTo(bidHistory.getPrice()),
                    () -> assertThat(saved.getQuantity()).isEqualTo(bidHistory.getQuantity()),
                    () -> assertThat(saved.getBidStatus()).isEqualTo(bidHistory.getBidStatus()),
                    () -> assertThat(saved.getAuctionId()).isEqualTo(bidHistory.getAuctionId()),
                    () -> assertThat(saved.getSellerId()).isEqualTo(bidHistory.getSellerId()),
                    () -> assertThat(saved.getBuyerId()).isEqualTo(bidHistory.getBuyerId()),
                    () -> assertThat(saved.getCreatedAt()).isEqualTo(bidHistory.getCreatedAt()),
                    () -> assertThat(saved.getUpdatedAt()).isEqualTo(bidHistory.getUpdatedAt())
            );
        }

        @Test
        void 이미_존재하는_id면_거래내역을_수정한다() {
            // given
            BidHistory bidHistory = BidHistory.builder()
                    .id(1L)
                    .productName("상품 이름")
                    .price(1000L)
                    .quantity(1L)
                    .bidStatus(BidStatus.BID)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            receiptCoreRepository.save(bidHistory);

            // when
            bidHistory.markAsRefund();
            BidHistory saved = receiptCoreRepository.save(bidHistory);

            // then
            BidHistory savedBidHistory = receiptCoreRepository.findById(saved.getId()).get();
            assertThat(savedBidHistory.getBidStatus()).isEqualTo(BidStatus.REFUND);
        }
    }
}
