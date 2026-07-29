package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EducationDto {
    private Long id;

    @NotBlank(message = "Institution is required")
    @Size(max = 255, message = "Institution name cannot exceed 255 characters")
    private String institution;

    @NotBlank(message = "Degree is required")
    @Size(max = 100, message = "Degree cannot exceed 100 characters")
    private String degree;

    @Size(max = 100, message = "Field of study cannot exceed 100 characters")
    private String fieldOfStudy;

    @Size(max = 30, message = "Start date cannot exceed 30 characters")
    private String startDate;

    @Size(max = 30, message = "End date cannot exceed 30 characters")
    private String endDate;

    private String description;

    private String university;
    private String cgpa;
    private String percentage;
    private String location;

    public EducationDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getCgpa() { return cgpa; }
    public void setCgpa(String cgpa) { this.cgpa = cgpa; }

    public String getPercentage() { return percentage; }
    public void setPercentage(String percentage) { this.percentage = percentage; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
