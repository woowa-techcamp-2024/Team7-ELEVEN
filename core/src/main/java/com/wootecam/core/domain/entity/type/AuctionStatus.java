package com.wootecam.core.domain.entity.type;

public enum AuctionStatus {
    WAITING, RUNNING, SOLD_OUT, FINISHED;

    public boolean isWaiting() {
        return this == WAITING;
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isSoldOut() {
        return this == SOLD_OUT;
    }

    public boolean isFinished() {
        return this == FINISHED;
    }
}
