package com.wootecam.luckyvickyauction.core.auction.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.context.RepositoryTest;
import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuctionCoreRepositoryTest extends RepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Nested
    class 경매_조회_작업을_수행할_때 {

        @Test
        void 경매의_id로_경매를_조회한다() {
            // given
            ZonedDateTime now = ZonedDateTime.now();
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
            Auction saved = auctionRepository.save(auction);

            // when
            Auction foundAuction = auctionRepository.findById(saved.getId()).get();

            // then
            assertAll(
                    () -> assertThat(foundAuction.getId()).isEqualTo(saved.getId()),
                    () -> assertThat(foundAuction.getSellerId()).isEqualTo(saved.getSellerId()),
                    () -> assertThat(foundAuction.getProductName()).isEqualTo(saved.getProductName()),
                    () -> assertThat(foundAuction.getOriginPrice()).isEqualTo(saved.getOriginPrice()),
                    () -> assertThat(foundAuction.getCurrentPrice()).isEqualTo(saved.getCurrentPrice()),
                    () -> assertThat(foundAuction.getOriginStock()).isEqualTo(saved.getOriginStock()),
                    () -> assertThat(foundAuction.getCurrentStock()).isEqualTo(saved.getCurrentStock()),
                    () -> assertThat(foundAuction.getMaximumPurchaseLimitCount()).isEqualTo(
                            saved.getMaximumPurchaseLimitCount()),
                    () -> assertThat(foundAuction.getPricePolicy()).isEqualTo(saved.getPricePolicy()),
                    () -> assertThat(foundAuction.getVariationDuration()).isEqualTo(saved.getVariationDuration()),
                    () -> assertThat(foundAuction.getStartedAt()).isEqualTo(saved.getStartedAt()),
                    () -> assertThat(foundAuction.getFinishedAt()).isEqualTo(saved.getFinishedAt()),
                    () -> assertThat(foundAuction.isShowStock()).isEqualTo(saved.isShowStock())
            );
        }

        @Test
        void 경매의_id에_해당하는_경매가_없으면_empty를_반환한다() {
            // given
            Long notExistId = 1L;

            // when
            boolean isExist = auctionRepository.findById(notExistId).isPresent();

            // then
            assertThat(isExist).isFalse();
        }
    }

    @Nested
    class 경매_저장_작업을_수행할_때 {

        @Test
        void 경매_도메인_엔티티를_받으면_정상적으로_수행한다() {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            Auction auction = Auction.builder()
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
            Auction save = auctionRepository.save(auction);

            // then
            assertAll(
                    () -> assertThat(save).isNotNull(),
                    () -> assertThat(save.getId()).isNotNull(),
                    () -> assertThat(save.getSellerId()).isEqualTo(2L),
                    () -> assertThat(save.getProductName()).isEqualTo("상품 이름"),
                    () -> assertThat(save.getOriginPrice()).isEqualTo(1000L),
                    () -> assertThat(save.getCurrentPrice()).isEqualTo(1000L),
                    () -> assertThat(save.getOriginStock()).isEqualTo(100L),
                    () -> assertThat(save.getCurrentStock()).isEqualTo(100L),
                    () -> assertThat(save.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(save.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(save.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(save.getStartedAt()).isEqualTo(now),
                    () -> assertThat(save.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(save.isShowStock()).isTrue()
            );

        }

        @Test
        void 이미_등록된_아이디인_경우_정상적으로_수정한다() {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            Auction auction = Auction.builder()
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
            Auction save = auctionRepository.save(auction);

            // when
            Auction newAuction = Auction.builder()
                    .id(save.getId())
                    .sellerId(2L)
                    .productName("멋지게 바뀐 이름")
                    .originPrice(5000L)
                    .currentPrice(5000L)
                    .originStock(500L)
                    .currentStock(500L)
                    .maximumPurchaseLimitCount(10L)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10L))
                    .startedAt(now)
                    .finishedAt(now.plusHours(1))
                    .isShowStock(true)
                    .build();
            Auction newSave = auctionRepository.save(newAuction);

            // then
            assertAll(
                    () -> assertThat(newSave).isNotNull(),
                    () -> assertThat(newSave.getId()).isEqualTo(save.getId()), // ID should be the same
                    () -> assertThat(newSave.getSellerId()).isEqualTo(2L), // ID should be the same
                    () -> assertThat(newSave.getProductName()).isEqualTo("멋지게 바뀐 이름"),
                    () -> assertThat(newSave.getOriginPrice()).isEqualTo(5000L),
                    () -> assertThat(newSave.getCurrentPrice()).isEqualTo(5000L),
                    () -> assertThat(newSave.getOriginStock()).isEqualTo(500L),
                    () -> assertThat(newSave.getCurrentStock()).isEqualTo(500L),
                    () -> assertThat(newSave.getMaximumPurchaseLimitCount()).isEqualTo(10L),
                    () -> assertThat(newSave.getPricePolicy()).isEqualTo(new ConstantPricePolicy(10L)),
                    () -> assertThat(newSave.getVariationDuration()).isEqualTo(Duration.ofMinutes(10L)),
                    () -> assertThat(newSave.getStartedAt()).isEqualTo(now),
                    () -> assertThat(newSave.getFinishedAt()).isEqualTo(now.plusHours(1)),
                    () -> assertThat(newSave.isShowStock()).isTrue()
            );
        }

    }
}
