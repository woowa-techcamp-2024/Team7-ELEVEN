package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.wootecam.luckyvickyauction.core.auction.controller.dto.BidRequest;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import io.restassured.http.Cookie;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class AuctionDocument extends DocumentationTest {

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/89">Github issue</a>
     */
    @Nested
    class 구매자_경매_목록_조회 {

        @Test
        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() {
            AuctionSearchCondition condition = new AuctionSearchCondition(0, 2);
            List<BuyerAuctionSimpleInfo> infos = buyerAuctionSimpleInfosSample();
            given(auctionService.getBuyerAuctionSimpleInfos(any())).willReturn(infos);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(condition)
                    .when().get("/auctions")
                    .then().log().all()
                    .apply(document("auctions/getAuctions/success",
                            requestFields(
                                    fieldWithPath("offset").type(JsonFieldType.NUMBER)
                                            .description("조회를 시작할 순서"),
                                    fieldWithPath("size").type(JsonFieldType.NUMBER)
                                            .description("조회할 페이지 크기")
                                            .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }

        private List<BuyerAuctionSimpleInfo> buyerAuctionSimpleInfosSample() {
            List<BuyerAuctionSimpleInfo> infos = new ArrayList<>();

            for (long i = 1; i <= 2; i++) {
                BuyerAuctionSimpleInfo simpleInfo = new BuyerAuctionSimpleInfo(i, "쓸만한 경매품 " + i, i * 2000,
                        ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(30L));
                infos.add(simpleInfo);
            }

            return infos;
        }
    }

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/130">Github issue</a>
     */
    @Nested
    class 판매자_경매_목록_조회 {

        @Test
        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() {
            AuctionSearchCondition condition = new AuctionSearchCondition(10, 2);
            List<SellerAuctionSimpleInfo> infos = sellerAuctionSimpleInfosSample();
            given(auctionService.getSellerAuctionSimpleInfos(any())).willReturn(infos);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(condition)
                    .when().get("/auctions/seller")
                    .then().log().all()
                    .apply(document("auctions/getSellerAuctions/success",
                            requestFields(
                                    fieldWithPath("offset").type(JsonFieldType.NUMBER)
                                            .description("조회를 시작할 순서"),
                                    fieldWithPath("size").type(JsonFieldType.NUMBER)
                                            .description("조회할 페이지 크기")
                                            .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }

        private List<SellerAuctionSimpleInfo> sellerAuctionSimpleInfosSample() {
            List<SellerAuctionSimpleInfo> infos = new ArrayList<>();

            for (long i = 1; i <= 2; i++) {
                SellerAuctionSimpleInfo simpleInfo = new SellerAuctionSimpleInfo(i, "내가 판매하는 경매품 " + i, i * 2000,
                        i * 2000 - 500, i * 100, i * 30,
                        ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(30L));
                infos.add(simpleInfo);
            }

            return infos;
        }
    }

    @Nested
    class 구매자_경매_입찰 {

        @Test
        void 경매_입찰을_성공하면_OK응답을_반환한다() {
            String auctionId = "1";
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();
            BidRequest bidRequest = new BidRequest(10000L, 20L);
            willDoNothing().given(paymentService)
                    .process(any(Member.class), anyLong(), anyLong(), anyLong(), any(ZonedDateTime.class));

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .sessionAttr("signInMember",
                            buyer)  // TODO: [인증 객체 만드는거 기다리기? - String되버리는 문제] [writeAt: 2024/08/19/22:40] [writeBy: chhs2131]
                    .cookie(new Cookie.Builder("JSESSIONID", "session-id-value").build()) // 쿠키 설정
                    .body(bidRequest)
                    .when().post("/auctions/{auctionId}/bids", auctionId)
                    .then().log().all()
                    .apply(document("auctions/bids/success",
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            pathParameters(
                                    parameterWithName("auctionId").description("입찰한 경매의 ID")
                            ),
                            requestFields(
                                    fieldWithPath("price").type(JsonFieldType.NUMBER)
                                            .description("입찰을 희망하는 가격"),
                                    fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                            .description("입찰을 희망하는 수량")
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }

    }

}
