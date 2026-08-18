package com.example.bankcards.security;

import com.example.bankcards.service.model.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("[JWT Filter] Processing request: {} {}", method, requestUri);

        if (authHeader == null) {
            log.info("[JWT Filter] Authorization header is missing for request: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.info("[JWT Filter] Authorization header does not start with 'Bearer ' for request: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.info("[JWT Filter] Token extracted successfully from header. Starting validation...");

        if (jwtUtils.validateAccessToken(token)) {
            try {
                List<UserRole> roles = jwtUtils.getRolesFromToken(token);
                UUID uuid = jwtUtils.getUuidFromToken(token);

                log.info("[JWT Filter] Token is valid. Extracted User UUID: {}, Roles: {}", uuid, roles);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> {
                        String roleName = "ROLE_" + role.name();
                        log.info("[JWT Filter] Mapping role to GrantedAuthority: {}", roleName);
                        return new SimpleGrantedAuthority(roleName);
                    })
                    .toList();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    uuid, null, authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("[JWT Filter] User {} authenticated successfully. Security context established.", uuid);
            } catch (Exception e) {
                log.error("[JWT Filter] Error extracting data from valid token", e);
            }
        } else {
            log.error("[JWT Filter] Token validation FAILED (validateAccessToken returned false).");
        }

        filterChain.doFilter(request, response);
    }
}
