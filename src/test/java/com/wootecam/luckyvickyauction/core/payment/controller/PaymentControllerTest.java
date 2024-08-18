package com.wootecam.luckyvickyauction.core.payment.controller;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerChargePointCommand;
import com.wootecam.luckyvickyauction.documentation.DocumentationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PaymentControllerTest extends DocumentationTest {

    @Test
    void 구매자는_충전_API() throws Exception {
        // given
        var command = new BuyerChargePointCommand(10000);
        Member member = MemberFixture.create().build();

        // expect
        mockMvc.perform(post("/payments/points/charge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(new Cookie("JSESSIONID", "sessionId"))
                        .sessionAttr("member", member)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andExpect(status().isOk())
                .andDo(document("post payments/points/charge",
                        requestCookies(
                                cookieWithName("JSESSIONID").description("세션 ID")
                        ),
                        requestFields(
                                fieldWithPath("amount").description("충전할 포인트 금액")
                        )
                ));
    }
}