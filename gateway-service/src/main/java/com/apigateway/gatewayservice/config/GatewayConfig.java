


package com.apigateway.gatewayservice.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

/**
 * Gateway Configuration
 */
@Configuration
@Getter
public class GatewayConfig {

    // Rate Limiting Settings
    @Value("${gateway.ratelimit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${gateway.ratelimit.burst-threshold:100}")
    private int burstThreshold;

    @Value("${gateway.ratelimit.window-size-seconds:60}")
    private int windowSizeSeconds;

    // Request Analysis Settings
    @Value("${gateway.analysis.enabled:true}")
    private boolean analysisEnabled;

    @Value("${gateway.analysis.suspicious-user-agents:python-requests,curl,wget,scrapy,bot,spider,crawler}")
    private String suspiciousUserAgentsString;

    // Logging
    @Value("${gateway.logging.verbose:false}")
    private boolean verboseLogging;

    // ⭐ NEW: AI Service Settings
    @Value("${gateway.ai.enabled:true}")
    private boolean aiEnabled;

    @Value("${gateway.ai.service-url:http://localhost:8000}")
    private String aiServiceUrl;

    @Value("${gateway.ai.endpoint:/predict}")
    private String aiEndpoint;

    @Value("${gateway.ai.timeout-ms:2000}")
    private int aiTimeoutMs;

    @Value("${gateway.ai.fallback-on-error:ALLOW}")
    private String aiFallbackOnError;

    @Value("${gateway.ai.use-cache:true}")
    private boolean aiUseCache;

    @Value("${gateway.ai.cache-ttl-seconds:60}")
    private int aiCacheTtlSeconds;

    public String[] getSuspiciousUserAgents() {
        return suspiciousUserAgentsString.split(",");
    }

    public String getAiServiceFullUrl() {
        return aiServiceUrl + aiEndpoint;
    }
}