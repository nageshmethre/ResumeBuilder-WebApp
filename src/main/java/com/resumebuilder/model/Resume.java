package com.resumebuilder.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phone;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String headline;

    // New Personal Details
    private String dob;
    private String city;
    private String state;
    private String country;
    private String linkedin;
    private String github;
    private String portfolio;
    private String website;

    // Customization Options
    private String template = "classic";
    
    @Column(name = "font_family")
    private String fontFamily = "Inter";
    
    @Column(name = "font_size")
    private String fontSize = "medium";
    
    @Column(name = "primary_color")
    private String primaryColor = "#4f46e5";
    
    @Column(name = "line_spacing")
    private String lineSpacing = "normal";
    
    @Column(name = "page_margins")
    private String pageMargins = "normal";
    
    @Column(name = "page_size")
    private String pageSize = "a4";

    @Column(name = "max_pages")
    private String maxPages = "2";

    @Column(name = "font_size_name")
    private Integer fontSizeName = 26;

    @Column(name = "font_size_heading")
    private Integer fontSizeHeading = 14;

    @Column(name = "font_size_body")
    private Double fontSizeBody = 10.5;

    @Column(name = "line_height")
    private Double lineHeight = 1.2;

    @Column(name = "margin_size")
    private Double marginSize = 0.5;

    @Column(name = "section_spacing")
    private Integer sectionSpacing = 12;

    @Column(name = "divider_thickness")
    private Double dividerThickness = 1.0;

    @Column(name = "divider_color")
    private String dividerColor = "#d1d5db";

    @Column(name = "has_dividers")
    private Boolean hasDividers = true;


    @Column(name = "show_sections", columnDefinition = "TEXT")
    private String showSections;

    @Column(name = "section_order", columnDefinition = "TEXT")
    private String sectionOrder;

    // Relational Tables
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experience = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Internship> internships = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Workshop> workshops = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Achievement> achievements = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodingProfile> codingProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reference> references = new ArrayList<>();

    public Resume() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public List<Experience> getExperience() {
        return experience;
    }

    public void setExperience(List<Experience> experience) {
        this.experience = experience;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
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

    public Integer getFontSizeName() { return fontSizeName; }
    public void setFontSizeName(Integer fontSizeName) { this.fontSizeName = fontSizeName; }

    public Integer getFontSizeHeading() { return fontSizeHeading; }
    public void setFontSizeHeading(Integer fontSizeHeading) { this.fontSizeHeading = fontSizeHeading; }

    public Double getFontSizeBody() { return fontSizeBody; }
    public void setFontSizeBody(Double fontSizeBody) { this.fontSizeBody = fontSizeBody; }

    public Double getLineHeight() { return lineHeight; }
    public void setLineHeight(Double lineHeight) { this.lineHeight = lineHeight; }

    public Double getMarginSize() { return marginSize; }
    public void setMarginSize(Double marginSize) { this.marginSize = marginSize; }

    public Integer getSectionSpacing() { return sectionSpacing; }
    public void setSectionSpacing(Integer sectionSpacing) { this.sectionSpacing = sectionSpacing; }

    public Double getDividerThickness() { return dividerThickness; }
    public void setDividerThickness(Double dividerThickness) { this.dividerThickness = dividerThickness; }

    public String getDividerColor() { return dividerColor; }
    public void setDividerColor(String dividerColor) { this.dividerColor = dividerColor; }

    public Boolean getHasDividers() { return hasDividers; }
    public void setHasDividers(Boolean hasDividers) { this.hasDividers = hasDividers; }

    public String getShowSections() { return showSections; }
    public void setShowSections(String showSections) { this.showSections = showSections; }

    public String getSectionOrder() { return sectionOrder; }
    public void setSectionOrder(String sectionOrder) { this.sectionOrder = sectionOrder; }

    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }

    public List<Internship> getInternships() { return internships; }
    public void setInternships(List<Internship> internships) { this.internships = internships; }

    public List<Publication> getPublications() { return publications; }
    public void setPublications(List<Publication> publications) { this.publications = publications; }

    public List<Workshop> getWorkshops() { return workshops; }
    public void setWorkshops(List<Workshop> workshops) { this.workshops = workshops; }

    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }

    public List<CodingProfile> getCodingProfiles() { return codingProfiles; }
    public void setCodingProfiles(List<CodingProfile> codingProfiles) { this.codingProfiles = codingProfiles; }

    public List<Language> getLanguages() { return languages; }
    public void setLanguages(List<Language> languages) { this.languages = languages; }

    public List<Interest> getInterests() { return interests; }
    public void setInterests(List<Interest> interests) { this.interests = interests; }

    public List<Reference> getReferences() { return references; }
    public void setReferences(List<Reference> references) { this.references = references; }
}
