package com.wootecam.luckyvickyauction.core.auction.dto;

import java.time.LocalDateTime;

/**
 * 판매자 경매 목록 조회시 사용되는 Auction 정보입니다.
 *
 * @param id           경매 ID
 * @param title        경매 노출 제목
 * @param originPrice  원가
 * @param currentPrice 현재 가격
 * @param totalStock   총 재고
 * @param currentStock 현재 남은 재고
 * @param startedAt    시작 시간
 * @param finishedAt   종료 시간
 */
public record SellerAuctionSimpleInfo(
        long id,
        String title,
        long originPrice,
        long currentPrice,
        long totalStock,
        long currentStock,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
}
