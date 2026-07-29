package com.resumebuilder.dto;

import jakarta.validation.constraints.NotBlank;

public class ReferenceDto {
    private Long id;

    @NotBlank(message = "Reference name is required")
    private String name;

    private String relationship;
    private String email;
    private String phone;
    private String company;

    public ReferenceDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}
