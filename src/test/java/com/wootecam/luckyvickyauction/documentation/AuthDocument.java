package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.member.controller.dto.SignInRequestInfo;
import com.wootecam.luckyvickyauction.core.member.controller.dto.SignUpRequestInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

/**
 * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/167">연관 이슈</a>
 */
public class AuthDocument extends DocumentationTest {

    @Nested
    class 회원가입 {

        @Test
        void 회원가입을_성공하면_200_응답을_받는다() {
            SignUpRequestInfo signUpRequestInfo = new SignUpRequestInfo("userId", "password1234", "BUYER");
            willDoNothing().given(memberService).signUp(any(SignUpRequestInfo.class));

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(signUpRequestInfo)
                    .when().post("/auth/signup")
                    .then().log().all()
                    .apply(document("auth/signup/success",
                            requestFields(
                                    fieldWithPath("signUpId").type(JsonFieldType.STRING)
                                            .description("회원가입을 진행할 아이디"),
                                    fieldWithPath("password").type(JsonFieldType.STRING)
                                            .description("회원가입을 진행할 패스워드"),
                                    fieldWithPath("userRole").type(JsonFieldType.STRING)
                                            .description("거래 권한 설정 (구매자 또는 판매자)")
                                            .attributes(key("constraints").value("BUYER or SELLER"))
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    class 세션_로그인 {

        @Test
        void 로그인에_성공하면_200_응답을_받는다() {
            SignInRequestInfo signInRequestInfo = new SignInRequestInfo("userId", "password");
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(memberService.signIn(any())).willReturn(signInInfo);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(signInRequestInfo)
                    .when().post("/auth/signin")
                    .then().log().all()
                    .apply(document("auth/signin/success",
                            requestFields(
                                    fieldWithPath("signInId").type(JsonFieldType.STRING)
                                            .description("사용자가 입력한 아이디"),
                                    fieldWithPath("password").type(JsonFieldType.STRING)
                                            .description("사용자가 입력한 패스워드")
                            ),
                            responseFields(
                                    fieldWithPath("role").type(JsonFieldType.STRING)
                                            .description("사용자의 거래 권한 (구매자 또는 판매자)")
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    class 세션_로그아웃 {

        @Test
        void 로그아웃을_성공하면_200응답을_반환한다() throws Exception {
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(post("/auth/signout")
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(document("auth/signout/success"))
                    .andExpect(status().isOk());
        }
    }
}
