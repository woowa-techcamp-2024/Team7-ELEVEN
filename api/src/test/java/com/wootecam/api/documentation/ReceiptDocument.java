package com.wootecam.api.documentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.core.domain.entity.type.ReceiptStatus;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.info.BuyerReceiptSimpleInfo;
import com.wootecam.core.dto.receipt.info.ReceiptInfo;
import com.wootecam.core.dto.receipt.info.SellerReceiptSimpleInfo;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;

public class ReceiptDocument extends DocumentationTest {

    @Nested
    class 거래_목록_조회 {

        @Test
        void 구매자는_정상적으로_거래목록을_확인한다() throws Exception {
            BuyerReceiptSearchCondition condition = new BuyerReceiptSearchCondition(3, 10);
            List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfos = buyerReceiptSimpleInfosSample();
            SignInInfo buyerInfo = new SignInInfo(1L, Role.BUYER);
            given(receiptService.getBuyerReceiptSimpleInfos(buyerInfo, condition)).willReturn(
                    buyerReceiptSimpleInfos);
            given(authenticationContext.getPrincipal()).willReturn(buyerInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/receipts/buyer")
                            .queryParam("offset", "3")
                            .queryParam("size", "10")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", buyerInfo))
                    .andDo(
                            MockMvcRestDocumentation.document("receipts/findAllBuyerReceipts/success",
                                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                    CookieDocumentation.requestCookies(
                                            CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                                    ),
                                    RequestDocumentation.queryParameters(
                                            RequestDocumentation.parameterWithName("offset").description("조회를 시작할 순서"),
                                            RequestDocumentation.parameterWithName("size").description("조회할 페이지 크기")
                                                    .attributes(Attributes.key("constraints").value("최소: 1 ~ 최대: 100"))
                                    ),
                                    PayloadDocumentation.responseFields(
                                            PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.STRING)
                                                    .description("거래 내역 ID (UUID)"),
                                            PayloadDocumentation.fieldWithPath("[].auctionId").type(JsonFieldType.NUMBER)
                                                    .description("구매한 경매의 ID"),
                                            PayloadDocumentation.fieldWithPath("[].type").type(JsonFieldType.STRING)
                                                    .description("거래 타입 (구매완료, 환불완료)"),
                                            PayloadDocumentation.fieldWithPath("[].productName").type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            PayloadDocumentation.fieldWithPath("[].quantity").type(JsonFieldType.NUMBER)
                                                    .description("구매 수량"),
                                            PayloadDocumentation.fieldWithPath("[].price").type(JsonFieldType.NUMBER)
                                                    .description("구매 가격")
                                    )
                            )
                    )
                    .andExpect(status().isOk());
        }

        private List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfosSample() {
            List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfos = new ArrayList<>();

            for (long i = 1; i <= 3; i++) {
                BuyerReceiptSimpleInfo receipt = BuyerReceiptSimpleInfo.builder()
                        .id(UUID.randomUUID())
                        .auctionId(i)
                        .price(i * 1000)
                        .productName("내가 구매한 상품" + i)
                        .quantity(i * 10)
                        .type(i % 2 == 1 ? ReceiptStatus.PURCHASED : ReceiptStatus.REFUND)
                        .build();
                buyerReceiptSimpleInfos.add(receipt);
            }

            return buyerReceiptSimpleInfos;
        }

        @Test
        void 판매자는_정상적으로_거래목록을_확인한다() throws Exception {
            SellerReceiptSearchCondition condition = new SellerReceiptSearchCondition(3, 10);
            List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfos = sellerReceiptSimpleInfosSample();
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(receiptService.getSellerReceiptSimpleInfos(sellerInfo, condition)).willReturn(
                    sellerReceiptSimpleInfos);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/receipts/seller")
                            .queryParam("offset", "3")
                            .queryParam("size", "10")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(MockMvcRestDocumentation.document("receipts/findAllSellerReceipts/success",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            CookieDocumentation.requestCookies(
                                    CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            RequestDocumentation.queryParameters(
                                    RequestDocumentation.parameterWithName("offset").description("조회를 시작할 순서"),
                                    RequestDocumentation.parameterWithName("size").description("조회할 페이지 크기")
                                            .attributes(Attributes.key("constraints").value("최소: 1 ~ 최대: 100"))
                            ),
                            PayloadDocumentation.responseFields(
                                    PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.STRING)
                                            .description("거래 내역 ID (UUID)"),
                                    PayloadDocumentation.fieldWithPath("[].auctionId").type(JsonFieldType.NUMBER)
                                            .description("구매한 경매의 ID"),
                                    PayloadDocumentation.fieldWithPath("[].type").type(JsonFieldType.STRING)
                                            .description("거래 타입 (구매완료, 환불완료)"),
                                    PayloadDocumentation.fieldWithPath("[].productName").type(JsonFieldType.STRING)
                                            .description("상품명"),
                                    PayloadDocumentation.fieldWithPath("[].quantity").type(JsonFieldType.NUMBER)
                                            .description("구매 수량"),
                                    PayloadDocumentation.fieldWithPath("[].price").type(JsonFieldType.NUMBER)
                                            .description("구매 가격")
                            )
                    ))
                    .andExpect(status().isOk());
        }

        private List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfosSample() {
            List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfos = new ArrayList<>();

            for (long i = 1; i <= 3; i++) {
                SellerReceiptSimpleInfo receipt = SellerReceiptSimpleInfo.builder()
                        .id(UUID.randomUUID())
                        .auctionId(i)
                        .price(i * 1000)
                        .productName("내가 판매한 상품" + i)
                        .quantity(i * 10)
                        .type(i % 2 == 0 ? ReceiptStatus.PURCHASED : ReceiptStatus.REFUND)
                        .build();
                sellerReceiptSimpleInfos.add(receipt);
            }

            return sellerReceiptSimpleInfos;
        }
    }

    @Nested
    class 거래내역_상세_조회 {

        @Test
        void 거래내역_id에_해당하는_거래내역을_조회한다() throws Exception {
            UUID receiptId = UUID.randomUUID();
            ReceiptInfo receiptInfo = ReceiptInfo.builder()
                    .receiptId(receiptId)
                    .productName("상품명")
                    .price(1000L)
                    .quantity(1L)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now().plusHours(1))
                    .build();
            SignInInfo memberInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(memberInfo);
            given(receiptService.getReceiptInfo(memberInfo, receiptId)).willReturn(receiptInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/receipts/{receiptId}", receiptId)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", memberInfo))
                    .andDo(
                            MockMvcRestDocumentation.document("receipts/findOneMemberReceipt/success",
                                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                    CookieDocumentation.requestCookies(
                                            CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                                    ),
                                    RequestDocumentation.pathParameters(
                                            RequestDocumentation.parameterWithName("receiptId").description("조회할 거래내역 ID")
                                    ),
                                    PayloadDocumentation.responseFields(
                                            PayloadDocumentation.fieldWithPath("receiptId").type(JsonFieldType.STRING)
                                                    .description("거래 내역 ID"),
                                            PayloadDocumentation.fieldWithPath("productName").type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            PayloadDocumentation.fieldWithPath("price").type(JsonFieldType.NUMBER)
                                                    .description("구매 가격"),
                                            PayloadDocumentation.fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                                    .description("구매 수량"),
                                            PayloadDocumentation.fieldWithPath("receiptStatus").type(JsonFieldType.STRING)
                                                    .description("입찰 상태 (구매완료, 환불완료)"),
                                            PayloadDocumentation.fieldWithPath("auctionId").type(JsonFieldType.NUMBER)
                                                    .description("경매 ID"),
                                            PayloadDocumentation.fieldWithPath("sellerId").type(JsonFieldType.NUMBER)
                                                    .description("판매자 ID"),
                                            PayloadDocumentation.fieldWithPath("buyerId").type(JsonFieldType.NUMBER)
                                                    .description("구매자 ID"),
                                            PayloadDocumentation.fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                    .description("경매 입찰 시간"),
                                            PayloadDocumentation.fieldWithPath("updatedAt").type(JsonFieldType.STRING)
                                                    .description("경매 마지막 수정 시간")
                                    )
                            )
                    ).andExpect(status().isOk());
        }
    }
}

