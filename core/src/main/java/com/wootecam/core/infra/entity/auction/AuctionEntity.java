package com.wootecam.core.infra.entity.auction;

import com.wootecam.core.domain.entity.type.PricePolicy;
import com.wootecam.core.util.PricePolicyConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "AUCTION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;
    private String productName;
    private long originPrice;
    private long currentPrice;
    private long originStock;
    private long currentStock;
    private long maximumPurchaseLimitCount;
    @Convert(converter = PricePolicyConverter.class)
    private PricePolicy pricePolicy;
    private Duration variationDuration;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private boolean isShowStock;

    @Builder
    private AuctionEntity(Long id, Long sellerId, String productName, long originPrice, long currentPrice,
                          long originStock, long currentStock, long maximumPurchaseLimitCount, PricePolicy pricePolicy,
                          Duration variationDuration, LocalDateTime startedAt, LocalDateTime finishedAt,
                          boolean isShowStock) {
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
}
