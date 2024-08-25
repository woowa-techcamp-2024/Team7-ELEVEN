package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionLockOperation;
import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.global.dto.AuctionRefundRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LettuceLockAuctioneerProxy implements AuctioneerProxy {

    private final BasicAuctioneer basicAuctioneer;
    private final AuctionLockOperation auctionLockOperation;

    @Override
    public void process(AuctionPurchaseRequestMessage message) {
        Long auctionId = message.auctionId();
        auctionLockOperation.lockLimitTry(auctionId, 30);

        try {
            basicAuctioneer.process(message);
        } finally {
            auctionLockOperation.unLock(auctionId);
        }
    }

    @Override
    public void refund(AuctionRefundRequestMessage message) {

    }
}
