package com.wootecam.api.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import com.wootecam.api.documentation.errorcode.ErrorCodeFieldsSnippet;
import org.junit.jupiter.api.Test;

public class ErrorCodeDocument extends DocumentationTest {

    @Test
    void 에러_코드_생성() {
        ErrorCodeFieldsSnippet errorCodeFieldsSnippet = new ErrorCodeFieldsSnippet("error-code", "error-code-template");

        docsGiven.when().get("/test/error-code")
                .then().log().all()
                .apply(document("error-code", errorCodeFieldsSnippet));
    }
}
