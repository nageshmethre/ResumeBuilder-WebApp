package com.resumebuilder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumebuilder.config.JwtTokenProvider;
import com.resumebuilder.dto.ResumeDto;
import com.resumebuilder.repository.UserRepository;
import com.resumebuilder.service.PdfService;
import com.resumebuilder.service.ResumeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResumeController.class)
public class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private PdfService pdfService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void getAllResumes_Success() throws Exception {
        ResumeDto resumeDto = new ResumeDto();
        resumeDto.setId(1L);
        resumeDto.setTitle("My Resume");
        resumeDto.setFirstName("John");
        resumeDto.setLastName("Doe");
        resumeDto.setEmail("john@example.com");

        when(resumeService.getResumesForUser("testuser")).thenReturn(Collections.singletonList(resumeDto));

        mockMvc.perform(get("/api/resumes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("My Resume"))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createResume_Success() throws Exception {
        ResumeDto inputDto = new ResumeDto();
        inputDto.setTitle("My New Resume");
        inputDto.setFirstName("John");
        inputDto.setLastName("Doe");
        inputDto.setEmail("john@example.com");

        ResumeDto savedDto = new ResumeDto();
        savedDto.setId(1L);
        savedDto.setTitle("My New Resume");
        savedDto.setFirstName("John");
        savedDto.setLastName("Doe");
        savedDto.setEmail("john@example.com");

        when(resumeService.createResume(any(ResumeDto.class), eq("testuser"))).thenReturn(savedDto);

        mockMvc.perform(post("/api/resumes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("My New Resume"));
    }
}
