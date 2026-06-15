package com.examen1.api_gateway.security;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        // 1. Si la ruta es pública, pasa directo sin validar token
        if (isPublicRoute(path, method)) {
            return chain.filter(exchange);
        }

        // 2. Extraer y validar la cabecera Authorization
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // 3. Validar la firma y expiración del JWT
        if (!jwtUtil.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 4. Validar si la ruta requiere estrictamente rol de ADMINISTRADOR
        if (isAdminRoute(path, method)) {
            String rol = jwtUtil.getRol(token);

            if (!"ADMIN".equals(rol)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }

        // Si pasó todas las validaciones (o es una ruta permitida para CLIENTE), continúa
        return chain.filter(exchange);
    }

    private boolean isPublicRoute(String path, String method) {
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/usuarios/register");
    }

    private boolean isAdminRoute(String path, String method) {
        // Reglas para Usuarios
        if (path.startsWith("/api/v1/usuarios")) {
            return true;
        }

        // Reglas críticas para RESERVAS:
        if (path.startsWith("/api/v1/reservas")) {
            // El CLIENTE SÍ PUEDE hacer POST (crear), GET a su DNI y PATCH (cancelar).
            // Por lo tanto, si NO es ninguna de esas, entonces es una ruta exclusiva del ADMIN.
            boolean isClienteAllowed = "POST".equals(method)
                    || ("GET".equals(method) && path.contains("/api/v1/reservas/dni/"))
                    || "PATCH".equals(method);

            return !isClienteAllowed; // Si no está permitido para el cliente, es ruta de ADMIN
        }

        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
