package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionLockOperation;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LettuceLockAuctioneerProxy implements Auctioneer {

    private final BasicAuctioneer basicAuctioneer;
    private final AuctionLockOperation auctionLockOperation;

    @Override
    public void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime) {
        auctionLockOperation.lockLimitTry(auctionId, 30);

        try {
            basicAuctioneer.process(buyerInfo, price, auctionId, quantity, requestTime);
        } finally {
            auctionLockOperation.unLock(auctionId);
        }
    }
}
