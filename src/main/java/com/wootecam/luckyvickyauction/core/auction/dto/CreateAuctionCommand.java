package com.wootecam.luckyvickyauction.core.auction.dto;

import com.wootecam.luckyvickyauction.core.auction.domain.AuctionType;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * 경매 생성을 위한 명령 객체
 *
 * @param sellerId                  판매자 ID
 * @param productName               상품 이름
 * @param originPrice               상품 원가
 * @param stock                     재고 수량
 * @param maximumPurchaseLimitCount 최대 구매 제한 수량
 * @param auctionType               경매 유형 {@link AuctionType}
 * @param variationWidth            가격 변동 폭
 * @param variationDuration        가격 변동 주기
 * @param startedAt                 경매 시작 시간
 * @param finishedAt                경매 종료 시간
 */
public record CreateAuctionCommand(
    Long sellerId, String productName, int originPrice, int stock, int maximumPurchaseLimitCount,
    AuctionType auctionType, int variationWidth, Duration variationDuration, ZonedDateTime startedAt,
    ZonedDateTime finishedAt
) {
}
