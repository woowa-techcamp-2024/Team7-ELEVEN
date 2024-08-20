package com.wootecam.luckyvickyauction.core.payment.entity;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RECEIPT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReceiptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private long price;
    private long quantity;
    private BidStatus bidStatus;
    private long auctionId;
    private Long sellerId;
    private Long buyerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private ReceiptEntity(Long id, String productName, long price, long quantity, BidStatus bidStatus, long auctionId,
                          Long sellerId, Long buyerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.bidStatus = bidStatus;
        this.auctionId = auctionId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
