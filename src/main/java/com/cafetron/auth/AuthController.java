package com.cafetron.auth;

import com.cafetron.auth.dto.AuthResponse;
import com.cafetron.auth.dto.LoginRequest;
import com.cafetron.auth.dto.RegisterRequest;
import com.cafetron.security.UserPrincipal;
import com.cafetron.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/users/me")
    public ResponseEntity<Map<String, Object>> me(
            @AuthenticationPrincipal UserPrincipal principal) {

        User user = principal.getUser();

        return ResponseEntity.ok(Map.of(
                "id",         user.getId(),
                "name",       user.getName(),
                "email",      user.getEmail(),
                "role",       user.getRole(),
                "employeeId", user.getEmployeeId() != null
                        ? user.getEmployeeId() : "",
                "department", user.getDepartment() != null
                        ? user.getDepartment() : ""
        ));
    }
}