package com.wootecam.luckyvickyauction.core.payment.controller;

import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

// @RestController  // TODO: [선행 @Repository가 생길 때, 주석을 풀 것] [writeAt: 2024/08/16/16:12] [writeBy: chhs2131]
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    // 사용자는 자신의 거래 이력 목록을 조회할 수 있다.
    @GetMapping
    public List<BuyerReceiptSimpleInfo> getReceipts(@RequestBody BuyerReceiptSearchCondition condition) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

}
