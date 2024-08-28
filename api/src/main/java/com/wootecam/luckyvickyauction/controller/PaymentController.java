package com.wootecam.luckyvickyauction.controller;

import com.wootecam.luckyvickyauction.controller.annotation.Login;
import com.wootecam.luckyvickyauction.controller.annotation.Roles;
import com.wootecam.luckyvickyauction.domain.entity.type.Role;
import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.dto.payment.command.BuyerChargePointCommand;
import com.wootecam.luckyvickyauction.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 사용자는 포인트를 충전한다.
    @Roles({Role.BUYER, Role.SELLER})
    @PostMapping("/points/charge")
    public ResponseEntity<Void> chargePoint(@Login SignInInfo memberInfo,
                                            @RequestBody BuyerChargePointCommand command) {
        paymentService.chargePoint(memberInfo, command.amount());
        return ResponseEntity.ok().build();
    }
}
