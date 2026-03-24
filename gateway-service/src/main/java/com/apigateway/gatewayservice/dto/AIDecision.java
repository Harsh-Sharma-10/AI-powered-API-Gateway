package com.apigateway.gatewayservice.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI Decision Response from AI Service
 * Matches the AIDecision model from ai-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIDecision {

    private String action;              // "ALLOW" or "BLOCK"
    private Double confidence;          // 0.0 to 1.0
    private String reason;              // Human-readable reason
    private String threatLevel;         // "LOW", "MEDIUM", "HIGH", "CRITICAL"

    // Additional metadata (optional)
    private Object analysisDetails;
    private String recommendedAction;
}