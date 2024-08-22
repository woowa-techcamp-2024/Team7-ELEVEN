package com.wootecam.luckyvickyauction.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Mapper. Auction 관련 테스트
 * <a href="{https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/146}">#146</a>
 */
abstract public class MapperAuctionTest {

    @Nested
    class 경매_영속성_엔티티를 {

        @Test
        void 도메인_엔티티로_변환하면_정보가_동일하다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            AuctionEntity entity = AuctionEntity.builder()
                    .id(1L)
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();

            // when
            Auction auction = Mapper.convertToAuction(entity);

            // then
            assertAll(
                    () -> assertThat(auction).isNotNull(),
                    () -> assertThat(auction.getId()).isEqualTo(1L),
                    () -> assertThat(auction.getSellerId()).isEqualTo(2L),
                    () -> assertThat(auction.getProductName()).isEqualTo("상품 이름"),
                    () -> assertThat(auction.getOriginPrice()).isEqualTo(1000L),
                    () -> assertThat(auction.getCurrentPrice()).isEqualTo(1000L),
                    () -> assertThat(auction.getOriginStock()).isEqualTo(100L),
                    () -> assertThat(auction.getCurrentStock()).isEqualTo(100L),
                    () -> assertThat(auction.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(auction.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(auction.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(auction.getStartedAt()).isEqualTo(now),
                    () -> assertThat(auction.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(auction.isShowStock()).isTrue()
            );
        }
    }

    @Nested
    class 경매_도메인_엔티티를 {

        @Test
        void 영속성_엔티티로_변환하면_정보가_동일하다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Auction auction = Auction.builder()
                    .id(1L)
                    .sellerId(2L)
                    .productName("상품 이름")
                    .originPrice(1000L)
                    .currentPrice(1000L)
                    .originStock(100L)
                    .currentStock(100L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();

            // when
            AuctionEntity entity = Mapper.convertToAuctionEntity(auction);

            // then
            assertAll(
                    () -> assertThat(entity).isNotNull(),
                    () -> assertThat(entity.getId()).isEqualTo(1L),
                    () -> assertThat(entity.getSellerId()).isEqualTo(2L),
                    () -> assertThat(entity.getProductName()).isEqualTo("상품 이름"),
                    () -> assertThat(entity.getOriginPrice()).isEqualTo(1000L),
                    () -> assertThat(entity.getCurrentPrice()).isEqualTo(1000L),
                    () -> assertThat(entity.getOriginStock()).isEqualTo(100L),
                    () -> assertThat(entity.getCurrentStock()).isEqualTo(100L),
                    () -> assertThat(entity.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(entity.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(entity.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(entity.getStartedAt()).isEqualTo(now),
                    () -> assertThat(entity.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(entity.isShowStock()).isTrue()
            );
        }

    }

}
