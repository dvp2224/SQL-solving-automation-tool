package com.bajajfinserv.qualifier.runner;

import com.bajajfinserv.qualifier.dto.WebhookRequest;
import com.bajajfinserv.qualifier.dto.WebhookResponse;
import com.bajajfinserv.qualifier.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
    
    @Autowired
    private WebhookService webhookService;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Bajaj Finserv Java Qualifier application...");
        
        // Step 1: Create request payload
        WebhookRequest request = new WebhookRequest(
            "Divya Parihar",
            "22BCE10702",  // Change this to your actual registration number
            "divyabgt2224@gmail.com"
        );
        
        try {
            // Step 2: Generate webhook
            WebhookResponse response = webhookService.generateWebhook(request);
            
            if (response == null) {
                logger.error("Failed to generate webhook - null response");
                return;
            }
            
            logger.info("Webhook generated successfully!");
            logger.info("Webhook URL: {}", response.getWebhook());
            logger.info("Access Token received: {}", response.getAccessToken() != null ? "Yes" : "No");
            
            // Step 3: Submit solution
            boolean success = webhookService.submitSolution(
                response.getWebhook(),
                response.getAccessToken(),
                request.getRegNo()
            );
            
            if (success) {
                logger.info("✅ Solution submitted successfully!");
                logger.info("🎉 Application completed successfully!");
            } else {
                logger.error("❌ Failed to submit solution");
            }
            
        } catch (Exception e) {
            logger.error("❌ Application failed with error: ", e);
        }
    }
}