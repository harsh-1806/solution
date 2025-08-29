package com.dinesh.solution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SolutionApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(SolutionApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Dinesh Kumar Rana");
        requestBody.put("regNo", "22BCE10915");
        requestBody.put("email", "dkr9304650@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            assert body != null;

            String webhookUrl = (String) body.get("webhook");
            String accessToken = (String) body.get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);


            String finalQuery =
                    "SELECT p.AMOUNT AS SALARY, " +
                            "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                            "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                            "d.DEPARTMENT_NAME " +
                            "FROM PAYMENTS p " +
                            "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                            "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                            "ORDER BY p.AMOUNT DESC " +
                            "LIMIT 1;";


            Map<String, String> finalRequest = new HashMap<>();
            finalRequest.put("finalQuery", finalQuery);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setContentType(MediaType.APPLICATION_JSON);
            authHeaders.set("Authorization", accessToken);

            HttpEntity<Map<String, String>> finalEntity = new HttpEntity<>(finalRequest, authHeaders);

            ResponseEntity<String> finalResponse =
                    restTemplate.postForEntity(webhookUrl, finalEntity, String.class);

            System.out.println("Submission Response: " + finalResponse.getBody());
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
    }
}
