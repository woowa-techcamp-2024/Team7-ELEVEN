package com.wootecam.luckyvickyauction.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.core.auction.controller.BuyerAuctionController;
import com.wootecam.luckyvickyauction.core.auction.controller.CurrentTimeArgumentResolver;
import com.wootecam.luckyvickyauction.core.auction.controller.SellerAuctionController;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.controller.AuthController;
import com.wootecam.luckyvickyauction.core.member.domain.AuthenticationContext;
import com.wootecam.luckyvickyauction.core.member.service.MemberService;
import com.wootecam.luckyvickyauction.core.payment.controller.PaymentController;
import com.wootecam.luckyvickyauction.core.payment.controller.ReceiptController;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import com.wootecam.luckyvickyauction.core.payment.service.ReceiptService;
import com.wootecam.luckyvickyauction.documentation.errorcode.FakeErrorCodeController;
import com.wootecam.luckyvickyauction.global.config.JsonConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest({
        FakeErrorCodeController.class,
        AuthController.class,
        SellerAuctionController.class,
        ReceiptController.class,
        BuyerAuctionController.class,
        SellerAuctionController.class,
        PaymentController.class,
        ReceiptController.class
})
@Import(JsonConfig.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DocumentationTest {

    protected MockMvcRequestSpecification docsGiven;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected AuctionService auctionService;

    @MockBean
    protected PaymentService paymentService;

    @MockBean
    protected Auctioneer auctioneer;

    @MockBean
    protected ReceiptService receiptService;

    @MockBean
    protected AuthenticationContext authenticationContext;

    @MockBean
    protected CurrentTimeArgumentResolver currentTimeArgumentResolver;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentation) {
        docsGiven = RestAssuredMockMvc.given()
                .mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(documentationConfiguration(restDocumentation)
                                .operationPreprocessors()
                                .withRequestDefaults(prettyPrint())
                                .withResponseDefaults(prettyPrint()))
                        .build()).log().all();
    }
}
