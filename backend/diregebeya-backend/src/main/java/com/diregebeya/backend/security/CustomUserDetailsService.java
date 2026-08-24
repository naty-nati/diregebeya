package com.diregebeya.backend.security;

import com.diregebeya.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The bridge Spring Security calls during both {@code AuthenticationManager
 * .authenticate(...)} (login) and JWT filter processing (every subsequent
 * request) to load a principal by identifier. We use email as the
 * "username" throughout - there's no separate username field.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email '%s'".formatted(email)));
    }
}
