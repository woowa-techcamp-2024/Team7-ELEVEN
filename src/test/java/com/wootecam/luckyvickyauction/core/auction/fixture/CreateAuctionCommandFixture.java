package com.wootecam.luckyvickyauction.core.auction.fixture;

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CreateAuctionCommandFixture {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private Long sellerId = 1L;
        private String productName = "상품이름";
        private long originPrice = 10000L;
        private long stock = 10L;
        private long maximumPurchaseLimitCount = 1L;
        private PricePolicy pricePolicy = PricePolicy.createConstantPricePolicy(100);
        private Duration variationDuration = Duration.ofMinutes(10);
        private ZonedDateTime requestTime = ZonedDateTime.of(2024, 8, 15, 14, 17, 0, 0, ZoneId.of("Asia/Seoul"));
        private ZonedDateTime startedAt = ZonedDateTime.of(2024, 8, 15, 14, 18, 0, 0, ZoneId.of("Asia/Seoul"));
        private ZonedDateTime finishedAt = ZonedDateTime.of(2024, 8, 15, 15, 18, 0, 0, ZoneId.of("Asia/Seoul"));
        private boolean isShowStock = true;

        public Builder sellerId(Long sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder originPrice(long originPrice) {
            this.originPrice = originPrice;
            return this;
        }

        public Builder stock(long stock) {
            this.stock = stock;
            return this;
        }

        public Builder maximumPurchaseLimitCount(long maximumPurchaseLimitCount) {
            this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
            return this;
        }

        public Builder pricePolicy(PricePolicy pricePolicy) {
            this.pricePolicy = pricePolicy;
            return this;
        }

        public Builder variationDuration(Duration variationDuration) {
            this.variationDuration = variationDuration;
            return this;
        }

        public Builder requestTime(ZonedDateTime requestTime) {
            this.requestTime = requestTime;
            return this;
        }

        public Builder startedAt(ZonedDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder finishedAt(ZonedDateTime finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        public Builder isShowStock(boolean isShowStock) {
            this.isShowStock = isShowStock;
            return this;
        }

        public CreateAuctionCommand build() {
            return new CreateAuctionCommand(productName, originPrice, stock, maximumPurchaseLimitCount,
                    pricePolicy, variationDuration, requestTime, startedAt, finishedAt, isShowStock);
        }
    }
}
