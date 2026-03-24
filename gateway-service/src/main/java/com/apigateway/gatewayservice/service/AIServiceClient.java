package com.apigateway.gatewayservice.service;


import com.apigateway.gatewayservice.config.GatewayConfig;
import com.apigateway.gatewayservice.dto.AIDecision;
import com.apigateway.gatewayservice.dto.RequestStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Service Client
 * Communicates with the AI decision service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceClient {

    private final GatewayConfig gatewayConfig;
    private final WebClient.Builder webClientBuilder;

    // Simple in-memory cache for AI decisions
    private final Map<String, CachedDecision> decisionCache = new ConcurrentHashMap<>();

    /**
     * Analyze request using AI service
     */
    public Mono<AIDecision> analyzeRequest(RequestStats stats) {

        if (!gatewayConfig.isAiEnabled()) {
            log.debug("AI service is disabled, defaulting to ALLOW");
            return Mono.just(createDefaultDecision("ALLOW", "AI service disabled"));
        }

        // Check cache first
        if (gatewayConfig.isAiUseCache()) {
            String cacheKey = generateCacheKey(stats);
            CachedDecision cached = decisionCache.get(cacheKey);

            if (cached != null && !cached.isExpired()) {
                log.debug("✅ Cache hit for IP: {}", stats.getIpAddress());
                return Mono.just(cached.getDecision());
            }
        }

        // Call AI service
        return callAIService(stats)
                .doOnSuccess(decision -> {
                    // Cache the decision
                    if (gatewayConfig.isAiUseCache()) {
                        cacheDecision(stats, decision);
                    }

                    log.info("🤖 AI Decision: {} (confidence: {:.2f}) for IP: {}",
                            decision.getAction(),
                            decision.getConfidence(),
                            stats.getIpAddress());
                })
                .onErrorResume(error -> {
                    log.error("❌ AI Service error: {}", error.getMessage());

                    // Fallback decision
                    String fallback = gatewayConfig.getAiFallbackOnError();
                    return Mono.just(createDefaultDecision(
                            fallback,
                            "AI service unavailable - using fallback: " + fallback
                    ));
                });
    }

    /**
     * Call AI service via HTTP
     */
    private Mono<AIDecision> callAIService(RequestStats stats) {

        WebClient webClient = webClientBuilder
                .baseUrl(gatewayConfig.getAiServiceUrl())
                .build();

        return webClient.post()
                .uri(gatewayConfig.getAiEndpoint())
                .bodyValue(stats)
                .retrieve()
                .bodyToMono(AIDecision.class)
                .timeout(Duration.ofMillis(gatewayConfig.getAiTimeoutMs()))
                .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(500))); // Retry once after 500ms
    }

    /**
     * Generate cache key from request stats
     */
    private String generateCacheKey(RequestStats stats) {
        return stats.getIpAddress() + ":" + stats.getEndpoint();
    }

    /**
     * Cache AI decision
     */
    private void cacheDecision(RequestStats stats, AIDecision decision) {
        String cacheKey = generateCacheKey(stats);
        long expiryTime = System.currentTimeMillis() +
                (gatewayConfig.getAiCacheTtlSeconds() * 1000L);

        decisionCache.put(cacheKey, new CachedDecision(decision, expiryTime));

        log.debug("📝 Cached decision for: {}", cacheKey);
    }

    /**
     * Create default decision when AI service is unavailable
     */
    private AIDecision createDefaultDecision(String action, String reason) {
        return AIDecision.builder()
                .action(action)
                .confidence(0.5)
                .reason(reason)
                .threatLevel("UNKNOWN")
                .build();
    }

    /**
     * Cached decision with expiry
     */
    private static class CachedDecision {
        private final AIDecision decision;
        private final long expiryTime;

        public CachedDecision(AIDecision decision, long expiryTime) {
            this.decision = decision;
            this.expiryTime = expiryTime;
        }

        public AIDecision getDecision() {
            return decision;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}