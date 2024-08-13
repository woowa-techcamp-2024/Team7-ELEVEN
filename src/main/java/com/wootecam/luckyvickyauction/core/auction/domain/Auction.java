package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Auction {
    private final Long sellerId;
    private final String productName;
    private long originPrice;
    private long currentPrice;
    private int stock;
    private int maximumPurchaseLimitCount;
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;
    private boolean isShowStock;

    @Builder
    private Auction(final ZonedDateTime startedAt, final Long sellerId, final String productName,
                    final long originPrice,
                    final int stock,
                    final int maximumPurchaseLimitCount, final PricePolicy pricePolicy,
                    final Duration variationDuration,
                    final ZonedDateTime finishedAt,
                    final boolean isShowStock
    ) {
        this.startedAt = startedAt;
        this.sellerId = sellerId;
        this.productName = productName;
        this.currentPrice = originPrice;
        this.originPrice = originPrice;
        this.stock = stock;
        this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
        this.pricePolicy = pricePolicy;
        this.variationDuration = variationDuration;
        this.finishedAt = finishedAt;
        this.isShowStock = isShowStock;

        pricePolicy.validate(originPrice);
    }

    public Long getId() {
        // TODO id반환 구현
        return 0L;
    }

    public AuctionStatus getStatus() {
        // TODO 경매 상태 반환 구현
        return AuctionStatus.WAITING;
    }

    public void update() {
        // TODO 경매 옵션을 변경하는 로직 (Update)
    }

    public void updateShowStock(Boolean isShowStock, Long requestSellerId) {
        if (isShowStock != null) {
            this.isShowStock = isShowStock;
        }

        if (!this.sellerId.equals(requestSellerId)) {
            throw new UnauthorizedException("동일한 판매자만 경매의 가격 노출 정책을 변경할 수 있습니다.", ErrorCode.A015);
        }
    }

    public void updatePricePolicy(PricePolicy newPricePolicy, Long requestSellerId) {

        if (newPricePolicy == null) {
            return;
        }

        if (!this.sellerId.equals(requestSellerId)) {
            throw new UnauthorizedException("동일한 판매자만 경매의 가격 정책을 변경할 수 있습니다.", ErrorCode.A015);
        }

        this.pricePolicy = newPricePolicy;
    }
}
