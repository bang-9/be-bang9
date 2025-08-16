package me.bang9.api.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.global.api.Bang9Response;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static me.bang9.api.global.api.code.status.UserErrorStatus.UNAUTHORIZED_ACCESS;

/**
 * JWT Authentication Entry Point
 * 
 * This class handles unauthorized access attempts to protected endpoints.
 * When authentication fails or is missing, it returns a consistent error response
 * following the existing Bang9Response pattern.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        
        log.debug("Unauthorized access attempt to: {} - {}", request.getRequestURI(), authException.getMessage());

        // Set response properties
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create consistent error response using existing pattern
        Bang9Response<Void> errorResponse = Bang9Response.onFailure(
                UNAUTHORIZED_ACCESS.getCode(),
                UNAUTHORIZED_ACCESS.getMessage(),
                UNAUTHORIZED_ACCESS.getHttpStatus()
        );

        // Write error response to the response body
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}