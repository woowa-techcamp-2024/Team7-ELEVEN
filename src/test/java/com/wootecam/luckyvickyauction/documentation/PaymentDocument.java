package com.wootecam.luckyvickyauction.documentation;

import static com.wootecam.luckyvickyauction.documentation.DocumentFormatGenerator.getAttribute;
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

import com.wootecam.core.domain.type.Role;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.dto.payment.command.BuyerChargePointCommand;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class PaymentDocument extends DocumentationTest {

    @Test
    void 사용자_포인트_충전() throws Exception {
        BuyerChargePointCommand command = new BuyerChargePointCommand(10000);
        SignInInfo sellerInfo = new SignInInfo(1L, Role.BUYER);

        mockMvc.perform(post("/payments/points/charge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .cookie(new Cookie("JSESSIONID", "sessionId"))
                        .sessionAttr("signInMember", sellerInfo)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(document("payment/chargePoint/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("JSESSIONID").description("세션 ID")
                        ),
                        requestFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전할 포인트 금액")
                                        .attributes(getAttribute("constraints", "Long.MAX_VALUE까지 충전 가능"))
                        )
                ))
                .andExpect(status().isOk());
    }
}
