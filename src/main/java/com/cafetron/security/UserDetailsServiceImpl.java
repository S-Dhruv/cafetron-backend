package com.cafetron.security;

import com.cafetron.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── Called by JwtFilter on every request — loads by user.id ────

    @Override
    public UserDetails loadUserByUsername(String userId)
            throws UsernameNotFoundException {

        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException(
                    "Invalid user id: " + userId);
        }

        return userRepository.findById(id)
                .map(UserPrincipal::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No user found with id: " + userId));
    }

    // ── Called during login only — loads by employeeId ──────────────

    public UserDetails loadUserByEmployeeId(String employeeId)
            throws UsernameNotFoundException {

        return userRepository.findByEmployeeId(employeeId)
                .map(UserPrincipal::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "No user found with employee ID: "
                                        + employeeId));
    }
}