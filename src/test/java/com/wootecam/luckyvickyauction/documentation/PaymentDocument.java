package com.wootecam.luckyvickyauction.documentation;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerChargePointCommand;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class PaymentDocument extends DocumentationTest {

    @Test
    void 사용자는_포인트를_충전할_수_있다_API() throws Exception {
        // given
        var command = new BuyerChargePointCommand(10000);
        SignInInfo sellerInfo = new SignInInfo(1L, Role.BUYER);

        // expect
        mockMvc.perform(post("/payments/points/charge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(new Cookie("JSESSIONID", "sessionId"))
                        .sessionAttr("signInMember", sellerInfo)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andExpect(status().isOk())
                .andDo(document("post payments/points/charge",
                        requestCookies(
                                cookieWithName("JSESSIONID").description("세션 ID")
                        ),
                        requestFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전할 포인트 금액")
                        )
                ));
    }
}
