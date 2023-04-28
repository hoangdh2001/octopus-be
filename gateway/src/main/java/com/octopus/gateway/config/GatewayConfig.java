package com.octopus.gateway.config;

import com.octopus.gateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrix
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service",r -> r.path("/api/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://auth-service"))
                .route("message-service",r -> r.path("/api/channels/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://message-service"))
                .route("workspace-service",r -> r.path("/api/workspaces/**", "/api/tasks/**", "/api/spaces/**", "/api/projects/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://workspace-service"))
                .route("storage-service",r -> r.path("/api/storage/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://storage-service"))
                .build();
    }
}
