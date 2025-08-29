package com.bajajfinserv.qualifier.service;

import com.bajajfinserv.qualifier.dto.SolutionRequest;
import com.bajajfinserv.qualifier.dto.WebhookRequest;
import com.bajajfinserv.qualifier.dto.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    @Autowired
    private SqlQueryService sqlQueryService;
    
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    
    public WebhookService() {
        this.restTemplate = new RestTemplate();
        this.webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .build();
    }
    
    /**
     * Step 1: Generate webhook using RestTemplate (as specified in requirements)
     */
    public WebhookResponse generateWebhook(WebhookRequest request) {
        logger.info("Generating webhook for user: {}", request.getName());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                GENERATE_WEBHOOK_URL,
                HttpMethod.POST,
                entity,
                WebhookResponse.class
            );
            
            WebhookResponse webhookResponse = response.getBody();
            logger.info("Webhook generated successfully. Webhook URL: {}", 
                       webhookResponse != null ? webhookResponse.getWebhook() : "null");
            
            return webhookResponse;
            
        } catch (Exception e) {
            logger.error("Error generating webhook", e);
            throw new RuntimeException("Failed to generate webhook", e);
        }
    }
    
    /**
     * Alternative method using WebClient (showing both implementations)
     */
    public Mono<WebhookResponse> generateWebhookAsync(WebhookRequest request) {
        logger.info("Generating webhook asynchronously for user: {}", request.getName());
        
        return webClient.post()
            .uri(GENERATE_WEBHOOK_URL)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(WebhookResponse.class)
            .doOnSuccess(response -> 
                logger.info("Async webhook generated. URL: {}", response.getWebhook()))
            .doOnError(error -> 
                logger.error("Error generating webhook asynchronously", error));
    }
    
    /**
     * Step 2: Submit solution using the generated webhook and JWT token
     */
    public boolean submitSolution(String webhookUrl, String accessToken, String regNo) {
        logger.info("Submitting solution to webhook: {}", webhookUrl);
        
        try {
            // Generate SQL query based on regNo
            String sqlQuery = sqlQueryService.generateSqlQuery(regNo);
            logger.info("Generated SQL Query: {}", sqlQuery);
            
            // Prepare headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            // Prepare request body
            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
            
            // Submit solution
            ResponseEntity<String> response = restTemplate.exchange(
                TEST_WEBHOOK_URL,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            logger.info("Solution submitted successfully. Response: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            logger.error("Error submitting solution", e);
            return false;
        }
    }
    
    /**
     * Alternative async submission using WebClient
     */
    public Mono<Boolean> submitSolutionAsync(String webhookUrl, String accessToken, String regNo) {
        logger.info("Submitting solution asynchronously to webhook: {}", webhookUrl);
        
        String sqlQuery = sqlQueryService.generateSqlQuery(regNo);
        SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
        
        return webClient.post()
            .uri(TEST_WEBHOOK_URL)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .bodyValue(solutionRequest)
            .retrieve()
            .toBodilessEntity()
            .map(response -> response.getStatusCode().is2xxSuccessful())
            .doOnSuccess(success -> 
                logger.info("Async solution submission result: {}", success))
            .doOnError(error -> 
                logger.error("Error submitting solution asynchronously", error));
    }
}