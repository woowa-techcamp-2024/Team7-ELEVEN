package com.wootecam.luckyvickyauction.core.payment.repository;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeBidHistoryRepository implements BidHistoryRepository {

    private Map<Long, BidHistory> bidHistories = new HashMap<Long, BidHistory>();
    private long id = 1L;

    @Override
    public BidHistory save(BidHistory bidHistory) {
        if (bidHistory.getId() == null) {
            // 새로운 BidHistory 객체인 경우
            bidHistory = createBidHistory(id, bidHistory);
            id++;
        } else {
            // 기존 BidHistory 객체인 경우 업데이트
            bidHistory = createBidHistory(bidHistory.getId(), bidHistory);
        }

        // BidHistory 객체를 저장소에 저장
        bidHistories.put(bidHistory.getId(), bidHistory);
        return bidHistory;
    }

    @Override
    public Optional<BidHistory> findById(long bidHistoryId) {
        // ID로 BidHistory 검색
        return Optional.ofNullable(bidHistories.get(bidHistoryId));
    }

    // BidHistory 객체를 생성하는 메서드
    private BidHistory createBidHistory(Long id, BidHistory bidHistory) {
        return BidHistory.builder()
                .id(id)
                .productName(bidHistory.getProductName())
                .price(bidHistory.getPrice())
                .quantity(bidHistory.getQuantity())
                .bidStatus(bidHistory.getBidStatus())
                .auctionId(bidHistory.getAuctionId())
                .seller(bidHistory.getSeller())
                .buyer(bidHistory.getBuyer())
                .build();
    }

}