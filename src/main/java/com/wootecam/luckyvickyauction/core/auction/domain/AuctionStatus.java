package com.wootecam.luckyvickyauction.core.auction.domain;

public enum AuctionStatus {
    WAITING, RUNNING, FINISHED;

    public boolean isWaiting() {
        return this == WAITING;
    }

    public boolean isRunning() {
        return this == RUNNING;
    }

    public boolean isFinished() {
        return this == FINISHED;
    }
}
