package me.bang9.api.global.controller;

import lombok.RequiredArgsConstructor;
import me.bang9.api.global.api.ApiResponse;
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
        return ApiResponse.onSuccess().toResponseEntity();
    }
}
