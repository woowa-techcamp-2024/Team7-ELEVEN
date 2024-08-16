package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import java.util.HashMap;
import java.util.List;
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

    // TODO: [ReceiptSelectCondition 조건 이후 변경 사항] [writeAt: 2024/08/15/16:03] [writeBy: yudonggeun]
    @Override
    public List<BidHistory> findAllBy(BuyerReceiptSearchCondition condition) {
        return bidHistories.values().stream()
                .filter(history -> true)
                .toList();
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
                .createdAt(bidHistory.getCreatedAt())
                .updatedAt(bidHistory.getUpdatedAt())
                .build();
    }

}
