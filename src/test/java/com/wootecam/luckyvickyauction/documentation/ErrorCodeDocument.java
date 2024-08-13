package com.wootecam.luckyvickyauction.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import com.wootecam.luckyvickyauction.documentation.errorcode.ErrorCodeFieldsSnippet;
import org.junit.jupiter.api.Test;

public class ErrorCodeDocument extends DocumentationTest {

    @Test
    void 에러_코드를_반환한다() {
        ErrorCodeFieldsSnippet errorCodeFieldsSnippet = new ErrorCodeFieldsSnippet("error-code", "error-code-template");

        docsGiven.when().get("/test/error-code")
                .then().log().all()
                .apply(document("error-code", errorCodeFieldsSnippet));
    }
}
