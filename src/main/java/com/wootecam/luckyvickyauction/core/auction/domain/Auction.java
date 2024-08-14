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
            boolean isShowStock
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

    /**
     * 현재 재고량을 변경합니다. <br> 1. 변경 재고량이 0보다 작은 경우 예외를 발생시킵니다 <br> 2. 현재 재고 + 변경 재고량이 원래 재고보다 많은 경우 예외를 발생시킵니다 <br> 3. 현재 재고를
     * 변경합니다
     *
     * @param refundStockAmount 변경할 재고량
     */
    public void refundStock(long refundStockAmount) {
        long newCurrentStock = this.currentStock + refundStockAmount;

        if (refundStockAmount < MINIMUM_STOCK_COUNT) {
            throw new BadRequestException(String.format("변경할 재고는 %d보다 작을 수 없습니다. inputStock=%s", MINIMUM_STOCK_COUNT, refundStockAmount), ErrorCode.A022);
        }

        if (newCurrentStock > this.originStock) {
            throw new BadRequestException("변경 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=" + refundStockAmount, ErrorCode.A023);
        }

        this.currentStock = newCurrentStock;
    }

    // TODO: [SOLD_OUT의 상태관리는 어떻게 해야할것인가?!] [writeAt: 2024/08/14/11:12] [writeBy: HiiWee]
    public AuctionStatus currentStatus(ZonedDateTime requestTime) {
        if (requestTime.isBefore(startedAt)) {
            return AuctionStatus.WAITING;
        }

        if (requestTime.isBefore(finishedAt)) {
            return AuctionStatus.RUNNING;
        }

        return AuctionStatus.FINISHED;
    }
}
