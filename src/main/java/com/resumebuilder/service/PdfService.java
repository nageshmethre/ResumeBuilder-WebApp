package com.resumebuilder.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.resumebuilder.dto.ResumeDto;
import com.resumebuilder.dto.EducationDto;
import com.resumebuilder.dto.ExperienceDto;
import com.resumebuilder.dto.ProjectDto;
import com.resumebuilder.dto.SkillDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    public byte[] generateResumePdf(ResumeDto resume) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts Setup (Helvetica)
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(26, 82, 118));
            Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(41, 128, 185));
            Font itemTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
            Font itemSubFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            // 1. Personal Header Section
            Paragraph namePara = new Paragraph(resume.getFirstName() + " " + resume.getLastName(), nameFont);
            namePara.setAlignment(Element.ALIGN_CENTER);
            namePara.setSpacingAfter(4);
            document.add(namePara);

            // Contact Info line
            StringBuilder contactBuilder = new StringBuilder();
            contactBuilder.append(resume.getEmail());
            if (resume.getPhone() != null && !resume.getPhone().trim().isEmpty()) {
                contactBuilder.append("  |  ").append(resume.getPhone());
            }
            if (resume.getAddress() != null && !resume.getAddress().trim().isEmpty()) {
                contactBuilder.append("  |  ").append(resume.getAddress());
            }
            Paragraph contactPara = new Paragraph(contactBuilder.toString(), contactFont);
            contactPara.setAlignment(Element.ALIGN_CENTER);
            contactPara.setSpacingAfter(15);
            document.add(contactPara);

            // Line separator
            LineSeparator separator = new LineSeparator(1.5f, 100, new Color(41, 128, 185), Element.ALIGN_CENTER, -5);

            // 2. Summary
            if (resume.getSummary() != null && !resume.getSummary().trim().isEmpty()) {
                Paragraph sectionTitle = new Paragraph("PROFESSIONAL SUMMARY", sectionTitleFont);
                sectionTitle.setSpacingBefore(10);
                sectionTitle.setSpacingAfter(2);
                document.add(sectionTitle);
                document.add(separator);

                Paragraph summaryPara = new Paragraph(resume.getSummary(), bodyFont);
                summaryPara.setSpacingBefore(5);
                summaryPara.setSpacingAfter(12);
                summaryPara.setLeading(14);
                document.add(summaryPara);
            }

            // 3. Work Experience
            if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
                Paragraph sectionTitle = new Paragraph("WORK EXPERIENCE", sectionTitleFont);
                sectionTitle.setSpacingBefore(10);
                sectionTitle.setSpacingAfter(2);
                document.add(sectionTitle);
                document.add(separator);

                for (ExperienceDto exp : resume.getExperience()) {
                    PdfPTable expTable = new PdfPTable(2);
                    expTable.setWidthPercentage(100);
                    expTable.setSpacingBefore(6);
                    expTable.setSpacingAfter(2);

                    // Row 1: Position and Dates
                    PdfPCell posCell = new PdfPCell(new Phrase(exp.getPosition() + " - " + exp.getCompany(), itemTitleFont));
                    posCell.setBorder(Rectangle.NO_BORDER);
                    
                    String dates = exp.getStartDate() + " - " + (exp.getEndDate() != null && !exp.getEndDate().isEmpty() ? exp.getEndDate() : "Present");
                    PdfPCell dateCell = new PdfPCell(new Phrase(dates, itemSubFont));
                    dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    dateCell.setBorder(Rectangle.NO_BORDER);

                    expTable.addCell(posCell);
                    expTable.addCell(dateCell);
                    document.add(expTable);

                    if (exp.getDescription() != null && !exp.getDescription().trim().isEmpty()) {
                        Paragraph descPara = new Paragraph(exp.getDescription(), bodyFont);
                        descPara.setSpacingAfter(8);
                        descPara.setLeading(13);
                        document.add(descPara);
                    }
                }
            }

            // 4. Education
            if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
                Paragraph sectionTitle = new Paragraph("EDUCATION", sectionTitleFont);
                sectionTitle.setSpacingBefore(10);
                sectionTitle.setSpacingAfter(2);
                document.add(sectionTitle);
                document.add(separator);

                for (EducationDto edu : resume.getEducation()) {
                    PdfPTable eduTable = new PdfPTable(2);
                    eduTable.setWidthPercentage(100);
                    eduTable.setSpacingBefore(6);
                    eduTable.setSpacingAfter(2);

                    String degreeInfo = edu.getDegree() + (edu.getFieldOfStudy() != null && !edu.getFieldOfStudy().isEmpty() ? " in " + edu.getFieldOfStudy() : "");
                    PdfPCell degCell = new PdfPCell(new Phrase(degreeInfo + " @ " + edu.getInstitution(), itemTitleFont));
                    degCell.setBorder(Rectangle.NO_BORDER);

                    String dates = edu.getStartDate() + " - " + (edu.getEndDate() != null && !edu.getEndDate().isEmpty() ? edu.getEndDate() : "Present");
                    PdfPCell dateCell = new PdfPCell(new Phrase(dates, itemSubFont));
                    dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    dateCell.setBorder(Rectangle.NO_BORDER);

                    eduTable.addCell(degCell);
                    eduTable.addCell(dateCell);
                    document.add(eduTable);

                    if (edu.getDescription() != null && !edu.getDescription().trim().isEmpty()) {
                        Paragraph descPara = new Paragraph(edu.getDescription(), bodyFont);
                        descPara.setSpacingAfter(8);
                        descPara.setLeading(13);
                        document.add(descPara);
                    }
                }
            }

            // 5. Projects
            if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                Paragraph sectionTitle = new Paragraph("PROJECTS", sectionTitleFont);
                sectionTitle.setSpacingBefore(10);
                sectionTitle.setSpacingAfter(2);
                document.add(sectionTitle);
                document.add(separator);

                for (ProjectDto proj : resume.getProjects()) {
                    PdfPTable projTable = new PdfPTable(2);
                    projTable.setWidthPercentage(100);
                    projTable.setSpacingBefore(6);
                    projTable.setSpacingAfter(2);

                    PdfPCell titleCell = new PdfPCell(new Phrase(proj.getTitle(), itemTitleFont));
                    titleCell.setBorder(Rectangle.NO_BORDER);

                    String linkText = (proj.getLink() != null && !proj.getLink().isEmpty()) ? proj.getLink() : "";
                    PdfPCell linkCell = new PdfPCell(new Phrase(linkText, itemSubFont));
                    linkCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    linkCell.setBorder(Rectangle.NO_BORDER);

                    projTable.addCell(titleCell);
                    projTable.addCell(linkCell);
                    document.add(projTable);

                    if (proj.getTechnologies() != null && !proj.getTechnologies().isEmpty()) {
                        Paragraph techPara = new Paragraph("Technologies: " + proj.getTechnologies(), itemSubFont);
                        techPara.setSpacingAfter(2);
                        document.add(techPara);
                    }

                    if (proj.getDescription() != null && !proj.getDescription().trim().isEmpty()) {
                        Paragraph descPara = new Paragraph(proj.getDescription(), bodyFont);
                        descPara.setSpacingAfter(8);
                        descPara.setLeading(13);
                        document.add(descPara);
                    }
                }
            }

            // 6. Skills
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                Paragraph sectionTitle = new Paragraph("SKILLS", sectionTitleFont);
                sectionTitle.setSpacingBefore(10);
                sectionTitle.setSpacingAfter(2);
                document.add(sectionTitle);
                document.add(separator);

                StringBuilder skillsBuilder = new StringBuilder();
                for (int i = 0; i < resume.getSkills().size(); i++) {
                    SkillDto skill = resume.getSkills().get(i);
                    skillsBuilder.append(skill.getName());
                    if (skill.getLevel() != null && !skill.getLevel().trim().isEmpty()) {
                        skillsBuilder.append(" (").append(skill.getLevel()).append(")");
                    }
                    if (i < resume.getSkills().size() - 1) {
                        skillsBuilder.append(", ");
                    }
                }

                Paragraph skillsPara = new Paragraph(skillsBuilder.toString(), bodyFont);
                skillsPara.setSpacingBefore(6);
                skillsPara.setSpacingAfter(10);
                skillsPara.setLeading(13);
                document.add(skillsPara);
            }

            document.close();
        } catch (Exception e) {
            logger.error("Error generating resume PDF", e);
        }

        return out.toByteArray();
    }
}
