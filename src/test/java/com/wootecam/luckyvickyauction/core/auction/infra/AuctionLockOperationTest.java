package com.wootecam.luckyvickyauction.core.auction.infra;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuctionLockOperationTest {

    @Autowired
    private AuctionLockOperation auctionLockOperation;

    @Test
    void 여러_스레드가_동시에_연산을_수행해도_수행영역이_독립된다() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        Counter counter = new Counter();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 각 스레드가 락을 시도합니다.
                    auctionLockOperation.lockLimitTry(1L, 10000);

                    int temp = counter.count;
                    Thread.sleep(10);
                    counter.count = temp + 1;

                    // 락이 성공적으로 획득되었으면 락을 해제합니다.
                    auctionLockOperation.unLock(1L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // ExecutorService가 모든 태스크를 완료할 때까지 대기합니다.
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        Assertions.assertThat(counter.count).isEqualTo(numberOfThreads);
    }

    private static class Counter {
        int count = 0;
    }

}
