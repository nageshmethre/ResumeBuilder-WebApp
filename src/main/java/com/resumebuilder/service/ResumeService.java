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

        // Update Education (Orphan removal handles cleanup)
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
                proj.setResume(resume);
                resume.getProjects().add(proj);
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

        dto.setEducation(resume.getEducation().stream().map(edu -> {
            EducationDto eDto = new EducationDto();
            eDto.setId(edu.getId());
            eDto.setInstitution(edu.getInstitution());
            eDto.setDegree(edu.getDegree());
            eDto.setFieldOfStudy(edu.getFieldOfStudy());
            eDto.setStartDate(edu.getStartDate());
            eDto.setEndDate(edu.getEndDate());
            eDto.setDescription(edu.getDescription());
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
            return exDto;
        }).collect(Collectors.toList()));

        dto.setSkills(resume.getSkills().stream().map(skill -> {
            SkillDto sDto = new SkillDto();
            sDto.setId(skill.getId());
            sDto.setName(skill.getName());
            sDto.setLevel(skill.getLevel());
            return sDto;
        }).collect(Collectors.toList()));

        dto.setProjects(resume.getProjects().stream().map(proj -> {
            ProjectDto pDto = new ProjectDto();
            pDto.setId(proj.getId());
            pDto.setTitle(proj.getTitle());
            pDto.setDescription(proj.getDescription());
            pDto.setTechnologies(proj.getTechnologies());
            pDto.setLink(proj.getLink());
            return pDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
