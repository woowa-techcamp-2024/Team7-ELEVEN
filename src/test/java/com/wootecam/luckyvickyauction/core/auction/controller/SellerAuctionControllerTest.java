package com.wootecam.luckyvickyauction.core.auction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.core.auction.controller.dto.CreateAuctionRequest;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PercentagePricePolicy;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.documentation.DocumentationTest;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SellerAuctionControllerTest extends DocumentationTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/198">Github issue</a>
     */
    @Nested
    class 판매자_경매_등록_API {

        @Test
        void ConstantPolicy_경매_생성() throws Exception {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            CreateAuctionRequest request = new CreateAuctionRequest(
                    "productName", 10000L, 100L, 10L, new ConstantPricePolicy(100L), Duration.ofMinutes(10),
                    now.plusHours(1), now.plusHours(2), true);
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(signInInfo);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    ZonedDateTime.now());

            // expect
            mockMvc.perform(post("/auctions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(
                            document("auctions/post/constant_policy/success",
                                    requestFields(
                                            fieldWithPath("productName").description("상품 이름"),
                                            fieldWithPath("originPrice").description("상품 원가"),
                                            fieldWithPath("stock").description("상품 재고"),
                                            fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                            fieldWithPath("pricePolicy").description("가격 정책"),
                                            fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                            fieldWithPath("pricePolicy.variationWidth").description(
                                                    "절대 가격 정책시 가격 절대 변동폭"),
                                            fieldWithPath("variationDuration").description("경매 기간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부"),
                                            fieldWithPath("startedAt").description("경매 시작 시간"),
                                            fieldWithPath("finishedAt").description("경매 종료 시간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부")
                                    )
                            )
                    ).andExpect(status().isOk());
        }

        @Test
        void PercentagePolicy_경매_생성() throws Exception {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            CreateAuctionRequest request = new CreateAuctionRequest(
                    "productName", 10000L, 100L, 10L, new PercentagePricePolicy(10.0), Duration.ofMinutes(10),
                    now.plusHours(1), now.plusHours(2), true);
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(signInInfo);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    ZonedDateTime.now());

            // expect
            mockMvc.perform(post("/auctions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(
                            document("auctions/post/percentage_policy/success",
                                    requestFields(
                                            fieldWithPath("productName").description("상품 이름"),
                                            fieldWithPath("originPrice").description("상품 원가"),
                                            fieldWithPath("stock").description("상품 재고"),
                                            fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                            fieldWithPath("pricePolicy").description("가격 정책"),
                                            fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                            fieldWithPath("pricePolicy.discountRate").description("퍼센트 가격 정책시 가격 할인율"),
                                            fieldWithPath("variationDuration").description("경매 기간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부"),
                                            fieldWithPath("startedAt").description("경매 시작 시간"),
                                            fieldWithPath("finishedAt").description("경매 종료 시간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부")
                                    )
                            )
                    ).andExpect(status().isOk());
        }
    }

    @Nested
    class 판매자_경매_등록_취소_API {

        @Test
        void 판매자_경매_취소() throws Exception {
            // given
            Long auctionId = 1L;
            Member seller = MemberFixture.createSellerWithDefaultPoint();
            SignInInfo signInInfo = new SignInInfo(seller.getId(), Role.SELLER);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    ZonedDateTime.now());

            mockMvc.perform(
                    delete("/auctions/{auctionId}", auctionId)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
            ).andDo(
                    document("auctions/delete/success",
                            requestCookies(cookieWithName("JSESSIONID").description("세션 ID")),
                            pathParameters(parameterWithName("auctionId").description("취소할 경매 ID"))
                    )
            ).andExpect(status().isOk());
        }
    }
}
