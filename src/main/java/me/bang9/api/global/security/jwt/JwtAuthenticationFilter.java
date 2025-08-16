package me.bang9.api.global.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.global.config.Whitelist;
import me.bang9.api.global.security.CustomUserDetailsService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip JWT processing for public endpoints
        if (isPublicEndpoint(request.getRequestURI())) {
            log.debug("Skipping JWT authentication for public endpoint: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from Authorization header
            final String token = extractTokenFromHeader(request);

            // Skip if no token or authentication already exists
            if (token == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract username from token
            final String username = jwtService.extractUsername(token);
            if (username == null) {
                log.debug("No username found in token");
                filterChain.doFilter(request, response);
                return;
            }

            // Load user details and validate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(token, userDetails)) {
                // Create authentication token
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                
                // Set authentication details
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Set authentication context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Successfully authenticated user: {}", username);
            } else {
                log.debug("Invalid token for user: {}", username);
            }

        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("Malformed JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.debug("Invalid JWT signature: {}", e.getMessage());
        } catch (UsernameNotFoundException e) {
            log.debug("User not found: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing JWT authentication", e);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * @param request HTTP request
     * @return JWT token or null if not found
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * Check if the request URI is a public endpoint that should skip JWT authentication
     * @param requestUri request URI
     * @return true if public endpoint
     */
    private boolean isPublicEndpoint(String requestUri) {
        // Check against whitelist patterns
        for (String pattern : Whitelist.URL_LIST) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        
        // Additional public endpoints for authentication
        String[] authEndpoints = {
                "/api/v1/auth/**",
                "/error",
                "/favicon.ico"
        };
        
        return Arrays.stream(authEndpoints)
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
}