package com.resumebuilder.service;

import com.resumebuilder.dto.*;
import com.resumebuilder.exception.ResourceNotFoundException;
import com.resumebuilder.model.*;
import com.resumebuilder.repository.ResumeRepository;
import com.resumebuilder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ResumeDto> getResumesForUser(String username) {
        List<Resume> resumes = resumeRepository.findByUserUsername(username);
        return resumes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResumeDto getResumeByIdAndUser(Long id, String username) {
        Resume resume = resumeRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + id));
        return convertToDto(resume);
    }

    @Transactional
    public ResumeDto createResume(ResumeDto resumeDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Resume resume = new Resume();
        resume.setUser(user);
        updateResumeFields(resume, resumeDto);

        Resume savedResume = resumeRepository.save(resume);
        return convertToDto(savedResume);
    }

    @Transactional
    public ResumeDto updateResume(Long id, ResumeDto resumeDto, String username) {
        Resume resume = resumeRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or you don't have access"));

        updateResumeFields(resume, resumeDto);
        Resume updatedResume = resumeRepository.save(resume);
        return convertToDto(updatedResume);
    }

    @Transactional
    public void deleteResume(Long id, String username) {
        Resume resume = resumeRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or you don't have access"));
        resumeRepository.delete(resume);
    }

    private void updateResumeFields(Resume resume, ResumeDto dto) {
        resume.setTitle(dto.getTitle());
        resume.setFirstName(dto.getFirstName());
        resume.setLastName(dto.getLastName());
        resume.setEmail(dto.getEmail());
        resume.setPhone(dto.getPhone());
        resume.setAddress(dto.getAddress());
        resume.setSummary(dto.getSummary());

        // Personal Info fields
        resume.setDob(dto.getDob());
        resume.setCity(dto.getCity());
        resume.setState(dto.getState());
        resume.setCountry(dto.getCountry());
        resume.setLinkedin(dto.getLinkedin());
        resume.setGithub(dto.getGithub());
        resume.setPortfolio(dto.getPortfolio());
        resume.setWebsite(dto.getWebsite());

        // Customization Options
        resume.setTemplate(dto.getTemplate() != null ? dto.getTemplate() : "classic");
        resume.setFontFamily(dto.getFontFamily() != null ? dto.getFontFamily() : "Inter");
        resume.setFontSize(dto.getFontSize() != null ? dto.getFontSize() : "medium");
        resume.setPrimaryColor(dto.getPrimaryColor() != null ? dto.getPrimaryColor() : "#4f46e5");
        resume.setLineSpacing(dto.getLineSpacing() != null ? dto.getLineSpacing() : "normal");
        resume.setPageMargins(dto.getPageMargins() != null ? dto.getPageMargins() : "normal");
        resume.setPageSize(dto.getPageSize() != null ? dto.getPageSize() : "a4");
        resume.setMaxPages(dto.getMaxPages() != null ? dto.getMaxPages() : "2");
        resume.setShowSections(dto.getShowSections());
        resume.setSectionOrder(dto.getSectionOrder());

        // Update Education
        resume.getEducation().clear();
        if (dto.getEducation() != null) {
            for (EducationDto eduDto : dto.getEducation()) {
                Education edu = new Education();
                edu.setInstitution(eduDto.getInstitution());
                edu.setDegree(eduDto.getDegree());
                edu.setFieldOfStudy(eduDto.getFieldOfStudy());
                edu.setStartDate(eduDto.getStartDate());
                edu.setEndDate(eduDto.getEndDate());
                edu.setDescription(eduDto.getDescription());
                edu.setUniversity(eduDto.getUniversity());
                edu.setCgpa(eduDto.getCgpa());
                edu.setPercentage(eduDto.getPercentage());
                edu.setLocation(eduDto.getLocation());
                edu.setResume(resume);
                resume.getEducation().add(edu);
            }
        }

        // Update Experience
        resume.getExperience().clear();
        if (dto.getExperience() != null) {
            for (ExperienceDto expDto : dto.getExperience()) {
                Experience exp = new Experience();
                exp.setCompany(expDto.getCompany());
                exp.setPosition(expDto.getPosition());
                exp.setStartDate(expDto.getStartDate());
                exp.setEndDate(expDto.getEndDate());
                exp.setDescription(expDto.getDescription());
                exp.setEmploymentType(expDto.getEmploymentType());
                exp.setLocation(expDto.getLocation());
                exp.setIsCurrent(expDto.getIsCurrent() != null ? expDto.getIsCurrent() : false);
                exp.setResponsibilities(expDto.getResponsibilities());
                exp.setAchievements(expDto.getAchievements());
                exp.setTechnologies(expDto.getTechnologies());
                exp.setResume(resume);
                resume.getExperience().add(exp);
            }
        }

        // Update Skills
        resume.getSkills().clear();
        if (dto.getSkills() != null) {
            for (SkillDto skillDto : dto.getSkills()) {
                Skill skill = new Skill();
                skill.setName(skillDto.getName());
                skill.setLevel(skillDto.getLevel());
                skill.setCategory(skillDto.getCategory());
                skill.setResume(resume);
                resume.getSkills().add(skill);
            }
        }

        // Update Projects
        resume.getProjects().clear();
        if (dto.getProjects() != null) {
            for (ProjectDto projDto : dto.getProjects()) {
                Project proj = new Project();
                proj.setTitle(projDto.getTitle());
                proj.setDescription(projDto.getDescription());
                proj.setTechnologies(projDto.getTechnologies());
                proj.setLink(projDto.getLink());
                proj.setGithubLink(projDto.getGithubLink());
                proj.setDemoLink(projDto.getDemoLink());
                proj.setRole(projDto.getRole());
                proj.setTeamSize(projDto.getTeamSize());
                proj.setDuration(projDto.getDuration());
                proj.setFeatures(projDto.getFeatures());
                proj.setResume(resume);
                resume.getProjects().add(proj);
            }
        }

        // Update Certifications
        resume.getCertifications().clear();
        if (dto.getCertifications() != null) {
            for (CertificationDto certDto : dto.getCertifications()) {
                Certification cert = new Certification();
                cert.setName(certDto.getName());
                cert.setOrganization(certDto.getOrganization());
                cert.setIssueDate(certDto.getIssueDate());
                cert.setExpiryDate(certDto.getExpiryDate());
                cert.setCredentialId(certDto.getCredentialId());
                cert.setCredentialUrl(certDto.getCredentialUrl());
                cert.setResume(resume);
                resume.getCertifications().add(cert);
            }
        }

        // Update Internships
        resume.getInternships().clear();
        if (dto.getInternships() != null) {
            for (InternshipDto intDto : dto.getInternships()) {
                Internship internship = new Internship();
                internship.setCompany(intDto.getCompany());
                internship.setPosition(intDto.getPosition());
                internship.setDuration(intDto.getDuration());
                internship.setDescription(intDto.getDescription());
                internship.setTechnologies(intDto.getTechnologies());
                internship.setResume(resume);
                resume.getInternships().add(internship);
            }
        }

        // Update Publications
        resume.getPublications().clear();
        if (dto.getPublications() != null) {
            for (PublicationDto pubDto : dto.getPublications()) {
                Publication pub = new Publication();
                pub.setTitle(pubDto.getTitle());
                pub.setPublisher(pubDto.getPublisher());
                pub.setDoi(pubDto.getDoi());
                pub.setLink(pubDto.getLink());
                pub.setDescription(pubDto.getDescription());
                pub.setResume(resume);
                resume.getPublications().add(pub);
            }
        }

        // Update Workshops
        resume.getWorkshops().clear();
        if (dto.getWorkshops() != null) {
            for (WorkshopDto workDto : dto.getWorkshops()) {
                Workshop workshop = new Workshop();
                workshop.setName(workDto.getName());
                workshop.setOrganization(workDto.getOrganization());
                workshop.setDate(workDto.getDate());
                workshop.setResume(resume);
                resume.getWorkshops().add(workshop);
            }
        }

        // Update Achievements
        resume.getAchievements().clear();
        if (dto.getAchievements() != null) {
            for (AchievementDto achDto : dto.getAchievements()) {
                Achievement achievement = new Achievement();
                achievement.setCategory(achDto.getCategory());
                achievement.setDescription(achDto.getDescription());
                achievement.setResume(resume);
                resume.getAchievements().add(achievement);
            }
        }

        // Update Coding Profiles
        resume.getCodingProfiles().clear();
        if (dto.getCodingProfiles() != null) {
            for (CodingProfileDto cpDto : dto.getCodingProfiles()) {
                CodingProfile profile = new CodingProfile();
                profile.setPlatform(cpDto.getPlatform());
                profile.setUrl(cpDto.getUrl());
                profile.setRating(cpDto.getRating());
                profile.setResume(resume);
                resume.getCodingProfiles().add(profile);
            }
        }

        // Update Languages
        resume.getLanguages().clear();
        if (dto.getLanguages() != null) {
            for (LanguageDto langDto : dto.getLanguages()) {
                Language language = new Language();
                language.setName(langDto.getName());
                language.setReading(langDto.getReading());
                language.setWriting(langDto.getWriting());
                language.setSpeaking(langDto.getSpeaking());
                language.setLevel(langDto.getLevel());
                language.setResume(resume);
                resume.getLanguages().add(language);
            }
        }

        // Update Interests
        resume.getInterests().clear();
        if (dto.getInterests() != null) {
            for (InterestDto intDto : dto.getInterests()) {
                Interest interest = new Interest();
                interest.setName(intDto.getName());
                interest.setResume(resume);
                resume.getInterests().add(interest);
            }
        }

        // Update References
        resume.getReferences().clear();
        if (dto.getReferences() != null) {
            for (ReferenceDto refDto : dto.getReferences()) {
                Reference reference = new Reference();
                reference.setName(refDto.getName());
                reference.setRelationship(refDto.getRelationship());
                reference.setEmail(refDto.getEmail());
                reference.setPhone(refDto.getPhone());
                reference.setCompany(refDto.getCompany());
                reference.setResume(resume);
                resume.getReferences().add(reference);
            }
        }
    }

    private ResumeDto convertToDto(Resume resume) {
        ResumeDto dto = new ResumeDto();
        dto.setId(resume.getId());
        dto.setTitle(resume.getTitle());
        dto.setFirstName(resume.getFirstName());
        dto.setLastName(resume.getLastName());
        dto.setEmail(resume.getEmail());
        dto.setPhone(resume.getPhone());
        dto.setAddress(resume.getAddress());
        dto.setSummary(resume.getSummary());

        // Personal Info fields
        dto.setDob(resume.getDob());
        dto.setCity(resume.getCity());
        dto.setState(resume.getState());
        dto.setCountry(resume.getCountry());
        dto.setLinkedin(resume.getLinkedin());
        dto.setGithub(resume.getGithub());
        dto.setPortfolio(resume.getPortfolio());
        dto.setWebsite(resume.getWebsite());

        // Customizations
        dto.setTemplate(resume.getTemplate());
        dto.setFontFamily(resume.getFontFamily());
        dto.setFontSize(resume.getFontSize());
        dto.setPrimaryColor(resume.getPrimaryColor());
        dto.setLineSpacing(resume.getLineSpacing());
        dto.setPageMargins(resume.getPageMargins());
        dto.setPageSize(resume.getPageSize());
        dto.setMaxPages(resume.getMaxPages());
        dto.setShowSections(resume.getShowSections());
        dto.setSectionOrder(resume.getSectionOrder());

        dto.setEducation(resume.getEducation().stream().map(edu -> {
            EducationDto eDto = new EducationDto();
            eDto.setId(edu.getId());
            eDto.setInstitution(edu.getInstitution());
            eDto.setDegree(edu.getDegree());
            eDto.setFieldOfStudy(edu.getFieldOfStudy());
            eDto.setStartDate(edu.getStartDate());
            eDto.setEndDate(edu.getEndDate());
            eDto.setDescription(edu.getDescription());
            eDto.setUniversity(edu.getUniversity());
            eDto.setCgpa(edu.getCgpa());
            eDto.setPercentage(edu.getPercentage());
            eDto.setLocation(edu.getLocation());
            return eDto;
        }).collect(Collectors.toList()));

        dto.setExperience(resume.getExperience().stream().map(exp -> {
            ExperienceDto exDto = new ExperienceDto();
            exDto.setId(exp.getId());
            exDto.setCompany(exp.getCompany());
            exDto.setPosition(exp.getPosition());
            exDto.setStartDate(exp.getStartDate());
            exDto.setEndDate(exp.getEndDate());
            exDto.setDescription(exp.getDescription());
            exDto.setEmploymentType(exp.getEmploymentType());
            exDto.setLocation(exp.getLocation());
            exDto.setIsCurrent(exp.getIsCurrent());
            exDto.setResponsibilities(exp.getResponsibilities());
            exDto.setAchievements(exp.getAchievements());
            exDto.setTechnologies(exp.getTechnologies());
            return exDto;
        }).collect(Collectors.toList()));

        dto.setSkills(resume.getSkills().stream().map(skill -> {
            SkillDto sDto = new SkillDto();
            sDto.setId(skill.getId());
            sDto.setName(skill.getName());
            sDto.setLevel(skill.getLevel());
            sDto.setCategory(skill.getCategory());
            return sDto;
        }).collect(Collectors.toList()));

        dto.setProjects(resume.getProjects().stream().map(proj -> {
            ProjectDto pDto = new ProjectDto();
            pDto.setId(proj.getId());
            pDto.setTitle(proj.getTitle());
            pDto.setDescription(proj.getDescription());
            pDto.setTechnologies(proj.getTechnologies());
            pDto.setLink(proj.getLink());
            pDto.setGithubLink(proj.getGithubLink());
            pDto.setDemoLink(proj.getDemoLink());
            pDto.setRole(proj.getRole());
            pDto.setTeamSize(proj.getTeamSize());
            pDto.setDuration(proj.getDuration());
            pDto.setFeatures(proj.getFeatures());
            return pDto;
        }).collect(Collectors.toList()));

        dto.setCertifications(resume.getCertifications().stream().map(cert -> {
            CertificationDto cDto = new CertificationDto();
            cDto.setId(cert.getId());
            cDto.setName(cert.getName());
            cDto.setOrganization(cert.getOrganization());
            cDto.setIssueDate(cert.getIssueDate());
            cDto.setExpiryDate(cert.getExpiryDate());
            cDto.setCredentialId(cert.getCredentialId());
            cDto.setCredentialUrl(cert.getCredentialUrl());
            return cDto;
        }).collect(Collectors.toList()));

        dto.setInternships(resume.getInternships().stream().map(internship -> {
            InternshipDto iDto = new InternshipDto();
            iDto.setId(internship.getId());
            iDto.setCompany(internship.getCompany());
            iDto.setPosition(internship.getPosition());
            iDto.setDuration(internship.getDuration());
            iDto.setDescription(internship.getDescription());
            iDto.setTechnologies(internship.getTechnologies());
            return iDto;
        }).collect(Collectors.toList()));

        dto.setPublications(resume.getPublications().stream().map(pub -> {
            PublicationDto pDto = new PublicationDto();
            pDto.setId(pub.getId());
            pDto.setTitle(pub.getTitle());
            pDto.setPublisher(pub.getPublisher());
            pDto.setDoi(pub.getDoi());
            pDto.setLink(pub.getLink());
            pDto.setDescription(pub.getDescription());
            return pDto;
        }).collect(Collectors.toList()));

        dto.setWorkshops(resume.getWorkshops().stream().map(workshop -> {
            WorkshopDto wDto = new WorkshopDto();
            wDto.setId(workshop.getId());
            wDto.setName(workshop.getName());
            wDto.setOrganization(workshop.getOrganization());
            wDto.setDate(workshop.getDate());
            return wDto;
        }).collect(Collectors.toList()));

        dto.setAchievements(resume.getAchievements().stream().map(ach -> {
            AchievementDto aDto = new AchievementDto();
            aDto.setId(ach.getId());
            aDto.setCategory(ach.getCategory());
            aDto.setDescription(ach.getDescription());
            return aDto;
        }).collect(Collectors.toList()));

        dto.setCodingProfiles(resume.getCodingProfiles().stream().map(cp -> {
            CodingProfileDto cpDto = new CodingProfileDto();
            cpDto.setId(cp.getId());
            cpDto.setPlatform(cp.getPlatform());
            cpDto.setUrl(cp.getUrl());
            cpDto.setRating(cp.getRating());
            return cpDto;
        }).collect(Collectors.toList()));

        List<LanguageDto> langList = resume.getLanguages().stream().map(lang -> {
            LanguageDto lDto = new LanguageDto();
            lDto.setId(lang.getId());
            lDto.setName(lang.getName());
            lDto.setReading(lang.getReading());
            lDto.setWriting(lang.getWriting());
            lDto.setSpeaking(lang.getSpeaking());
            lDto.setLevel(lang.getLevel());
            return lDto;
        }).collect(Collectors.toList());
        dto.setLanguages(langList);

        dto.setInterests(resume.getInterests().stream().map(interest -> {
            InterestDto iDto = new InterestDto();
            iDto.setId(interest.getId());
            iDto.setName(interest.getName());
            return iDto;
        }).collect(Collectors.toList()));

        dto.setReferences(resume.getReferences().stream().map(ref -> {
            ReferenceDto rDto = new ReferenceDto();
            rDto.setId(ref.getId());
            rDto.setName(ref.getName());
            rDto.setRelationship(ref.getRelationship());
            rDto.setEmail(ref.getEmail());
            rDto.setPhone(ref.getPhone());
            rDto.setCompany(ref.getCompany());
            return rDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
