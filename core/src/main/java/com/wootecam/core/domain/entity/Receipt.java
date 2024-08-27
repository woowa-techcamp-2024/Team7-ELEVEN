package com.wootecam.core.domain.entity;

import com.wootecam.core.domain.entity.type.ReceiptStatus;
import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Receipt {

    private UUID id;
    private String productName;
    private long price;
    private long quantity;
    private ReceiptStatus receiptStatus;
    private long auctionId;
    private long sellerId;
    private long buyerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static final String ERROR_VARIATION_UPDATE_AT = "생성 시간보다 수정 시간이 더 작을 수 없습니다. 생성시간: %s, 수정시간: %s";

    @Builder
    public Receipt(
            final UUID id,
            final String productName,
            final long price,
            final long quantity,
            final ReceiptStatus receiptStatus,
            final long auctionId,
            final long sellerId,
            final long buyerId,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.receiptStatus = receiptStatus;
        this.auctionId = auctionId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markAsRefund() {
        if (receiptStatus.equals(ReceiptStatus.REFUND)) {
            throw new BadRequestException("이미 환불된 입찰 내역입니다.", ErrorCode.R002);
        }
        receiptStatus = ReceiptStatus.REFUND;
    }

    public boolean isOwnedBy(long requestUserId) {
        return sellerId == requestUserId || buyerId == requestUserId;
    }

}
