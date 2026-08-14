//package com.example.bankcards.security;
//
//import com.example.bankcards.service.model.enums.UserRole;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.jspecify.annotations.NonNull;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtUtils jwtUtils;
//
//    @Override
//    protected void doFilterInternal(
//        HttpServletRequest request,
//        @NonNull HttpServletResponse response,
//        @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//
//        if (jwtUtils.validateAccessToken(token)) {
//            List<UserRole> roles = jwtUtils.getRolesFromToken(token);
//            UUID uuid = jwtUtils.getUuidFromToken(token);
//
//            List<SimpleGrantedAuthority> authorities = roles.stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
//                .toList();
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                uuid, null, authorities
//            );
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

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

@Slf4j // Аннотация включает переменную log в классе
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

        log.info("[JWT Filter] Обработка запроса: {} {}", method, requestUri);

        if (authHeader == null) {
            log.info("[JWT Filter] Заголовок Authorization отсутствует для запроса: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.info("[JWT Filter] Заголовок Authorization не начинается с 'Bearer ' для запроса: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.info("[JWT Filter] Токен успешно извлечен из заголовка. Начинаем валидацию...");

        if (jwtUtils.validateAccessToken(token)) {
            try {
                List<UserRole> roles = jwtUtils.getRolesFromToken(token);
                UUID uuid = jwtUtils.getUuidFromToken(token);

                log.info("[JWT Filter] Токен валиден. Извлечен User UUID: {}, Роли из токена: {}", uuid, roles);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> {
                        String roleName = "ROLE_" + role.name();
                        log.info("[JWT Filter] Маппинг роли в GrantedAuthority: {}", roleName);
                        return new SimpleGrantedAuthority(roleName);
                    })
                    .toList();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    uuid, null, authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("[JWT Filter] Пользователь {} успешно аутентифицирован. Контекст безопасности установлен.", uuid);
            } catch (Exception e) {
                log.error("[JWT Filter] Ошибка при извлечении данных из валидного токена", e);
            }
        } else {
            log.error("[JWT Filter] Токен НЕ ПРОШЕЛ валидацию (метод validateAccessToken вернул false).");
        }

        filterChain.doFilter(request, response);
    }
}
