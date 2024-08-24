package com.wootecam.luckyvickyauction.core.lock;

import com.wootecam.luckyvickyauction.global.aop.LockProvider;

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
