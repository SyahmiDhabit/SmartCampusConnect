package com.smartcampus.enrolment.client;

import com.smartcampus.enrolment.resilience.SimpleCircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class StudentClient {

    private final RestTemplate restTemplate;
    private final String studentServiceUrl;
    private final SimpleCircuitBreaker circuitBreaker = new SimpleCircuitBreaker("student-service", 3, 15000);

    public StudentClient(@Value("${smartcampus.student-profile.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(1000);
        factory.setReadTimeout(2000);
        this.restTemplate = new RestTemplate(factory);
        this.studentServiceUrl = baseUrl + "/api/students/{id}";
    }

    public static class VerificationResult {
        private final boolean exists;
        private final boolean isOffline;
        private final String name;
        private final String programme;

        public VerificationResult(boolean exists, boolean isOffline, String name, String programme) {
            this.exists = exists;
            this.isOffline = isOffline;
            this.name = name;
            this.programme = programme;
        }

        public boolean exists() { return exists; }
        public boolean isOffline() { return isOffline; }
        public String getName() { return name; }
        public String getProgramme() { return programme; }
    }

    public VerificationResult verifyStudent(String studentId) {
        if (!circuitBreaker.canCall()) {
            System.err.println("[StudentClient] Circuit is OPEN. Skipping HTTP call to student-service (Fail-Fast).");
            return new VerificationResult(false, true, null, null);
        }

        int maxRetries = 3;
        int delayMs = 500;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> student = restTemplate.getForObject(studentServiceUrl, Map.class, studentId);
                circuitBreaker.recordSuccess();

                if (student != null) {
                    return new VerificationResult(true, false,
                            (String) student.get("name"),
                            (String) student.get("programme"));
                }
                return new VerificationResult(false, false, null, null);
            } catch (ResourceAccessException e) {
                System.err.println("[StudentClient] HTTP attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxRetries) {
                    circuitBreaker.recordFailure();
                    break;
                }
                try {
                    Thread.sleep(delayMs);
                    delayMs *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
                circuitBreaker.recordSuccess();
                return new VerificationResult(false, false, null, null);
            } catch (Exception e) {
                System.err.println("[StudentClient] Unexpected error: " + e.getMessage());
                circuitBreaker.recordFailure();
                break;
            }
        }

        return new VerificationResult(false, true, null, null);
    }

    public String getCircuitBreakerState() {
        return circuitBreaker.getState().toString();
    }
}
