package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;

public interface Auctioneer {

    void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime);

}
