package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public class PublicationDto {
    private Long id;

    @NotBlank(message = "Publication title is required")
    private String title;

    private String publisher;
    private String doi;
    private String link;
    private String description;

    public PublicationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
