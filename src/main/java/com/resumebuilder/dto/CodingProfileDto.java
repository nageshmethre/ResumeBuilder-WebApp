package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public class CodingProfileDto {
    private Long id;

    @NotBlank(message = "Platform is required")
    private String platform;

    @NotBlank(message = "URL is required")
    private String url;

    private String rating;

    public CodingProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
}
