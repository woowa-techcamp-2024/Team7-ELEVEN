package com.wootecam.luckyvickyauction.core.payment.controller;

import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.service.BidHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final BidHistoryService bidHistoryService;

    // 사용자는 자신의 거래 이력을 상세 조회할 수 있다.
    @GetMapping("/{receiptId}")
    public ResponseEntity<BidHistoryInfo> getReceipt(@PathVariable("receiptId") Long receiptId) {
        BidHistoryInfo bidHistoryInfo = bidHistoryService.getBidHistoryInfo(receiptId);
        return ResponseEntity.ok(bidHistoryInfo);
    }

    // 사용자는 자신의 거래 이력 목록을 조회할 수 있다.
    @GetMapping
    public List<BuyerReceiptSimpleInfo> getReceipts(@RequestBody BuyerReceiptSearchCondition condition) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }
}
