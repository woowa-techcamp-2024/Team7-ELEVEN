package com.wootecam.luckyvickyauction.core.auction.dto;

// 조회할때 조건
public record AuctionSearchCondition(
        int size
) {
    public AuctionSearchCondition {
        validateSizeBetween(1, 100, size);
    }

    private void validateSizeBetween(int from, int to, int size) {
        if (size < from || size > to) {
            throw new IllegalArgumentException("size는 " + from + "보다 크거나 같고 " + to + "보다 작거나 같아야 합니다.");
        }
    }
}
