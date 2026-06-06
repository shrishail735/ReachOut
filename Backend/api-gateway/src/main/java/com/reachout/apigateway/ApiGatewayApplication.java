package com.reachout.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import java.time.Duration;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> jobServiceRoutes() {
        return route("job-auth-route")
                .route(path("/api/auth/**"), http())
                .before(uri("http://localhost:8081"))
                .filter(FilterFunctions.requestRatelimiter(config -> {
                    config.setReplenishRate(10);   // 10 requests per second
                    config.setBurstCapacity(20);   // max burst of 20
                }))
                .build()
                .and(
                        route("job-applications-route")
                                .route(path("/api/applications/**"), http())
                                .before(uri("http://localhost:8081"))
                                .filter(FilterFunctions.requestRatelimiter(config -> {
                                    config.setReplenishRate(5);  // 5 requests per second
                                    config.setBurstCapacity(10);
                                }))
                                .build()
                );
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoutes() {
        return route("notification-route")
                .route(path("/api/notify/**"), http())
                .before(uri("http://localhost:8082"))
                .build();
    }
}