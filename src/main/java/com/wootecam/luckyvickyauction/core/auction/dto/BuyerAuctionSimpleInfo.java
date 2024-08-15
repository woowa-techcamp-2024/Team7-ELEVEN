package com.wootecam.luckyvickyauction.core.auction.dto;

import java.time.ZonedDateTime;

/**
 * 구매자가 경매 목록 조회시 필요한 간단한 정보를 담는 DTO
 *
 * @param id         경매 상품 ID
 * @param title      경매 상품 이름
 * @param price      경매 상품 가격
 * @param startedAt  경매 시작 시간
 * @param finishedAt 경매 종료 시간
 */
public record BuyerAuctionSimpleInfo(
        long id,
        String title,
        long price,
        ZonedDateTime startedAt,
        ZonedDateTime finishedAt
) {
}
