package com.wootecam.api.documentation;

import static com.wootecam.api.documentation.DocumentFormatGenerator.getAttribute;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.dto.payment.command.BuyerChargePointCommand;
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
                                PayloadDocumentation.fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전할 포인트 금액")
                                        .attributes(getAttribute("constraints", "Long.MAX_VALUE까지 충전 가능"))
                        )
                ))
                .andExpect(status().isOk());
    }
}
