package com.smartcampus.reporting.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class StudentServiceClient {
    private final WebClient webClient;
    
    @Value("${services.student-profile-url}")
    private String studentServiceUrl;

    public StudentServiceClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public List<StudentDto> getAllStudents() {
        return webClient.get()
            .uri(studentServiceUrl + "/students")
            .retrieve()
            .bodyToFlux(StudentDto.class)
            .collectList()
            .block();
    }
    
    public static class StudentDto {
        private Long id;
        private String name;
        private String email;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}