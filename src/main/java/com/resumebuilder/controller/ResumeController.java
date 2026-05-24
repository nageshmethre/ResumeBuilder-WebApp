package com.resumebuilder.controller;

import com.resumebuilder.dto.ResumeDto;
import com.resumebuilder.service.PdfService;
import com.resumebuilder.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<ResumeDto>> getAllResumes(Principal principal) {
        List<ResumeDto> resumes = resumeService.getResumesForUser(principal.getName());
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeDto> getResumeById(@PathVariable Long id, Principal principal) {
        ResumeDto resume = resumeService.getResumeByIdAndUser(id, principal.getName());
        return ResponseEntity.ok(resume);
    }

    @PostMapping
    public ResponseEntity<ResumeDto> createResume(@Valid @RequestBody ResumeDto resumeDto, Principal principal) {
        ResumeDto created = resumeService.createResume(resumeDto, principal.getName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeDto> updateResume(
            @PathVariable Long id,
            @Valid @RequestBody ResumeDto resumeDto,
            Principal principal) {
        ResumeDto updated = resumeService.updateResume(id, resumeDto, principal.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id, Principal principal) {
        resumeService.deleteResume(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, Principal principal) {
        ResumeDto resume = resumeService.getResumeByIdAndUser(id, principal.getName());
        byte[] pdfBytes = pdfService.generateResumePdf(resume);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Resume_" + resume.getFirstName() + "_" + resume.getLastName() + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
