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

    private String githubLink;
    private String demoLink;
    private String role;
    private String teamSize;
    private String duration;
    private String features;

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

    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }

    public String getDemoLink() { return demoLink; }
    public void setDemoLink(String demoLink) { this.demoLink = demoLink; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTeamSize() { return teamSize; }
    public void setTeamSize(String teamSize) { this.teamSize = teamSize; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
}
