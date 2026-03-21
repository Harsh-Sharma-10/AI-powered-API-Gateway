package com.apigateway.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
        // Print startup banner
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║   🚀 AI-Powered API Gateway Started Successfully!     ║");
        System.out.println("║                                                        ║");
        System.out.println("║   📍 Running on: http://localhost:8080                ║");
        System.out.println("║   🔍 Ready to analyze requests!                       ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
	}

}
