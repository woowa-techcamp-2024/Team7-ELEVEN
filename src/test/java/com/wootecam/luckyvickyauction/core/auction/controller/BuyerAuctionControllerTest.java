package com.wootecam.luckyvickyauction.core.auction.controller;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.documentation.DocumentationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class BuyerAuctionControllerTest extends DocumentationTest {

    @Nested
    class 구매자_경매_취소_API {

        @Test
        void 성공시() throws Exception {
            // given
            Long receiptId = 1L;
            Member buyer = MemberFixture.create()
                    .build();

            // when
            ResultActions perform = mockMvc.perform(delete("/auctions/bids/{receiptId}", receiptId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .cookie(new Cookie("JSESSIONID", "sessionId"))
                    .sessionAttr("signInMember", buyer)
            );

            // then
            ResultActions docs = perform.andExpect(status().isOk());

            // docs
            docs.andDo(
                    document("auctions/bids/{receiptId}/success",
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            pathParameters(
                                    parameterWithName("receiptId").description("환불할 경매의 영수증 ID")
                            ))
            );
        }
    }

}