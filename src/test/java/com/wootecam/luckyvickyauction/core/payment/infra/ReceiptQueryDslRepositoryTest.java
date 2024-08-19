package com.wootecam.luckyvickyauction.core.payment.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.wootecam.luckyvickyauction.context.RepositoryTest;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.entity.ReceiptEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReceiptQueryDslRepositoryTest extends RepositoryTest {

    @Autowired
    private ReceiptJpaRepository repository;

    @Nested
    class 구매자_거래_내역_동적_쿼리_실행시 {

        @Test
        void 조회_개수만큼_거래_내역을_조회한다() {

            // given
            Long buyerId = 1L;
            int size = 100;
            var condition = new BuyerReceiptSearchCondition(size);

            for (int i = 0; i < size + 1; i++) {
                repository.save(ReceiptEntity.builder()
                        .productName("상품1")
                        .price(1000)
                        .quantity(1)
                        .bidStatus(BidStatus.BID)
                        .auctionId(4L)
                        .buyerId(buyerId)
                        .sellerId(2L)
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build());
            }

            // when
            List<ReceiptEntity> receipts = repository.findAllByBuyerId(buyerId, condition);

            // then
            assertThat(receipts).hasSize(size);
        }

        /**
         * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/157">거래 내역 조회 시 구매자의 거래 내역만 조회</a>
         */
        @Test
        void 조회한_구매자의_거래_내역만_조회된다() {

            // given
            Long buyerId = 1L;
            Long otherBuyerId = 2L;
            int size = 100;
            var condition = new BuyerReceiptSearchCondition(size);

            repository.save(ReceiptEntity.builder()
                    .productName("상품1")
                    .price(1000)
                    .quantity(1)
                    .bidStatus(BidStatus.BID)
                    .auctionId(4L)
                    .buyerId(buyerId)
                    .sellerId(2L)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build());

            repository.save(ReceiptEntity.builder()
                    .productName("상품1")
                    .price(1000)
                    .quantity(1)
                    .bidStatus(BidStatus.BID)
                    .auctionId(4L)
                    .buyerId(otherBuyerId)
                    .sellerId(2L)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build());
            // when
            List<ReceiptEntity> receipts = repository.findAllByBuyerId(buyerId, condition);

            // then
            assertThat(receipts)
                    .map(ReceiptEntity::getBuyerId)
                    .allMatch(receiptBuyerId -> Objects.equals(receiptBuyerId, buyerId));
        }
    }

    @Nested
    class 판매자_거래_내역_동적_쿼리_실행시 {

        @Test
        void 조회_개수만큼_거래_내역을_조회한다() {
            // given
            Long sellerId = 1L;
            int size = 100;
            var condition = new SellerReceiptSearchCondition(sellerId, size);

            for (int i = 0; i < size + 1; i++) {
                repository.save(ReceiptEntity.builder()
                        .productName("상품1")
                        .price(1000)
                        .quantity(1)
                        .bidStatus(BidStatus.BID)
                        .auctionId(4L)
                        .buyerId(2L)
                        .sellerId(sellerId)
                        .createdAt(ZonedDateTime.now())
                        .updatedAt(ZonedDateTime.now())
                        .build());
            }

            // when
            List<ReceiptEntity> receipts = repository.findAllBy(condition);

            // then
            assertThat(receipts).hasSize(size);
        }

        @Test
        void 조회한_판매자의_거래_내역만_조회된다() {
            // given
            Long sellerId = 1L;
            Long otherSellerId = 2L;
            int size = 100;
            var condition = new SellerReceiptSearchCondition(sellerId, size);

            repository.save(ReceiptEntity.builder()
                    .productName("상품1")
                    .price(1000)
                    .quantity(1)
                    .bidStatus(BidStatus.BID)
                    .auctionId(4L)
                    .buyerId(2L)
                    .sellerId(sellerId)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build());

            repository.save(ReceiptEntity.builder()
                    .productName("상품1")
                    .price(1000)
                    .quantity(1)
                    .bidStatus(BidStatus.BID)
                    .auctionId(4L)
                    .buyerId(2L)
                    .sellerId(otherSellerId)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build());
            // when
            List<ReceiptEntity> receipts = repository.findAllBy(condition);

            // then
            assertThat(receipts)
                    .map(ReceiptEntity::getSellerId)
                    .allMatch(receiptSellerId -> Objects.equals(receiptSellerId, sellerId));
        }
    }
}
