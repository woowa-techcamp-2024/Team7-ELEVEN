package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionCommand;

import java.util.List;

public class AuctionService {

    /**
     * 경매 생성
     */
    public void createAuction(CreateAuctionCommand command) {
        // 경매 지속 시간 검증
        Duration diff = Duration.between(command.startedAt(), command.finishedAt());
        if (!(diff.getSeconds() % (60 * 10) == 0 && diff.getSeconds() / (60 * 10) <= 6)) {
            throw new BadRequestException("경매 지속 시간은 10분 단위여야하고, 최대 60분까지만 가능합니다. 현재: " + diff.getSeconds() % (60 * 10), ErrorCode.A008);
        }

        // TODO 로직
    }

    /**
     * 경매 종료
     * 경매 시작 전에는 경매를 종료할 수 있다.
     */
    public void closeAuction(long auctionId) {
    }

    /**
     * 경매 단건 조회
     */
    public AuctionInfo getAuction(long auctionId) {
        return null;
    }

    /**
     * 경매 목록 조회
     */
    public List<AuctionInfo> getAuctions(AuctionSearchCondition condition) {
        return null;
    }

    /**
     * 경매 재고 감소
     */
    public void decreaseStock(long actionId, int quantity) {
    }

    /**
     * 경매 재고 증가
     */
    public void increaseStock(long actionId, int quantity) {
    }

    /**
     * 경매 옵션 변경
     */
    public void changeOption(UpdateAuctionCommand command) {
    }
}
