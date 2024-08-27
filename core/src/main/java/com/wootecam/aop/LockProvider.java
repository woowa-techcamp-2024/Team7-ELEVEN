package com.wootecam.aop;

public interface LockProvider {

    void tryLock(String key);

    void unlock(String key);

}
