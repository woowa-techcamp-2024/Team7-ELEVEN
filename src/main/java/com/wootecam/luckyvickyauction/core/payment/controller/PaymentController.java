package com.wootecam.luckyvickyauction.core.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @RestController  // TODO: [선행 @Repository가 생길 때, 주석을 풀 것] [writeAt: 2024/08/16/16:12] [writeBy: chhs2131]
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    // 사용자는 포인트를 충전한다.
    @PostMapping("/points/charge")
    public void chargePoint(@RequestParam("amount") long amount) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }
}
