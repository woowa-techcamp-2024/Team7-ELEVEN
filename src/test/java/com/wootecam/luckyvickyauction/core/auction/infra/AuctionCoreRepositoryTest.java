package com.wootecam.luckyvickyauction.core.auction.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.entity.AuctionEntity;
import com.wootecam.luckyvickyauction.global.config.JpaConfig;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@DataJpaTest
public class AuctionCoreRepositoryTest {

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    @Test
    void 경매의_id로_경매를_조회한다() {
        // given
        ZonedDateTime now = ZonedDateTime.now();
        AuctionEntity auction = AuctionEntity.builder()
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
        auctionJpaRepository.save(auction);

        // then
        AuctionEntity foundAuction = auctionJpaRepository.findById(auction.getId()).get();
        assertAll(
                () -> assertThat(foundAuction.getId()).isEqualTo(auction.getId()),
                () -> assertThat(foundAuction.getSellerId()).isEqualTo(auction.getSellerId()),
                () -> assertThat(foundAuction.getProductName()).isEqualTo(auction.getProductName()),
                () -> assertThat(foundAuction.getOriginPrice()).isEqualTo(auction.getOriginPrice()),
                () -> assertThat(foundAuction.getCurrentPrice()).isEqualTo(auction.getCurrentPrice()),
                () -> assertThat(foundAuction.getOriginStock()).isEqualTo(auction.getOriginStock()),
                () -> assertThat(foundAuction.getCurrentStock()).isEqualTo(auction.getCurrentStock()),
                () -> assertThat(foundAuction.getMaximumPurchaseLimitCount()).isEqualTo(auction.getMaximumPurchaseLimitCount()),
                () -> assertThat(foundAuction.getPricePolicy()).isEqualTo(auction.getPricePolicy()),
                () -> assertThat(foundAuction.getVariationDuration()).isEqualTo(auction.getVariationDuration()),
                () -> assertThat(foundAuction.getStartedAt()).isEqualTo(auction.getStartedAt()),
                () -> assertThat(foundAuction.getFinishedAt()).isEqualTo(auction.getFinishedAt()),
                () -> assertThat(foundAuction.isShowStock()).isEqualTo(auction.isShowStock())
        );
    }
}
