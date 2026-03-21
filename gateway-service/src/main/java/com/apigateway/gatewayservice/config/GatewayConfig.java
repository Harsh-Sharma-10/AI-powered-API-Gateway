


package com.apigateway.gatewayservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

@Configuration
@Getter
public class GatewayConfig {

    @Value("${gateway.ratelimit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${gateway.ratelimit.burst-threshold:100}")
    private int burstThreshold;

    @Value("${gateway.ratelimit.window-size-seconds:60}")
    private int windowSizeSeconds;

    @Value("${gateway.analysis.enabled:true}")
    private boolean analysisEnabled;

    // ⭐ FIXED: Split comma-separated string into array
    @Value("${gateway.analysis.suspicious-user-agents:python-requests,curl,wget,scrapy,bot,spider,crawler}")
    private String suspiciousUserAgentsString;

    @Value("${gateway.logging.verbose:false}")
    private boolean verboseLogging;

    // ⭐ NEW: Method to get array from string
    public String[] getSuspiciousUserAgents() {
        return suspiciousUserAgentsString.split(",");
    }
}