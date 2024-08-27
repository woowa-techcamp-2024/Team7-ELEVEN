package com.wootecam.core.domain.entity;

import com.wootecam.core.domain.entity.type.AuctionStatus;
import com.wootecam.core.domain.entity.type.PricePolicy;
import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Auction {

    private static final int MINIMUM_STOCK_COUNT = 1;
    private static final long NANOS_IN_MINUTE = 60_000_000_000L; // 1분의 나노초
    private static final long MAX_AUCTION_DURATION_NANOS = 60 * NANOS_IN_MINUTE; // 60분의 나노초

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
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
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
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            boolean isShowStock
    ) {
        validateAuctionTime(startedAt, finishedAt);
        validateVariationDuration(variationDuration, Duration.between(startedAt, finishedAt));
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

    private void validateAuctionTime(LocalDateTime startedAt, LocalDateTime finishedAt) {
        Duration duration = Duration.between(startedAt, finishedAt);
        long durationNanos = duration.toNanos();

        if (durationNanos > MAX_AUCTION_DURATION_NANOS) {
            long leftNanoSeconds = durationNanos % NANOS_IN_MINUTE;
            String message = String.format("경매 지속 시간은 최대 60분까지만 가능합니다. 현재 초과되는 나노초: %d초", leftNanoSeconds);
            throw new BadRequestException(message, ErrorCode.A007);
        }

        if (durationNanos % NANOS_IN_MINUTE != 0) {
            long leftNanoSeconds = durationNanos % NANOS_IN_MINUTE;
            String message = String.format("경매 지속 시간은 정확히 분 단위여야 합니다. 현재 남는 나노초: %d초", leftNanoSeconds);
            throw new BadRequestException(message, ErrorCode.A030);
        }
    }

    private void validateVariationDuration(Duration variationDuration, Duration auctionDuration) {
        if (!isAllowedDuration(variationDuration, auctionDuration)) {
            String message = String.format("경매 할인 주기는 경매 지속 시간에서 나누었을때 나누어 떨어져야 합니다. 할인 주기 시간(초): %d, 경매 주기 시간(초): %d",
                    variationDuration.getSeconds(), auctionDuration.getSeconds());
            throw new BadRequestException(message, ErrorCode.A028);
        }

        if (auctionDuration.minus(variationDuration).isZero()
                || auctionDuration.minus(variationDuration).isNegative()) {
            throw new BadRequestException("경매 할인 주기 시간은 경매 지속 시간보다 작아야 합니다.", ErrorCode.A029);
        }
    }

    private boolean isAllowedDuration(Duration duration, Duration auctionDuration) {
        if (duration.isZero() || auctionDuration.isZero()) {
            return false;
        }
        long durationSeconds = duration.getSeconds();
        long auctionDurationSeconds = auctionDuration.getSeconds();

        return auctionDurationSeconds % durationSeconds == 0;
    }

    private void validateMinimumPrice(LocalDateTime startedAt, LocalDateTime finishedAt, Duration variationDuration,
                                      long originPrice, PricePolicy pricePolicy) {
        Duration totalDuration = Duration.between(startedAt, finishedAt);
        long variationCount = totalDuration.dividedBy(variationDuration) - 1;
        long discountedPrice = pricePolicy.calculatePriceAtVariation(originPrice, variationCount);

        if (discountedPrice <= 0) {
            String message = String.format("경매 진행 중 가격이 0원 이하가 됩니다. 초기 가격: %d, 할인횟수: %d, 모든 할인 적용 후 가격: %d",
                    originPrice, variationCount, discountedPrice);
            throw new BadRequestException(message, ErrorCode.A021);
        }
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
                    String.format("환불할 재고는 %d보다 작을 수 없습니다. inputStock=%s", MINIMUM_STOCK_COUNT, refundStockAmount),
                    ErrorCode.A015);
        }

        if (newCurrentStock > this.originStock) {
            throw new BadRequestException("환불 후 재고는 원래 재고보다 많을 수 없습니다. inputStock=" + refundStockAmount,
                    ErrorCode.A016);
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
    public void submit(long price, long quantity, LocalDateTime requestTime) {
        AuctionStatus currentStatus = currentStatus(requestTime);
        if (!currentStatus.isRunning()) {
            String message = String.format("진행 중인 경매에만 입찰할 수 있습니다. 현재상태: %s", currentStatus);
            throw new BadRequestException(message, ErrorCode.A013);
        }
        verifyCurrentPrice(price, requestTime);
        verifyPurchaseQuantity(quantity);

        currentStock -= quantity;
    }

    // TODO: [SOLD_OUT의 상태관리는 어떻게 해야할것인가?!] [writeAt: 2024/08/14/11:12] [writeBy: HiiWee]
    public AuctionStatus currentStatus(LocalDateTime requestTime) {
        if (requestTime.isBefore(startedAt)) {
            return AuctionStatus.WAITING;
        }

        if (requestTime.isBefore(finishedAt)) {
            return AuctionStatus.RUNNING;
        }

        return AuctionStatus.FINISHED;
    }

    private void verifyCurrentPrice(long inputPrice, LocalDateTime requestTime) {
        Duration elapsedDuration = Duration.between(startedAt, requestTime);
        long currentVariationCount = elapsedDuration.dividedBy(variationDuration);

        long actualPrice = pricePolicy.calculatePriceAtVariation(originPrice, currentVariationCount);

        if (actualPrice != inputPrice) {
            String message = String.format("입력한 가격으로 상품을 구매할 수 없습니다. 현재가격: %d 입력가격: %d", actualPrice, inputPrice);
            throw new BadRequestException(message, ErrorCode.A022);
        }
    }

    private void verifyPurchaseQuantity(long quantity) {
        if (!canPurchase(quantity)) {
            String message = String.format(
                    "해당 수량만큼 구매할 수 없습니다. 재고: %d, 요청: %d, 인당구매제한: %d", currentStock, quantity,
                    maximumPurchaseLimitCount);
            throw new BadRequestException(message, ErrorCode.A012);
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
