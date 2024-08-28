package com.wootecam.luckyvickyauction.lock;

import com.wootecam.luckyvickyauction.aop.LockProvider;

public class NoOperationLockProvider implements LockProvider {

    @Override
    public void tryLock(String key) {
        // 락 동작을 수행하지 않습니다.
    }

    @Override
    public void unlock(String key) {
        // 락 동작을 수행하지 않습니다.
    }

}
