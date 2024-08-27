package com.wootecam.api.controller;

import com.wootecam.api.controller.annotation.Login;
import com.wootecam.api.controller.annotation.Roles;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.dto.payment.command.BuyerChargePointCommand;
import com.wootecam.core.service.payment.PaymentService;
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
