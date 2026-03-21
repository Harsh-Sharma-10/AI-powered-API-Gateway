package com.apigateway.backendservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendServiceApplication.class, args);

    // Print startup banner
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║    Backend Service Started Successfully!             ║");
        System.out.println("║                                                        ║");
        System.out.println("║     Running on: http://localhost:8081                 ║");
        System.out.println("║   🔗 Endpoints:                                       ║");
        System.out.println("║      GET  /api/products                               ║");
        System.out.println("║      GET  /api/users                                  ║");
        System.out.println("║      GET  /health                                     ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

}
}
