package com.cafetron.auth;

import com.cafetron.auth.dto.AuthResponse;
import com.cafetron.auth.dto.LoginRequest;
import com.cafetron.auth.dto.RegisterRequest;
import com.cafetron.user.repository.UserRepository;
import com.cafetron.security.JwtUtil;
import com.cafetron.security.UserDetailsServiceImpl;
import com.cafetron.security.UserPrincipal;
import com.cafetron.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager,
                           UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException(
                    "Email already in use: " + request.getEmail());
        }

        if (userRepository.findByEmployeeId(
                request.getEmployeeId()).isPresent()) {
            throw new IllegalStateException(
                    "Employee ID already in use: " + request.getEmployeeId());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmployeeId(request.getEmployeeId());
        user.setDepartment(request.getDepartment());
        user.setRole(request.getRole() != null
                ? request.getRole().toUpperCase()
                : "EMPLOYEE");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtil.generateToken(principal);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Step 1 — find user by employeeId first
        UserPrincipal principal = (UserPrincipal)
                userDetailsService.loadUserByEmployeeId(
                        request.getEmployeeId());

        // Step 2 — authenticate using user.id + password internally
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        String.valueOf(principal.getId()),  // ← user.id
                        request.getPassword()
                )
        );

        // Step 3 — generate token with user.id as subject
        User user = principal.getUser();
        String token = jwtUtil.generateToken(principal);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}