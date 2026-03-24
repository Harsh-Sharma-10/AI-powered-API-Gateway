package com.apigateway.gatewayservice.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Global filter that logs all incoming requests and outgoing responses
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Extract request details
        String requestPath = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().toString();
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

        // Log incoming request
        log.info("╔═══════════════════════════════════════════════════════╗");
        log.info("║   INCOMING REQUEST                                    ║");
        log.info("╠═══════════════════════════════════════════════════════╣");
        log.info("║  Time:        {}                                      ", timestamp);
        log.info("║  Method:      {}                                       ", method);
        log.info("║  Path:        {}                                       ", requestPath);
        log.info("╚═══════════════════════════════════════════════════════╝");

        long startTime = System.currentTimeMillis();

        // Continue filter chain and log response
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null ?
                            exchange.getResponse().getStatusCode().value() : 0;

                    String statusEmoji = getStatusEmoji(statusCode);

                    log.info("╔════════════════════════════════════════════════════════╗");
                    log.info("║  {} RESPONSE                                          ║", statusEmoji);
                    log.info("╠════════════════════════════════════════════════════════╣");
                    log.info("║  Status:      {}                                      ", statusCode);
                    log.info("║  Duration:    {} ms                                   ", duration);
                    log.info("╚════════════════════════════════════════════════════════╝");
                })
        );
    }

    private String getStatusEmoji(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) return "✅";
        if (statusCode >= 300 && statusCode < 400) return "🔄";
        if (statusCode >= 400 && statusCode < 500) return "⚠️";
        if (statusCode >= 500) return "XXX";
        return "?";
    }

    @Override
    public int getOrder() {
        return 1; // Run first
    }
}