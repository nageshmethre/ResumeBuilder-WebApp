package com.resumebuilder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ResumeDto {
    private Long id;

    @NotBlank(message = "Resume title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    private String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    private String summary;

    // New Personal Info
    private String dob;
    private String city;
    private String state;
    private String country;
    private String linkedin;
    private String github;
    private String portfolio;
    private String website;

    // Customizations
    private String template = "classic";
    private String fontFamily = "Inter";
    private String fontSize = "medium";
    private String primaryColor = "#4f46e5";
    private String lineSpacing = "normal";
    private String pageMargins = "normal";
    private String pageSize = "a4";
    private String maxPages = "2";
    private String showSections;
    private String sectionOrder;

    @Valid
    private List<EducationDto> education = new ArrayList<>();

    @Valid
    private List<ExperienceDto> experience = new ArrayList<>();

    @Valid
    private List<SkillDto> skills = new ArrayList<>();

    @Valid
    private List<ProjectDto> projects = new ArrayList<>();

    @Valid
    private List<CertificationDto> certifications = new ArrayList<>();

    @Valid
    private List<InternshipDto> internships = new ArrayList<>();

    @Valid
    private List<PublicationDto> publications = new ArrayList<>();

    @Valid
    private List<WorkshopDto> workshops = new ArrayList<>();

    @Valid
    private List<AchievementDto> achievements = new ArrayList<>();

    @Valid
    private List<CodingProfileDto> codingProfiles = new ArrayList<>();

    @Valid
    private List<LanguageDto> languages = new ArrayList<>();

    @Valid
    private List<InterestDto> interests = new ArrayList<>();

    @Valid
    private List<ReferenceDto> references = new ArrayList<>();

    public ResumeDto() {}

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<EducationDto> getEducation() {
        return education;
    }

    public void setEducation(List<EducationDto> education) {
        this.education = education;
    }

    public List<ExperienceDto> getExperience() {
        return experience;
    }

    public void setExperience(List<ExperienceDto> experience) {
        this.experience = experience;
    }

    public List<SkillDto> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDto> skills) {
        this.skills = skills;
    }

    public List<ProjectDto> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDto> projects) {
        this.projects = projects;
    }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public String getFontSize() { return fontSize; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getLineSpacing() { return lineSpacing; }
    public void setLineSpacing(String lineSpacing) { this.lineSpacing = lineSpacing; }

    public String getPageMargins() { return pageMargins; }
    public void setPageMargins(String pageMargins) { this.pageMargins = pageMargins; }

    public String getPageSize() { return pageSize; }
    public void setPageSize(String pageSize) { this.pageSize = pageSize; }

    public String getMaxPages() { return maxPages; }
    public void setMaxPages(String maxPages) { this.maxPages = maxPages; }

    public String getShowSections() { return showSections; }
    public void setShowSections(String showSections) { this.showSections = showSections; }

    public String getSectionOrder() { return sectionOrder; }
    public void setSectionOrder(String sectionOrder) { this.sectionOrder = sectionOrder; }

    public List<CertificationDto> getCertifications() { return certifications; }
    public void setCertifications(List<CertificationDto> certifications) { this.certifications = certifications; }

    public List<InternshipDto> getInternships() { return internships; }
    public void setInternships(List<InternshipDto> internships) { this.internships = internships; }

    public List<PublicationDto> getPublications() { return publications; }
    public void setPublications(List<PublicationDto> publications) { this.publications = publications; }

    public List<WorkshopDto> getWorkshops() { return workshops; }
    public void setWorkshops(List<WorkshopDto> workshops) { this.workshops = workshops; }

    public List<AchievementDto> getAchievements() { return achievements; }
    public void setAchievements(List<AchievementDto> achievements) { this.achievements = achievements; }

    public List<CodingProfileDto> getCodingProfiles() { return codingProfiles; }
    public void setCodingProfiles(List<CodingProfileDto> codingProfiles) { this.codingProfiles = codingProfiles; }

    public List<LanguageDto> getLanguages() { return languages; }
    public void setLanguages(List<LanguageDto> languages) { this.languages = languages; }

    public List<InterestDto> getInterests() { return interests; }
    public void setInterests(List<InterestDto> interests) { this.interests = interests; }

    public List<ReferenceDto> getReferences() { return references; }
    public void setReferences(List<ReferenceDto> references) { this.references = references; }
}
