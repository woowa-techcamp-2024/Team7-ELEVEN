package com.wootecam.luckyvickyauction.core.auction.domain;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import java.time.Duration;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Auction {

    private static final int MINIMUM_STOCK_COUNT = 1;

    private Long id;
    private final Long sellerId;
    private final String productName;
    private long originPrice;
    private long currentPrice;
    private long originStock;
    private long currentStock;
    private long maximumPurchaseLimitCount;
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private ZonedDateTime startedAt;
    private ZonedDateTime finishedAt;
    private boolean isShowStock;
    private AuctionStatus status;

    @Builder
    public Auction(
            Long id,
            Long sellerId,
            String productName,
            long originPrice,
            long currentPrice,
            long originStock,
            long currentStock,
            long maximumPurchaseLimitCount,
            PricePolicy pricePolicy,
            Duration variationDuration,
            ZonedDateTime startedAt,
            ZonedDateTime finishedAt,
            boolean isShowStock,
            AuctionStatus status
    ) {
        this.id = id;
        this.sellerId = sellerId;
        this.productName = productName;
        this.originPrice = originPrice;
        this.currentPrice = currentPrice;
        this.originStock = originStock;
        this.currentStock = currentStock;
        this.maximumPurchaseLimitCount = maximumPurchaseLimitCount;
        this.pricePolicy = pricePolicy;
        this.variationDuration = variationDuration;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.isShowStock = isShowStock;
        this.status = status;

        pricePolicy.validate(originPrice);
    }

    /**
     * 해당 수량만큼 구매가 가능한지 확인한다. <br> 1. 구매 요청이 0보다 작은지 확인합니다. <br> 2. 인당 구매 수량 제한을 넘기지 않는지 확인합니다. <br> 3. 구매 요청 수량보다 햔재
     * 재고가 많은지 확인합니다.
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

        return currentStock >= quantity;  // 구매 요청 수량보다 재고가 많은지 확인한다.
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

    /**
     * 경매 재고량을 변경합니다 - 변경은 경매 시작 전에만 가능하므로 원래 재고와 현재 재고를 같은 값으로 변경합니다
     */
    public void changeStock(long changeRequestStock) {
        if (changeRequestStock < MINIMUM_STOCK_COUNT) {
            String message = String.format("변경 할 재고는 %d개 이상이어야 합니다. inputStock=%d", MINIMUM_STOCK_COUNT,
                    changeRequestStock);
            throw new BadRequestException(message, ErrorCode.A019);
        }
        originStock = changeRequestStock;
        currentStock = changeRequestStock;
    }

    public boolean isSeller(Long sellerId) {
        return this.sellerId.equals(sellerId);
    }
}
