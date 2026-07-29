package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public class LanguageDto {
    private Long id;

    @NotBlank(message = "Language name is required")
    private String name;

    private String reading;
    private String writing;
    private String speaking;
    private String level; // Beginner, Intermediate, Fluent, Native

    public LanguageDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getReading() { return reading; }
    public void setReading(String reading) { this.reading = reading; }

    public String getWriting() { return writing; }
    public void setWriting(String writing) { this.writing = writing; }

    public String getSpeaking() { return speaking; }
    public void setSpeaking(String speaking) { this.speaking = speaking; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
