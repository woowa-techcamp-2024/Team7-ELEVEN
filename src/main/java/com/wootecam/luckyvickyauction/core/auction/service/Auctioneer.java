package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;

/**
 * 경매 입찰 로직 분리
 * <a href="{https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/246}">#246</a>
 */
public interface Auctioneer {

    void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime);

}
