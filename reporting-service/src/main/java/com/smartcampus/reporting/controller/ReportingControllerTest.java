package com.smartcampus.reporting.controller;

import com.smartcampus.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ReportingController.class)
class ReportingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void enrolmentSummary_returnsSummaryJson() throws Exception {
        when(analyticsService.getEnrolmentSummary()).thenReturn(Map.of(
                "totalCreated", 5,
                "totalDropped", 2,
                "activeEnrolments", 3,
                "totalEvents", 7
        ));

        mockMvc.perform(get("/reports/enrolment-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCreated").value(5))
                .andExpect(jsonPath("$.totalDropped").value(2))
                .andExpect(jsonPath("$.activeEnrolments").value(3))
                .andExpect(jsonPath("$.totalEvents").value(7));
    }
}