package com.arrowhitech.trungnp.todo_project.controller;

import com.arrowhitech.trungnp.todo_project.dto.ForgetPasswordRequest;
import com.arrowhitech.trungnp.todo_project.dto.LoginRequest;
import com.arrowhitech.trungnp.todo_project.dto.LoginResponse;
import com.arrowhitech.trungnp.todo_project.dto.RefreshTokenRequest;
import com.arrowhitech.trungnp.todo_project.dto.RegisterRequest;
import com.arrowhitech.trungnp.todo_project.dto.ResetPasswordRequest;
import com.arrowhitech.trungnp.todo_project.entity.User;
import com.arrowhitech.trungnp.todo_project.response.BaseResponse;
import com.arrowhitech.trungnp.todo_project.service.AuthService;
import com.arrowhitech.trungnp.todo_project.service.JwtService;
import com.arrowhitech.trungnp.todo_project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        // default role = ROLE_USER
        String defaultRole = "ROLE_USER";
        User user = userService.register(
                req.getUsername(), req.getPassword(), defaultRole
        );

        return ResponseEntity.ok(
                BaseResponse.builder()
                        .status(200)
                        .message("Đăng kí thành công.")
                        .data(user)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(@RequestBody LoginRequest req) {
        // Xác thực thông tin đăng nhập
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );
        String accessToken = jwtService.generateAccessToken(req.getUsername());
        String refreshToken = jwtService.generateRefreshToken(req.getUsername());
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .status(200)
                        .message("Đăng nhập thành công.")
                        .data(new LoginResponse(accessToken, refreshToken))
                        .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<?>> refresh(@RequestBody RefreshTokenRequest req) {
        try {
            LoginResponse loginResponse = authService.refreshToken(req.getRefreshToken());
            return ResponseEntity.ok(
                    BaseResponse.builder()
                            .status(200)
                            .message("Làm mới token thành công.")
                            .data(loginResponse)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    BaseResponse.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<BaseResponse<?>> forgetPassword(@RequestBody ForgetPasswordRequest req) {
        try {
            String resetToken = authService.forgetPassword(req.getUsername());
            // Luôn trả về message thành công để tránh user enumeration attack
            return ResponseEntity.ok(
                    BaseResponse.builder()
                            .status(200)
                            .message("Nếu tên người dùng tồn tại, bạn sẽ nhận được hướng dẫn đặt lại mật khẩu.")
                            .data(resetToken != null ? java.util.Map.of("resetToken", resetToken) : null)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    BaseResponse.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<?>> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            authService.resetPassword(req.getResetToken(), req.getNewPassword());
            return ResponseEntity.ok(
                    BaseResponse.builder()
                            .status(200)
                            .message("Đặt lại mật khẩu thành công.")
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    BaseResponse.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

}
// app todo, vì sao lại gọi authenticate
// làm thêm forget password, gọi auth nhưng chưa dùng
//làm collection postman ,  