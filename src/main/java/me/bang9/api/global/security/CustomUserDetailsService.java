package me.bang9.api.global.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.user.entity.UserEntity;
import me.bang9.api.user.model.UserRole;
import me.bang9.api.user.repository.UserJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    /**
     * Load user by username (email) for Spring Security authentication
     * @param email user email address
     * @return UserDetails for authentication
     * @throws UsernameNotFoundException if user not found or deleted
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        // Check if user is soft-deleted
        if (user.getStatus() == Boolean.FALSE) {
            log.warn("Attempt to authenticate deleted user: {}", email);
            throw new UsernameNotFoundException("User account is disabled: " + email);
        }

        log.debug("Successfully loaded user: {} with role: {}", user.getEmail(), user.getRole());

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(mapRolesToAuthorities(user.getRole()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getStatus()) // Use status field for enabled state
                .build();
    }

    /**
     * Map UserRole enum to Spring Security authorities
     * @param userRole user role from entity
     * @return collection of granted authorities
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(UserRole userRole) {
        if (userRole == null) {
            log.warn("User role is null, defaulting to USER role");
            return List.of(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }

        return List.of(new SimpleGrantedAuthority(userRole.getValue()));
    }
}