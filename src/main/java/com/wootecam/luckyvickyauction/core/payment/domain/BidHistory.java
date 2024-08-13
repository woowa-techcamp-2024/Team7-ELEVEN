package com.wootecam.luckyvickyauction.core.payment.domain;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
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

    @Builder
    public BidHistory(final Long id, final String productName, final long price, final long quantity,
                      BidStatus bidStatus, final long auctionId, final Member seller, final Member buyer) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.bidStatus = bidStatus;
        this.auctionId = auctionId;
        this.seller = seller;
        this.buyer = buyer;
    }

    public boolean isRefundStatus() {
        return bidStatus.equals(BidStatus.REFUND);
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
}
