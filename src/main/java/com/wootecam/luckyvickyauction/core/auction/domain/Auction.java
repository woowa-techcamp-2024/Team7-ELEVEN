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
        validateDurationTime(startedAt, finishedAt);
        validateMinimumPrice(startedAt, finishedAt, variationDuration, originPrice, pricePolicy);

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
    }

    private void validateDurationTime(ZonedDateTime startedAt, ZonedDateTime finishedAt) {
        Duration diff = Duration.between(startedAt, finishedAt);
        long diffNanos = diff.toNanos();
        long tenMinutesInNanos = 10L * 60 * 1_000_000_000; // 10분을 나노초로 변환

        if (!(diffNanos % tenMinutesInNanos == 0 && diffNanos / tenMinutesInNanos <= 6)) {
            String message = String.format("경매 지속 시간은 10분 단위여야하고, 최대 60분까지만 가능합니다. 현재: %.9f분",
                    diffNanos / (60.0 * 1_000_000_000));
            throw new BadRequestException(message, ErrorCode.A008);
        }
    }

    private void validateMinimumPrice(ZonedDateTime startedAt, ZonedDateTime finishedAt, Duration variationDuration,
                                      long originPrice, PricePolicy pricePolicy) {
        Duration totalDuration = Duration.between(startedAt, finishedAt);
        long variationCount = totalDuration.dividedBy(variationDuration) - 1;

        // 현재는 0원이 minimumPrice 추후 필드로 추가될 수 있음
        long discountedPrice = pricePolicy.calculatePriceAtVariation(originPrice, variationCount);
        if (discountedPrice <= 0) {
            String message = String.format("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                    originPrice, variationCount, discountedPrice);
            throw new BadRequestException(message, ErrorCode.A028);
        }
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
    public void changeStock(long changeRequestStock, ZonedDateTime requestTime, Long ownerId) {
        if (!currentStatus(requestTime).isWaiting()) {
            String message = String.format("시작 전인 경매의 재고만 수정할 수 있습니다. 시작시간=%s, 요청시간=%s", startedAt, requestTime);
            throw new BadRequestException(message, ErrorCode.A021);
        }
        if (!isSeller(ownerId)) {
            throw new UnauthorizedException("자신이 등록한 경매만 수정할 수 있습니다.", ErrorCode.A018);
        }
        if (changeRequestStock < MINIMUM_STOCK_COUNT) {
            String message = String.format("변경 할 재고는 %d개 이상이어야 합니다. inputStock=%d", MINIMUM_STOCK_COUNT,
                    changeRequestStock);
            throw new BadRequestException(message, ErrorCode.A019);
        }
        originStock = changeRequestStock;
        currentStock = changeRequestStock;
    }

    /**
     * 현재 재고량을 변경합니다. <br> 1. 변경 재고량이 0보다 작은 경우 예외를 발생시킵니다 <br> 2. 현재 재고 + 변경 재고량이 원래 재고보다 많은 경우 예외를 발생시킵니다 <br> 3. 현재
     * 재고를 변경합니다
     *
     * @param refundStockAmount 변경할 재고량
     */
    public void refundStock(long refundStockAmount) {
        long newCurrentStock = this.currentStock + refundStockAmount;

        if (refundStockAmount < MINIMUM_STOCK_COUNT) {
            throw new BadRequestException(
                    String.format("변경할 재고는 %d보다 작을 수 없습니다. inputStock=%s", MINIMUM_STOCK_COUNT, refundStockAmount),
                    ErrorCode.A022);
        }

        if (newCurrentStock > this.originStock) {
            throw new BadRequestException("변경 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=" + refundStockAmount,
                    ErrorCode.A023);
        }

        this.currentStock = newCurrentStock;
    }


    /**
     * 1. 현재 상태가 진행 중 인지 검증 <br> 2. 현재 가격으로 구매할 수 있는지 검증 <br> 3. 요청 수량만큼의 재고가 남아있는지 검증 <br> 이후 실제 요청을 처리합니다.
     *
     * @param price       구매자가 구매 요청한 가격
     * @param quantity    구매자가 구매할 상품 갯수
     * @param requestTime 구매자가 요청한 시간
     */
    public void submit(long price, long quantity, ZonedDateTime requestTime) {
        AuctionStatus currentStatus = currentStatus(requestTime);
        if (!currentStatus.isRunning()) {
            String message = String.format("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: %s", currentStatus);
            throw new BadRequestException(message, ErrorCode.A016);
        }
        verifyCurrentPrice(price, requestTime);
        verifyPurchaseQuantity(quantity);

        currentStock -= quantity;
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

    private void verifyCurrentPrice(long inputPrice, ZonedDateTime requestTime) {
        Duration elapsedDuration = Duration.between(startedAt, requestTime);
        long currentVariationCount = elapsedDuration.dividedBy(variationDuration);

        long actualPrice = pricePolicy.calculatePriceAtVariation(originPrice, currentVariationCount);

        if (actualPrice != inputPrice) {
            String message = String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d", actualPrice, inputPrice);
            throw new BadRequestException(message, ErrorCode.A029);
        }
    }

    private void verifyPurchaseQuantity(long quantity) {
        if (!canPurchase(quantity)) {
            String message = String.format(
                    "해당 수량만큼 구매할 수 없습니다. 재고: %d, 요청: %d, 인당구매제한: %d", currentStock, quantity,
                    maximumPurchaseLimitCount);
            throw new BadRequestException(message, ErrorCode.A014);
        }
    }

    /**
     * 해당 수량만큼 구매가 가능한지 확인한다. <br> 1. 구매 요청이 0보다 작은지 확인합니다. <br> 2. 인당 구매 수량 제한을 넘기지 않는지 확인합니다. <br> 3. 구매 요청 수량보다 햔재
     * 재고가 많은지 확인합니다.
     *
     * @param quantity 구매를 원하는 수량
     * @return 구매가 가능한 경우 True, 구매가 불가능한 경우 False를 반환한다.
     */
    private boolean canPurchase(long quantity) {
        if (quantity <= 0) {
            return false;
        }
        if (quantity > maximumPurchaseLimitCount) {
            return false;
        }

        return currentStock >= quantity;
    }

    public boolean isSeller(Long sellerId) {
        return this.sellerId.equals(sellerId);
    }
}
