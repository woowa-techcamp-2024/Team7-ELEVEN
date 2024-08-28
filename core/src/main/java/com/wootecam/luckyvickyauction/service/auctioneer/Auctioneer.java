package com.wootecam.luckyvickyauction.service.auctioneer;

import com.wootecam.luckyvickyauction.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.dto.auction.message.AuctionRefundRequestMessage;

/**
 * 경매 입찰 로직 분리
 * <a href="{https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/246}">#246</a>
 */
public interface Auctioneer {

    void process(AuctionPurchaseRequestMessage message);

    void refund(AuctionRefundRequestMessage message);

}
