package me.bang9.api.global.controller;

import lombok.RequiredArgsConstructor;
import me.bang9.api.global.api.ApiResponse;
import me.bang9.api.global.api.code.status.CommonErrorStatus;
import me.bang9.api.global.api.exception.Bang9Exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping
    public ResponseEntity<ApiResponse<Void>> test() {
        throw new Bang9Exception(CommonErrorStatus._BAD_REQUEST);
//        return ApiResponse.onSuccess().toResponseEntity();
    }
}
