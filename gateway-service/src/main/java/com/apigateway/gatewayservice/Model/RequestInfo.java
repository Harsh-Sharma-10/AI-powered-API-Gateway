package com.apigateway.gatewayservice.Model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * RequestInfo - MongoDB Entity
 *
 * Purpose: Stores request information in the database
 * Used for: Analytics, auditing, AI model training
 *
 * @Document - Marks this as a MongoDB collection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "request_logs")  // MongoDB collection name
public class RequestInfo {




    @Id
    private String id;

    @Indexed
    private String ipAddress;


    private String userAgent;

    private String method;
    @Indexed
    private String endpoint;
    private String requestPath;


    private Integer requestCount;
    @Indexed
    private LocalDateTime timestamp;
    private Long responseTimeMs;


    private Integer statusCode;

    @Indexed
    private String aiDecision;
    private Double aiConfidence;
    private String aiReason;


    private Boolean rateLimited;
    private Boolean suspicious;
    private LocalDateTime createdAt;


    private String environment;
}