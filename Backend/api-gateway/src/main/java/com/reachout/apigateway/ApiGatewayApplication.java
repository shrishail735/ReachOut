package com.reachout.apigateway;

import com.reachout.apigateway.config.LoadBalancerFilter;
import com.reachout.apigateway.config.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Autowired
    private LoggingFilter loggingFilter;

    @Autowired
    private LoadBalancerFilter loadBalancerFilter;

    @Bean
    public RouterFunction<ServerResponse> jobServiceRoutes() {
        return route("job-auth-route")
                .route(path("/api/auth/**"), http())
                .filter(loadBalancerFilter)
                .filter(loggingFilter)
                .build()
                .and(
                        route("job-applications-route")
                                .route(path("/api/applications/**").or(path("/api/dashboard/**")), http())
                                .filter(loadBalancerFilter)
                                .filter(loggingFilter)
                                .build()
                );
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoutes() {
        return route("notification-route")
                .route(path("/api/notify/**"), http())
                .before(uri("http://localhost:8082"))
                .filter(loggingFilter)
                .build();
    }
}