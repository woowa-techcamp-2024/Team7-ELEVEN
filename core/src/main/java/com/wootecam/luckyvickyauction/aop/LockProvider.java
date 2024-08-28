package com.wootecam.luckyvickyauction.aop;

public interface LockProvider {

    void tryLock(String key);

    void unlock(String key);

}
