package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Auction {
    private Long id;
    private final Long sellerId;
    private final String productName;
    private long originPrice;
    private long currentPrice;
    private long stock;
    private long maximumPurchaseLimitCount;
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;
    private boolean isShowStock;
    private AuctionStatus status;

    @Builder
    private Auction(final Long id, final ZonedDateTime startedAt, final Long sellerId, final String productName,
                    final long originPrice,
                    final long stock,
                    final long maximumPurchaseLimitCount, final PricePolicy pricePolicy,
                    final Duration variationDuration,
                    final ZonedDateTime finishedAt,
                    final boolean isShowStock,
                    final AuctionStatus auctionStatus
    ) {
        this.id = id;
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

        if (auctionStatus == null) {
            this.status = AuctionStatus.WAITING;
        } else {
            this.status = auctionStatus;
        }

        pricePolicy.validate(originPrice);
    }

    public void updateStatus() {
        // TODO 임시로직: 추후 스케줄러 등 믿을 수 있는 경매 관리자가 상태를 업데이트 하도록 수정
        ZonedDateTime requestTime = ZonedDateTime.now();

        if (requestTime.isBefore(startedAt)) {
            status = AuctionStatus.WAITING;
        }
        if (requestTime.isAfter(finishedAt)) {
            status = AuctionStatus.FINISHED;
        } else {
            status = (stock <= 0) ? AuctionStatus.SOLD_OUT : AuctionStatus.RUNNING;
        }
    }

    /**
     * 해당 수량만큼 구매가 가능한지 확인한다.
     *
     * @param quantity 구매를 원하는 수량
     * @return 구매가 가능한 경우 True, 구매가 불가능한 경우 False를 반환한다.
     */
    public boolean canPurchase(long quantity) {
        if (quantity <= 0) {  // 구매 요청은 0이거나 더 작을 수 없다.
            return false;
        }

        if (quantity > maximumPurchaseLimitCount) {  // 인당 구매 수량 제한을 넘기지 않는지 확인한다.
            return false;
        }

        return stock >= quantity;  // 구매 요청 수량보다 재고가 많은지 확인한다.
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
