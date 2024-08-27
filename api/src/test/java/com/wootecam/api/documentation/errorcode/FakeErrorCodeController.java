package com.wootecam.api.documentation.errorcode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeErrorCodeController {

    @GetMapping("/test/error-code")
    public void findErrorCodes() {
    }
}
