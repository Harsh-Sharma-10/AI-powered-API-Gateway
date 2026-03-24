package com.apigateway.gatewayservice.filter;

import com.apigateway.gatewayservice.config.GatewayConfig;
import com.apigateway.gatewayservice.dto.AIDecision;
import com.apigateway.gatewayservice.dto.RequestStats;
import com.apigateway.gatewayservice.service.AIServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * AI Decision Filter
 * Calls AI service to analyze requests and applies decisions
 *
 * Filter Order: Runs between RequestAnalysisFilter (-2) and RateLimitingFilter (0)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIDecisionFilter implements GlobalFilter, Ordered {

    private final GatewayConfig gatewayConfig;
    private final AIServiceClient aiServiceClient;

    private static final String REQUEST_STATS_ATTR = "REQUEST_STATS";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Skip if AI is disabled
        if (!gatewayConfig.isAiEnabled()) {
            return chain.filter(exchange);
        }

        // Get RequestStats from exchange
        RequestStats stats = exchange.getAttribute(REQUEST_STATS_ATTR);

        if (stats == null) {
            log.warn("RequestStats not found - skipping AI analysis");
            return chain.filter(exchange);
        }

        // Call AI service for analysis
        return aiServiceClient.analyzeRequest(stats)
                .flatMap(decision -> {

                    // Update stats with AI decision
                    stats.setAiDecision(decision.getAction());
                    stats.setAiConfidence(decision.getConfidence());
                    stats.setAiReason(decision.getReason());

                    // Apply decision
                    if ("BLOCK".equals(decision.getAction())) {
                        log.warn("🚫 AI BLOCK: IP={}, Reason={}, Confidence={:.2f}, Threat={}",
                                stats.getIpAddress(),
                                decision.getReason(),
                                decision.getConfidence(),
                                decision.getThreatLevel());

                        return blockRequest(exchange, decision);
                    }

                    // ALLOW - continue to next filter
                    log.info("✅ AI ALLOW: IP={}, Confidence={:.2f}",
                            stats.getIpAddress(),
                            decision.getConfidence());

                    return chain.filter(exchange);
                });
    }

    /**
     * Block request with AI decision details
     */
    private Mono<Void> blockRequest(ServerWebExchange exchange, AIDecision decision) {

        // Determine HTTP status based on threat level
        HttpStatus status = determineHttpStatus(decision.getThreatLevel());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create JSON response
        String jsonResponse = String.format(
                """
                {
                  "error": "Access Denied",
                  "message": "%s",
                  "action": "%s",
                  "confidence": %.2f,
                  "threatLevel": "%s",
                  "recommendation": "%s"
                }
                """,
                decision.getReason(),
                decision.getAction(),
                decision.getConfidence(),
                decision.getThreatLevel(),
                decision.getRecommendedAction() != null ?
                        decision.getRecommendedAction() : "Please contact support if you believe this is an error"
        );

        // Write response
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * Determine HTTP status code based on threat level
     */
    private HttpStatus determineHttpStatus(String threatLevel) {
        return switch (threatLevel) {
            case "CRITICAL" -> HttpStatus.FORBIDDEN;           // 403
            case "HIGH" -> HttpStatus.TOO_MANY_REQUESTS;      // 429
            case "MEDIUM" -> HttpStatus.TOO_MANY_REQUESTS;    // 429
            default -> HttpStatus.TOO_MANY_REQUESTS;          // 429
        };
    }

    @Override
    public int getOrder() {
        return -1; // Run after RequestAnalysisFilter (-2), before RateLimitingFilter (0)
    }
}