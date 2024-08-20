package com.wootecam.luckyvickyauction.core.auction.infra;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AuctionStockOperation {
    private static final Logger log = LoggerFactory.getLogger(AuctionStockOperation.class);
    private static final String key = "stock:total";

    private final RedisTemplate<String, Long> redisOperations;

    private static String getKey(Long keyPrefix) {
        return keyPrefix.toString() + ":" + key;
    }

    /**
     * SADD Operation
     */
    public void add(long auctionId, long count) {
        String redisKey = getKey(auctionId);
        redisOperations.opsForValue().increment(redisKey, count);
        log.info("Added stock {} to stock set: {}", redisKey, count);
    }

    /**
     * SREM Operation
     */
    public Long remove(long auctionId, long count) {
        String redisKey = getKey(auctionId);
        Long popCnt = redisOperations.opsForValue().decrement(redisKey, count);
        log.info("Removed stock {} from total stock set: {}", redisKey, count);
        return popCnt;
    }

    /**
     * SCARD Operation
     */
    public Long size(long auctionId) {
        String redisKey = getKey(auctionId);
        Long size = redisOperations.opsForValue().size(redisKey);
        log.info("Stock {} used count: {}", redisKey, size);
        return size != null ? size : 0L;
    }

}
