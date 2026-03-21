package com.apigateway.gatewayservice.util;



import org.springframework.web.server.ServerWebExchange;

/**
 * Utility class for extracting real client IP address
 * Handles proxies, load balancers, and CDNs
 */
public class IPExtractor {


    public static String extractClientIP(ServerWebExchange exchange) {

        // Priority 1: X-Forwarded-For (standard for proxies)
        String xForwardedFor = exchange.getRequest()
                .getHeaders().getFirst("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Format: "client, proxy1, proxy2"
            // We want the first IP (actual client)
            String clientIp = xForwardedFor.split(",")[0].trim();
            if (isValidIP(clientIp)) {
                return clientIp;
            }
        }

        // Priority 2: X-Real-IP (used by Nginx)
        String xRealIp = exchange.getRequest()
                .getHeaders().getFirst("X-Real-IP");

        if (xRealIp != null && !xRealIp.isEmpty() && isValidIP(xRealIp)) {
            return xRealIp;
        }

        // Priority 3: CF-Connecting-IP (Cloudflare)
        String cfConnectingIp = exchange.getRequest()
                .getHeaders().getFirst("CF-Connecting-IP");

        if (cfConnectingIp != null && !cfConnectingIp.isEmpty()
                && isValidIP(cfConnectingIp)) {
            return cfConnectingIp;
        }

        // Fallback: Remote address
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress()
                    .getAddress().getHostAddress();
        }

        return "unknown";
    }


    private static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        // Reject localhost/private IPs in production
        if (ip.equals("127.0.0.1") || ip.equals("0.0.0.0")
                || ip.equals("::1")) {
            return false;
        }

        // Basic format check (IPv4)
        String ipv4Pattern =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        return ip.matches(ipv4Pattern);
    }
}