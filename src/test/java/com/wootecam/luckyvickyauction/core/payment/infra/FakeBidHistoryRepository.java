package com.wootecam.luckyvickyauction.core.payment.infra;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
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

    @Override
    public Optional<BidHistory> findByIdAndMemberId(long bidHistoryId, Long memberId, Role role) {
        if (role.equals(Role.SELLER)) {
            return bidHistories.values()
                    .stream()
                    .filter(bidHistory -> bidHistory.getId() == bidHistoryId && bidHistory.getSellerId() == memberId)
                    .findAny();
        }
        return bidHistories.values()
                .stream()
                .filter(bidHistory -> bidHistory.getId() == bidHistoryId && bidHistory.getBuyerId() == memberId)
                .findAny();
    }

    @Override
    public List<BidHistory> findAllByBuyerId(Long buyerId, BuyerReceiptSearchCondition condition) {
        return bidHistories.values().stream()
                .limit(condition.size())
                .toList();
    }

    @Override
    public List<BidHistory> findAllBy(SellerReceiptSearchCondition condition) {
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
                .sellerId(bidHistory.getSellerId())
                .buyerId(bidHistory.getBuyerId())
                .createdAt(bidHistory.getCreatedAt())
                .updatedAt(bidHistory.getUpdatedAt())
                .build();
    }

}
