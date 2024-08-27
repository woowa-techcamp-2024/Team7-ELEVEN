package com.wootecam.api.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.api.config.JsonConfig;
import com.wootecam.api.context.AuthenticationContext;
import com.wootecam.api.controller.AuthController;
import com.wootecam.api.controller.BuyerAuctionController;
import com.wootecam.api.controller.annotation.CurrentTimeArgumentResolver;
import com.wootecam.api.controller.PaymentController;
import com.wootecam.api.controller.ReceiptController;
import com.wootecam.api.controller.SellerAuctionController;
import com.wootecam.core.service.auction.AuctionService;
import com.wootecam.core.service.auctioneer.Auctioneer;
import com.wootecam.core.service.member.MemberService;
import com.wootecam.core.service.payment.PaymentService;
import com.wootecam.core.service.receipt.ReceiptService;
import com.wootecam.api.documentation.errorcode.FakeErrorCodeController;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
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
                        .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                                .operationPreprocessors()
                                .withRequestDefaults(Preprocessors.prettyPrint())
                                .withResponseDefaults(Preprocessors.prettyPrint()))
                        .build()).log().all();
    }
}
