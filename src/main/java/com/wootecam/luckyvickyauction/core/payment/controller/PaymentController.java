package com.wootecam.luckyvickyauction.core.payment.controller;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerChargePointCommand;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 사용자는 포인트를 충전한다.
    @PostMapping("/points/charge")
    public ResponseEntity<Void> chargePoint(
            @RequestBody BuyerChargePointCommand command,
            @SessionAttribute("signInMember") Member member
    ) {
        paymentService.chargePoint(member, command.amount());
        return ResponseEntity.ok().build();
    }
}
