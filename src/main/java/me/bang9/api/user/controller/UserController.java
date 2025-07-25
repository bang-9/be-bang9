package me.bang9.api.user.controller;

import jakarta.validation.Valid;
import me.bang9.api.global.api.ApiResponse;
import me.bang9.api.global.api.code.status.CommonSuccessStatus;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    // TODO: Implement userService in Phase 2
    // @PostMapping
    // public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
    //     return ApiResponse.onSuccess(
    //             CommonSuccessStatus._CREATED.getCode(),
    //             CommonSuccessStatus._CREATED.getMessage(),
    //             userService.createUser(request)
    //     ).toResponseEntity();
    // }
}
