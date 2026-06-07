package com.reachout.apigateway.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
@Slf4j
public class LoggingFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public ServerResponse filter(ServerRequest request,
                                 HandlerFunction<ServerResponse> next) throws Exception {
        long startTime = System.currentTimeMillis();

        log.info("→ Gateway request: {} {} from {}",
                request.method(),
                request.path(),
                request.remoteAddress().map(Object::toString).orElse("unknown"));

        ServerResponse response = next.handle(request);

        long duration = System.currentTimeMillis() - startTime;
        log.info("← Gateway response: {} {} in {}ms",
                request.method(),
                request.path(),
                duration);

        return response;
    }
}