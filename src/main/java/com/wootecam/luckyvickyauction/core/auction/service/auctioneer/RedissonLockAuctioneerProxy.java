package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.global.dto.AuctionPurchaseRequestMessage;
import com.wootecam.luckyvickyauction.global.dto.AuctionRefundRequestMessage;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockAuctioneerProxy implements AuctioneerProxy {

    private final BasicAuctioneer basicAuctioneer;
    private final RedissonClient redissonClient;

    @Override
    public void process(AuctionPurchaseRequestMessage message) {
        RLock rLock = redissonClient.getLock(message.auctionId() + ":auction:lock");

        try {
            boolean available = rLock.tryLock(5, 5, TimeUnit.SECONDS);
            if (!available) {
                throw new IllegalStateException("TimeOut에 도달했습니다.");
            }
            log.debug("레디슨 락 획득! 경매번호: {}", message.buyerId());
            basicAuctioneer.process(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void refund(AuctionRefundRequestMessage message) {

    }
}
