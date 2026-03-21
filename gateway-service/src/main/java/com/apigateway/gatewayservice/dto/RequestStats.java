package com.apigateway.gatewayservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RequestStats - Data Transfer Object (DTO)
 * Purpose: Transfer request data between services
 * Used for:
 *   - Passing data between filters
 *   - Sending to AI service
 *   - Internal processing
 *
 * NO database annotations - this is NOT persisted directly
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestStats {
     private String ipAddress;
     private String userAgent;

     private String method;
     private String endpoint;
     private String requestpath;

     private Integer requestcount;
     private LocalDateTime timestamp;
     private Long responseTimeMs;

    private Integer statusCode;


    private String aiDecision;
    private Double aiConfidence;
    private String aiReason;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequestpath() {
        return requestpath;
    }

    public void setRequestpath(String requestpath) {
        this.requestpath = requestpath;
    }

    public Integer getRequestcount() {
        return requestcount;
    }

    public void setRequestcount(Integer requestcount) {
        this.requestcount = requestcount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getAiDecision() {
        return aiDecision;
    }

    public void setAiDecision(String aiDecision) {
        this.aiDecision = aiDecision;
    }

    public Double getAiConfidence() {
        return aiConfidence;
    }

    public void setAiConfidence(Double aiConfidence) {
        this.aiConfidence = aiConfidence;
    }

    public String getAiReason() {
        return aiReason;
    }

    public void setAiReason(String aiReason) {
        this.aiReason = aiReason;
    }
}
