package com.wootecam.luckyvickyauction.documentation;

import static com.wootecam.luckyvickyauction.documentation.DocumentFormatGenerator.getAttribute;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.domain.entity.type.Role;
import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.dto.payment.command.BuyerChargePointCommand;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;

class PaymentDocument extends DocumentationTest {

    @Test
    void 사용자_포인트_충전() throws Exception {
        BuyerChargePointCommand command = new BuyerChargePointCommand(10000);
        SignInInfo sellerInfo = new SignInInfo(1L, Role.BUYER);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/payments/points/charge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(new Cookie("JSESSIONID", "sessionId"))
                        .sessionAttr("signInMember", sellerInfo)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(MockMvcRestDocumentation.document("payment/chargePoint/success",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        CookieDocumentation.requestCookies(
                                CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                        ),
                        PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("amount").type(JsonFieldType.NUMBER)
                                        .description("충전할 포인트 금액")
                                        .attributes(getAttribute("constraints", "Long.MAX_VALUE까지 충전 가능"))
                        )
                ))
                .andExpect(status().isOk());
    }
}
