package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project title is required")
    @Size(max = 255, message = "Project title cannot exceed 255 characters")
    private String title;

    private String description;

    @Size(max = 255, message = "Technologies cannot exceed 255 characters")
    private String technologies;

    @Size(max = 255, message = "Link cannot exceed 255 characters")
    private String link;

    public ProjectDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
