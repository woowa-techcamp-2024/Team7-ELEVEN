package com.wootecam.luckyvickyauction.core.payment.domain;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BidHistory {

    private Long id;
    private String productName;
    private long price;
    private long quantity;
    private BidStatus bidStatus;
    private long auctionId;
    private Member seller;
    private Member buyer;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public static final String ERROR_VARIATION_UPDATE_AT = "생성 시간보다 수정 시간이 더 작을 수 없습니다. 생성시간: %s, 수정시간: %s";

    @Builder
    public BidHistory(
            final Long id,
            final String productName,
            final long price,
            final long quantity,
            final BidStatus bidStatus,
            final long auctionId,
            final Member seller,
            final Member buyer,
            final ZonedDateTime createdAt,
            final ZonedDateTime updatedAt) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.bidStatus = bidStatus;
        this.auctionId = auctionId;
        this.seller = seller;
        this.buyer = buyer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 해당 거래 내역을 환불 상태로 전환합니다.
     */
    public void markAsRefund(ZonedDateTime updatedAt) {
        if (bidStatus.equals(BidStatus.REFUND)) {
            throw new BadRequestException("이미 환불된 입찰 내역입니다.", ErrorCode.B005);
        }
        bidStatus = BidStatus.REFUND;
        setUpdatedAt(updatedAt);
    }

    /**
     * 회원이 해당 거래 내역에 구매자 또는 판매자가 맞는지 확인한다.
     *
     * @param member {@link Member}
     * @return 해당 거래 내역에 거래자 또는 판매자인 경우 true
     */
    public boolean isOwnedBy(Member member) {
        if (member == null || member.getSignInId() == null) {
            return false;
        }

        String signInId = member.getSignInId();
        return seller.isSameMember(signInId) || buyer.isSameMember(signInId);
    }

    private void setUpdatedAt(ZonedDateTime updatedAt) {
        if (updatedAt.isBefore(createdAt)) {
            throw new BadRequestException(String.format(ERROR_VARIATION_UPDATE_AT, createdAt, updatedAt),
                    ErrorCode.B006);
        }
        this.updatedAt = updatedAt;
    }
}
