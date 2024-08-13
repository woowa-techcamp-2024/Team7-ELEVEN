package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BidHistoryService {

    private final BidHistoryRepository bidHistoryRepository;

    public BidHistoryInfo getBidHistoryInfo(long bidHistoryId) {
        // 검증
        final BidHistory bidHistory = bidHistoryRepository.findById(bidHistoryId)
            .orElseThrow(() -> new NotFoundException("입찰 내역을 찾을 수 없습니다. 내역 id=" + bidHistoryId, ErrorCode.P002));

        // 반환
        return null;
    }
}
