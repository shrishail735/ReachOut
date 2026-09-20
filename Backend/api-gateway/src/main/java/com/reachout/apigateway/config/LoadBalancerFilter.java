package com.reachout.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class LoadBalancerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final List<String> instances = List.of(
            "http://localhost:8081"

    );

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public ServerResponse filter(ServerRequest request,
                                 HandlerFunction<ServerResponse> next) throws Exception {

        int index = counter.getAndIncrement() % instances.size();
        String targetInstance = instances.get(index);

        log.info("⚖️ Load Balancer → routing to {}", targetInstance);

        // set the route URI via gateway's attribute — correct way
        request.attributes().put(MvcUtils.GATEWAY_REQUEST_URL_ATTR,
                URI.create(targetInstance));

        return next.handle(request);
    }
}