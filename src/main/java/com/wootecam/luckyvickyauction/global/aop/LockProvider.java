package com.wootecam.luckyvickyauction.global.aop;

public interface LockProvider {

    void tryLock(String key);

    void unlock(String key);

}
