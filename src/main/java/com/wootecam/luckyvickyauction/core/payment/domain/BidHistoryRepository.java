package com.wootecam.luckyvickyauction.core.payment.domain;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import java.util.List;
import java.util.Optional;

public interface BidHistoryRepository {

    BidHistory save(BidHistory bidHistory);

    Optional<BidHistory> findById(long bidHistoryId);

    Optional<BidHistory> findByIdAndMemberId(long bidHistoryId, Long memberId, Role role);

    List<BidHistory> findAllBy(BuyerReceiptSearchCondition condition);

    List<BidHistory> findAllBy(SellerReceiptSearchCondition condition);
}
