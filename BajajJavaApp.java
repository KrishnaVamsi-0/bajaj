package com.example.bajaj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajJavaApp implements CommandLineRunner {

    private static final String INIT_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    public static void main(String[] args) {
        SpringApplication.run(BajajJavaApp.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", "Nindra Krishna Vamsi");
            requestBody.put("regNo", "22BCE7486");  // <-- replace with your regNo (even last digit)
            requestBody.put("email", "krisha.22bce7486@vitapstudent.ac.in");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 2. Send POST to generateWebhook
            ResponseEntity<Map> response = restTemplate.postForEntity(INIT_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String webhookUrl = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                System.out.println("Webhook URL: " + webhookUrl);
                System.out.println("Access Token: " + accessToken);

                // 3. Your final SQL query (for Question 2 since regNo is even)
                String finalQuery =
                        "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, " +
                        "COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                        "FROM EMPLOYEE e1 " +
                        "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                        "LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT " +
                        "AND e2.DOB > e1.DOB " +
                        "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                        "ORDER BY e1.EMP_ID DESC;";

                Map<String, Object> finalBody = new HashMap<>();
                finalBody.put("finalQuery", finalQuery);

                HttpHeaders authHeaders = new HttpHeaders();
                authHeaders.setContentType(MediaType.APPLICATION_JSON);
                authHeaders.setBearerAuth(accessToken);  // <-- JWT token

                HttpEntity<Map<String, Object>> finalRequest = new HttpEntity<>(finalBody, authHeaders);

                // 4. Submit solution
                ResponseEntity<String> finalResponse = restTemplate.postForEntity(webhookUrl, finalRequest, String.class);

                System.out.println("Submission Response: " + finalResponse.getBody());
            } else {
                System.err.println("Failed to get webhook or token");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
