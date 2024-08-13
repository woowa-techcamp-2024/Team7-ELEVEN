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
    private Member seller;
    private Member buyer;

    @Builder
    public BidHistory(final Long id, final String productName, final long price, final long quantity,
                      BidStatus bidStatus, final Member seller, final Member buyer) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.bidStatus = bidStatus;
        this.seller = seller;
        this.buyer = buyer;
    }
}
