package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.wootecam.luckyvickyauction.core.member.controller.dto.SignInRequestInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
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
    class 세션_로그인 {

        @Test
        void 로그인에_성공하면_세션에_로그인한_사용자_정보가_담긴다() {
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
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }
    }
}
