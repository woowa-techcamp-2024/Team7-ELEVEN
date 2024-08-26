package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.core.domain.type.ReceiptStatus;
import com.wootecam.core.domain.type.Role;
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
import org.springframework.restdocs.payload.JsonFieldType;

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

            mockMvc.perform(get("/receipts/buyer")
                            .queryParam("offset", "3")
                            .queryParam("size", "10")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", buyerInfo))
                    .andDo(
                            document("receipts/findAllBuyerReceipts/success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestCookies(
                                            cookieWithName("JSESSIONID").description("세션 ID")
                                    ),
                                    queryParameters(
                                            parameterWithName("offset").description("조회를 시작할 순서"),
                                            parameterWithName("size").description("조회할 페이지 크기")
                                                    .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
                                    ),
                                    responseFields(
                                            fieldWithPath("[].id").type(JsonFieldType.STRING)
                                                    .description("거래 내역 ID (UUID)"),
                                            fieldWithPath("[].auctionId").type(JsonFieldType.NUMBER)
                                                    .description("구매한 경매의 ID"),
                                            fieldWithPath("[].type").type(JsonFieldType.STRING)
                                                    .description("거래 타입 (구매완료, 환불완료)"),
                                            fieldWithPath("[].productName").type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            fieldWithPath("[].quantity").type(JsonFieldType.NUMBER)
                                                    .description("구매 수량"),
                                            fieldWithPath("[].price").type(JsonFieldType.NUMBER)
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

            mockMvc.perform(get("/receipts/seller")
                            .queryParam("offset", "3")
                            .queryParam("size", "10")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(document("receipts/findAllSellerReceipts/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            queryParameters(
                                    parameterWithName("offset").description("조회를 시작할 순서"),
                                    parameterWithName("size").description("조회할 페이지 크기")
                                            .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
                            ),
                            responseFields(
                                    fieldWithPath("[].id").type(JsonFieldType.STRING)
                                            .description("거래 내역 ID (UUID)"),
                                    fieldWithPath("[].auctionId").type(JsonFieldType.NUMBER)
                                            .description("구매한 경매의 ID"),
                                    fieldWithPath("[].type").type(JsonFieldType.STRING)
                                            .description("거래 타입 (구매완료, 환불완료)"),
                                    fieldWithPath("[].productName").type(JsonFieldType.STRING)
                                            .description("상품명"),
                                    fieldWithPath("[].quantity").type(JsonFieldType.NUMBER)
                                            .description("구매 수량"),
                                    fieldWithPath("[].price").type(JsonFieldType.NUMBER)
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

            mockMvc.perform(get("/receipts/{receiptId}", receiptId)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", memberInfo))
                    .andDo(
                            document("receipts/findOneMemberReceipt/success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestCookies(
                                            cookieWithName("JSESSIONID").description("세션 ID")
                                    ),
                                    pathParameters(
                                            parameterWithName("receiptId").description("조회할 거래내역 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("receiptId").type(JsonFieldType.STRING)
                                                    .description("거래 내역 ID"),
                                            fieldWithPath("productName").type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            fieldWithPath("price").type(JsonFieldType.NUMBER)
                                                    .description("구매 가격"),
                                            fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                                    .description("구매 수량"),
                                            fieldWithPath("receiptStatus").type(JsonFieldType.STRING)
                                                    .description("입찰 상태 (구매완료, 환불완료)"),
                                            fieldWithPath("auctionId").type(JsonFieldType.NUMBER)
                                                    .description("경매 ID"),
                                            fieldWithPath("sellerId").type(JsonFieldType.NUMBER)
                                                    .description("판매자 ID"),
                                            fieldWithPath("buyerId").type(JsonFieldType.NUMBER)
                                                    .description("구매자 ID"),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                    .description("경매 입찰 시간"),
                                            fieldWithPath("updatedAt").type(JsonFieldType.STRING)
                                                    .description("경매 마지막 수정 시간")
                                    )
                            )
                    ).andExpect(status().isOk());
        }
    }
}

