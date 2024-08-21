package com.wootecam.luckyvickyauction.core.auction.service.auctioneer;

import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonLockAuctioneerProxy implements Auctioneer {

    private final BasicAuctioneer basicAuctioneer;
    private final RedissonClient redissonClient;

    @Override
    public void process(SignInInfo buyerInfo, long price, long auctionId, long quantity, LocalDateTime requestTime) {
        RLock rLock = redissonClient.getLock(auctionId + ":auction:lock");

        try {
            boolean available = rLock.tryLock(5, 5, TimeUnit.SECONDS);
            if (!available) {
                throw new IllegalStateException("TimeOut에 도달했습니다.");
            }
            log.debug("레디슨 락 획득! {}", buyerInfo.id());
            basicAuctioneer.process(buyerInfo, price, auctionId, quantity, requestTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
    }
}
