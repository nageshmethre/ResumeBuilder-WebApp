package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public class WorkshopDto {
    private Long id;

    @NotBlank(message = "Workshop name is required")
    private String name;

    @NotBlank(message = "Organization name is required")
    private String organization;

    private String date;

    public WorkshopDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
