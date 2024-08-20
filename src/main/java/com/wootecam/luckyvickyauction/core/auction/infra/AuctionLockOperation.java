package com.wootecam.luckyvickyauction.core.auction.infra;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AuctionLockOperation {
    private static final Logger log = LoggerFactory.getLogger(AuctionLockOperation.class);
    private static final String key = "stock:lock";

    private final RedisTemplate<String, Long> redisOperations;

    private static String getKey(Long keyPrefix) {
        return keyPrefix.toString() + ":" + key;
    }

    public boolean lock(long auctionId) {
        String key = getKey(auctionId);
        Boolean success = redisOperations.opsForValue().setIfAbsent(key, 1L);
        return success != null && success;
    }

    public void unLock(long auctionId) {
        String key = getKey(auctionId);
        redisOperations.delete(key);
    }

    public void lockLimitTry(long auctionId, int maxRetry) {
        int retry = 0;
        while (!lock(auctionId)) {
            if (++retry == maxRetry) {
                throw new IllegalStateException("최대 시도 횟수에 도달했습니다. 재시도: " + maxRetry);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("Auction Lock을 획득했습니다. 경매번호: " + auctionId);
    }
}
