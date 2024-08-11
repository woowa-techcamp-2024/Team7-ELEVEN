package com.wootecam.luckyvickyauction.core.auction.dto;

/**
 * @param sellerId    판매자의 ID
 * @param productName 판매상품의 이름
 * @param price       현재 단일 상품의 가격
 * @param quantity    현재 경매에 구매할 수 있는 재고 개수
 */
public record AuctionInfo(Long sellerId, String productName, long price, long quantity) {
}
