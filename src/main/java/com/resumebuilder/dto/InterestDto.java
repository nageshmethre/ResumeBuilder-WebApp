package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public class InterestDto {
    private Long id;

    @NotBlank(message = "Interest name is required")
    private String name;

    public InterestDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
