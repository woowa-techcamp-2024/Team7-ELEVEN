package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionLockOperation;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProxyService {

    private final PaymentService paymentService;
    private final AuctionLockOperation auctionLockOperation;

    public void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime) {
        auctionLockOperation.lockLimitTry(auctionId, 30);

        try {
            paymentService.process(buyerInfo, price, auctionId, quantity, requestTime);
        } finally {
            auctionLockOperation.unLock(auctionId);
        }
    }
}
