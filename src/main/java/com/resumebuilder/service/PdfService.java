package com.resumebuilder.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.resumebuilder.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    public byte[] generateResumePdf(ResumeDto resume) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        // Page Size
        Rectangle pageSize = PageSize.A4;
        if ("letter".equalsIgnoreCase(resume.getPageSize())) {
            pageSize = PageSize.LETTER;
        }

        // Margins
        float margin = 36f; // normal
        if ("compact".equalsIgnoreCase(resume.getPageMargins())) {
            margin = 20f;
        } else if ("large".equalsIgnoreCase(resume.getPageMargins())) {
            margin = 54f;
        }

        Document document = new Document(pageSize, margin, margin, margin, margin);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            String template = resume.getTemplate() != null ? resume.getTemplate().toLowerCase() : "classic";

            if ("professional".equals(template) || "modern".equals(template)) {
                renderSplitLayout(document, resume, writer, margin);
            } else {
                renderStandardLayout(document, resume, writer, template);
            }

            document.close();
        } catch (Exception e) {
            logger.error("Error generating resume PDF", e);
        }

        return out.toByteArray();
    }

    // Helper to parse colors safely
    private Color parseColor(String hex, Color fallback) {
        if (hex == null || hex.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Color.decode(hex);
        } catch (Exception e) {
            return fallback;
        }
    }

    // Helper to get Font based on configuration
    private Font getFont(String family, float size, int style, Color color) {
        String fontId = FontFactory.HELVETICA;
        if ("serif".equalsIgnoreCase(family) || "times".equalsIgnoreCase(family) || "executive".equalsIgnoreCase(family)) {
            fontId = FontFactory.TIMES_ROMAN;
        } else if ("monospace".equalsIgnoreCase(family) || "courier".equalsIgnoreCase(family) || "minimalist".equalsIgnoreCase(family)) {
            fontId = FontFactory.COURIER;
        }
        return FontFactory.getFont(fontId, size, style, color);
    }

    // Helper to check section visibility
    private boolean isSectionVisible(ResumeDto resume, String sectionId) {
        if (resume.getShowSections() == null || resume.getShowSections().trim().isEmpty()) {
            return true; // visible by default
        }
        String[] sections = resume.getShowSections().toLowerCase().split(",");
        for (String s : sections) {
            if (s.trim().equals(sectionId.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // STANDARD LAYOUT (Classic, Minimal, Creative, Executive, Student)
    private void renderStandardLayout(Document document, ResumeDto resume, PdfWriter writer, String template) throws DocumentException {
        // Base Styling Tokens
        Color primary = parseColor(resume.getPrimaryColor(), new Color(79, 70, 229)); // Default Indigo
        Color textMain = Color.BLACK;
        Color textMuted = Color.GRAY;
        String fontFamily = resume.getFontFamily() != null ? resume.getFontFamily() : "sans-serif";

        if ("minimal".equals(template) || "developer".equals(template)) {
            primary = Color.BLACK;
            fontFamily = "monospace";
        } else if ("executive".equals(template) || "elegant".equals(template) || "academic".equals(template)) {
            primary = parseColor(resume.getPrimaryColor(), new Color(26, 54, 93)); // Navy
            fontFamily = "serif";
        } else if ("creative".equals(template)) {
            primary = parseColor(resume.getPrimaryColor(), new Color(219, 39, 119)); // Pink/Rose
        } else if ("bold-header".equals(template)) {
            primary = parseColor(resume.getPrimaryColor(), new Color(37, 99, 235)); // Blue
        }

        // Font Sizes configuration
        float baseSize = 10f;
        if ("small".equalsIgnoreCase(resume.getFontSize()) || "compact".equals(template)) {
            baseSize = 8.5f;
        } else if ("large".equalsIgnoreCase(resume.getFontSize())) {
            baseSize = 11.5f;
        }

        Font nameFont = getFont(fontFamily, baseSize + 12, Font.BOLD, "minimal".equals(template) || "developer".equals(template) ? Color.BLACK : primary);
        Font titleFont = getFont(fontFamily, baseSize + 2, Font.BOLD, textMuted);
        Font contactFont = getFont(fontFamily, baseSize - 1, Font.NORMAL, textMuted);
        Font sectionTitleFont = getFont(fontFamily, baseSize + 3, Font.BOLD, primary);
        Font itemTitleFont = getFont(fontFamily, baseSize + 1, Font.BOLD, textMain);
        Font itemSubFont = getFont(fontFamily, baseSize, Font.ITALIC, textMuted);
        Font bodyFont = getFont(fontFamily, baseSize, Font.NORMAL, textMain);

        // Line Spacing
        float leading = 13f;
        if ("compact".equalsIgnoreCase(resume.getLineSpacing()) || "compact".equals(template)) {
            leading = 11f;
        } else if ("large".equalsIgnoreCase(resume.getLineSpacing())) {
            leading = 16f;
        }

        // 1. Personal Header Section (Creative gets banner look, Executive is centered serif, etc.)
        if ("creative".equals(template) || "bold-header".equals(template)) {
            // Colored Banner background
            PdfPTable bannerTable = new PdfPTable(1);
            bannerTable.setWidthPercentage(100);
            bannerTable.setSpacingAfter(15);
            
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(primary);
            cell.setPadding(15);
            cell.setBorder(Rectangle.NO_BORDER);

            Paragraph namePara = new Paragraph(resume.getFirstName() + " " + resume.getLastName(), getFont(fontFamily, baseSize + 14, Font.BOLD, Color.WHITE));
            namePara.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(namePara);

            String title = (resume.getTitle() != null) ? resume.getTitle() : "";
            Paragraph titlePara = new Paragraph(title.toUpperCase(), getFont(fontFamily, baseSize + 1, Font.BOLD, new Color(244, 244, 245)));
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingBefore(3);
            cell.addElement(titlePara);

            bannerTable.addCell(cell);
            document.add(bannerTable);

            // Contact info below banner
            renderContactInfo(document, resume, contactFont, Element.ALIGN_CENTER);
        } else {
            // Centered Header for Classic/Executive/Minimal
            int align = Element.ALIGN_CENTER;
            if ("minimal".equals(template) || "developer".equals(template) || "left-border".equals(template)) {
                align = Element.ALIGN_LEFT;
            }
            
            Paragraph namePara = new Paragraph(resume.getFirstName() + " " + resume.getLastName(), nameFont);
            namePara.setAlignment(align);
            document.add(namePara);

            if (resume.getTitle() != null && !resume.getTitle().isEmpty()) {
                Paragraph titlePara = new Paragraph(resume.getTitle().toUpperCase(), titleFont);
                titlePara.setAlignment(align);
                titlePara.setSpacingBefore(2);
                document.add(titlePara);
            }

            document.add(new Paragraph(" ")); // spacing
            renderContactInfo(document, resume, contactFont, align);
        }

        // Separator
        LineSeparator separator = new LineSeparator(1.2f, 100, "minimal".equals(template) ? Color.BLACK : primary, Element.ALIGN_CENTER, -4);
        
        // Define default section order
        List<String> orderList = new ArrayList<>(Arrays.asList(
            "summary", "experience", "internships", "projects", "education", 
            "certifications", "publications", "workshops", "coding_profiles", 
            "languages", "achievements", "references", "interests"
        ));

        // Override order if specified by user
        if (resume.getSectionOrder() != null && !resume.getSectionOrder().trim().isEmpty()) {
            String[] userOrder = resume.getSectionOrder().split(",");
            List<String> validOrder = new ArrayList<>();
            for (String s : userOrder) {
                String clean = s.trim().toLowerCase();
                if (orderList.contains(clean)) {
                    validOrder.add(clean);
                }
            }
            // Add any missing ones at the end
            for (String s : orderList) {
                if (!validOrder.contains(s)) {
                    validOrder.add(s);
                }
            }
            orderList = validOrder;
        }

        // Render sections in order
        for (String section : orderList) {
            if (!isSectionVisible(resume, section)) {
                continue;
            }

            switch (section) {
                case "summary":
                    if (resume.getSummary() != null && !resume.getSummary().trim().isEmpty()) {
                        addSectionHeader(document, "Professional Summary", sectionTitleFont, separator);
                        Paragraph summaryPara = new Paragraph(resume.getSummary(), bodyFont);
                        summaryPara.setLeading(leading);
                        summaryPara.setSpacingBefore(4);
                        summaryPara.setSpacingAfter(10);
                        document.add(summaryPara);
                    }
                    break;
                case "experience":
                    if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
                        addSectionHeader(document, "Work Experience", sectionTitleFont, separator);
                        for (ExperienceDto exp : resume.getExperience()) {
                            PdfPTable expTable = new PdfPTable(2);
                            expTable.setWidthPercentage(100);
                            expTable.setSpacingBefore(5);
                            expTable.setSpacingAfter(2);

                            String details = exp.getPosition() + " - " + exp.getCompany();
                            if (exp.getEmploymentType() != null && !exp.getEmploymentType().isEmpty()) {
                                details += " (" + exp.getEmploymentType() + ")";
                            }
                            PdfPCell posCell = new PdfPCell(new Phrase(details, itemTitleFont));
                            posCell.setBorder(Rectangle.NO_BORDER);

                            String dateStr = exp.getStartDate() + " - " + (Boolean.TRUE.equals(exp.getIsCurrent()) ? "Present" : exp.getEndDate());
                            if (exp.getLocation() != null && !exp.getLocation().isEmpty()) {
                                dateStr += " | " + exp.getLocation();
                            }
                            PdfPCell dateCell = new PdfPCell(new Phrase(dateStr, itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            expTable.addCell(posCell);
                            expTable.addCell(dateCell);
                            document.add(expTable);

                            if (exp.getTechnologies() != null && !exp.getTechnologies().isEmpty()) {
                                Paragraph techPara = new Paragraph("Technologies: " + exp.getTechnologies(), itemSubFont);
                                techPara.setSpacingAfter(2);
                                document.add(techPara);
                            }

                            if (exp.getResponsibilities() != null && !exp.getResponsibilities().trim().isEmpty()) {
                                Paragraph respPara = new Paragraph(exp.getResponsibilities(), bodyFont);
                                respPara.setLeading(leading);
                                respPara.setSpacingAfter(5);
                                document.add(respPara);
                            }
                        }
                    }
                    break;
                case "internships":
                    if (resume.getInternships() != null && !resume.getInternships().isEmpty()) {
                        addSectionHeader(document, "Internships", sectionTitleFont, separator);
                        for (InternshipDto internship : resume.getInternships()) {
                            PdfPTable intTable = new PdfPTable(2);
                            intTable.setWidthPercentage(100);
                            intTable.setSpacingBefore(5);
                            intTable.setSpacingAfter(2);

                            PdfPCell posCell = new PdfPCell(new Phrase(internship.getPosition() + " @ " + internship.getCompany(), itemTitleFont));
                            posCell.setBorder(Rectangle.NO_BORDER);

                            PdfPCell dateCell = new PdfPCell(new Phrase(internship.getDuration(), itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            intTable.addCell(posCell);
                            intTable.addCell(dateCell);
                            document.add(intTable);

                            if (internship.getTechnologies() != null && !internship.getTechnologies().isEmpty()) {
                                Paragraph techPara = new Paragraph("Technologies: " + internship.getTechnologies(), itemSubFont);
                                techPara.setSpacingAfter(2);
                                document.add(techPara);
                            }

                            if (internship.getDescription() != null && !internship.getDescription().trim().isEmpty()) {
                                Paragraph descPara = new Paragraph(internship.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(5);
                                document.add(descPara);
                            }
                        }
                    }
                    break;
                case "projects":
                    if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                        addSectionHeader(document, "Projects", sectionTitleFont, separator);
                        for (ProjectDto proj : resume.getProjects()) {
                            PdfPTable projTable = new PdfPTable(2);
                            projTable.setWidthPercentage(100);
                            projTable.setSpacingBefore(5);
                            projTable.setSpacingAfter(2);

                            String titleText = proj.getTitle();
                            if (proj.getRole() != null && !proj.getRole().isEmpty()) {
                                titleText += " (" + proj.getRole() + ")";
                            }
                            PdfPCell titleCell = new PdfPCell(new Phrase(titleText, itemTitleFont));
                            titleCell.setBorder(Rectangle.NO_BORDER);

                            String metaText = "";
                            if (proj.getDuration() != null && !proj.getDuration().isEmpty()) {
                                metaText += proj.getDuration();
                            }
                            if (proj.getTeamSize() != null && !proj.getTeamSize().isEmpty()) {
                                metaText += (metaText.isEmpty() ? "" : " | ") + "Team Size: " + proj.getTeamSize();
                            }
                            PdfPCell metaCell = new PdfPCell(new Phrase(metaText, itemSubFont));
                            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            metaCell.setBorder(Rectangle.NO_BORDER);

                            projTable.addCell(titleCell);
                            projTable.addCell(metaCell);
                            document.add(projTable);

                            // Project Links
                            StringBuilder links = new StringBuilder();
                            if (proj.getGithubLink() != null && !proj.getGithubLink().isEmpty()) {
                                links.append("GitHub: ").append(proj.getGithubLink());
                            }
                            if (proj.getDemoLink() != null && !proj.getDemoLink().isEmpty()) {
                                if (links.length() > 0) links.append("  |  ");
                                links.append("Demo: ").append(proj.getDemoLink());
                            }
                            if (links.length() > 0) {
                                Paragraph linkPara = new Paragraph(links.toString(), itemSubFont);
                                linkPara.setSpacingAfter(2);
                                document.add(linkPara);
                            }

                            if (proj.getTechnologies() != null && !proj.getTechnologies().isEmpty()) {
                                Paragraph techPara = new Paragraph("Technologies: " + proj.getTechnologies(), itemSubFont);
                                techPara.setSpacingAfter(2);
                                document.add(techPara);
                            }

                            if (proj.getFeatures() != null && !proj.getFeatures().isEmpty()) {
                                Paragraph featuresPara = new Paragraph("Key Features:\n" + proj.getFeatures(), bodyFont);
                                featuresPara.setLeading(leading);
                                featuresPara.setSpacingAfter(3);
                                document.add(featuresPara);
                            }

                            if (proj.getDescription() != null && !proj.getDescription().trim().isEmpty()) {
                                Paragraph descPara = new Paragraph(proj.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(6);
                                document.add(descPara);
                            }
                        }
                    }
                    break;
                case "education":
                    if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
                        addSectionHeader(document, "Education", sectionTitleFont, separator);
                        for (EducationDto edu : resume.getEducation()) {
                            PdfPTable eduTable = new PdfPTable(2);
                            eduTable.setWidthPercentage(100);
                            eduTable.setSpacingBefore(5);
                            eduTable.setSpacingAfter(2);

                            String degreeInfo = edu.getDegree() + (edu.getFieldOfStudy() != null && !edu.getFieldOfStudy().isEmpty() ? " in " + edu.getFieldOfStudy() : "");
                            String instText = edu.getInstitution();
                            if (edu.getUniversity() != null && !edu.getUniversity().isEmpty()) {
                                instText += " (" + edu.getUniversity() + ")";
                            }
                            PdfPCell degCell = new PdfPCell(new Phrase(degreeInfo + " @ " + instText, itemTitleFont));
                            degCell.setBorder(Rectangle.NO_BORDER);

                            String dateLoc = edu.getStartDate() + " - " + edu.getEndDate();
                            if (edu.getLocation() != null && !edu.getLocation().isEmpty()) {
                                dateLoc += " | " + edu.getLocation();
                            }
                            PdfPCell dateCell = new PdfPCell(new Phrase(dateLoc, itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            eduTable.addCell(degCell);
                            eduTable.addCell(dateCell);
                            document.add(eduTable);

                            // CGPA / Percentage
                            StringBuilder grades = new StringBuilder();
                            if (edu.getCgpa() != null && !edu.getCgpa().isEmpty()) {
                                grades.append("CGPA: ").append(edu.getCgpa());
                            }
                            if (edu.getPercentage() != null && !edu.getPercentage().isEmpty()) {
                                if (grades.length() > 0) grades.append("  |  ");
                                grades.append("Percentage: ").append(edu.getPercentage());
                            }
                            if (grades.length() > 0) {
                                Paragraph gradePara = new Paragraph(grades.toString(), itemSubFont);
                                gradePara.setSpacingAfter(2);
                                document.add(gradePara);
                            }

                            if (edu.getDescription() != null && !edu.getDescription().trim().isEmpty()) {
                                Paragraph descPara = new Paragraph(edu.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(6);
                                document.add(descPara);
                            }
                        }
                    }
                    break;
                case "certifications":
                    if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
                        addSectionHeader(document, "Certifications", sectionTitleFont, separator);
                        for (CertificationDto cert : resume.getCertifications()) {
                            PdfPTable certTable = new PdfPTable(2);
                            certTable.setWidthPercentage(100);
                            certTable.setSpacingBefore(5);
                            certTable.setSpacingAfter(2);

                            PdfPCell nameCell = new PdfPCell(new Phrase(cert.getName() + " - " + cert.getOrganization(), itemTitleFont));
                            nameCell.setBorder(Rectangle.NO_BORDER);

                            String dateStr = (cert.getIssueDate() != null ? cert.getIssueDate() : "") + (cert.getExpiryDate() != null && !cert.getExpiryDate().isEmpty() ? " - " + cert.getExpiryDate() : "");
                            PdfPCell dateCell = new PdfPCell(new Phrase(dateStr, itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            certTable.addCell(nameCell);
                            certTable.addCell(dateCell);
                            document.add(certTable);

                            StringBuilder details = new StringBuilder();
                            if (cert.getCredentialId() != null && !cert.getCredentialId().isEmpty()) {
                                details.append("Credential ID: ").append(cert.getCredentialId());
                            }
                            if (cert.getCredentialUrl() != null && !cert.getCredentialUrl().isEmpty()) {
                                if (details.length() > 0) details.append("  |  ");
                                details.append("URL: ").append(cert.getCredentialUrl());
                            }
                            if (details.length() > 0) {
                                Paragraph detPara = new Paragraph(details.toString(), itemSubFont);
                                detPara.setSpacingAfter(4);
                                document.add(detPara);
                            }
                        }
                    }
                    break;
                case "publications":
                    if (resume.getPublications() != null && !resume.getPublications().isEmpty()) {
                        addSectionHeader(document, "Research Publications", sectionTitleFont, separator);
                        for (PublicationDto pub : resume.getPublications()) {
                            PdfPTable pubTable = new PdfPTable(2);
                            pubTable.setWidthPercentage(100);
                            pubTable.setSpacingBefore(5);
                            pubTable.setSpacingAfter(2);

                            PdfPCell titleCell = new PdfPCell(new Phrase(pub.getTitle(), itemTitleFont));
                            titleCell.setBorder(Rectangle.NO_BORDER);

                            String pubMeta = (pub.getPublisher() != null ? pub.getPublisher() : "");
                            PdfPCell metaCell = new PdfPCell(new Phrase(pubMeta, itemSubFont));
                            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            metaCell.setBorder(Rectangle.NO_BORDER);

                            pubTable.addCell(titleCell);
                            pubTable.addCell(metaCell);
                            document.add(pubTable);

                            StringBuilder links = new StringBuilder();
                            if (pub.getDoi() != null && !pub.getDoi().isEmpty()) {
                                links.append("DOI: ").append(pub.getDoi());
                            }
                            if (pub.getLink() != null && !pub.getLink().isEmpty()) {
                                if (links.length() > 0) links.append("  |  ");
                                links.append("Link: ").append(pub.getLink());
                            }
                            if (links.length() > 0) {
                                Paragraph linkPara = new Paragraph(links.toString(), itemSubFont);
                                linkPara.setSpacingAfter(2);
                                document.add(linkPara);
                            }

                            if (pub.getDescription() != null && !pub.getDescription().isEmpty()) {
                                Paragraph descPara = new Paragraph(pub.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(4);
                                document.add(descPara);
                            }
                        }
                    }
                    break;
                case "workshops":
                    if (resume.getWorkshops() != null && !resume.getWorkshops().isEmpty()) {
                        addSectionHeader(document, "Workshops", sectionTitleFont, separator);
                        for (WorkshopDto work : resume.getWorkshops()) {
                            PdfPTable workTable = new PdfPTable(2);
                            workTable.setWidthPercentage(100);
                            workTable.setSpacingBefore(4);
                            workTable.setSpacingAfter(2);

                            PdfPCell nameCell = new PdfPCell(new Phrase(work.getName() + " (" + work.getOrganization() + ")", itemTitleFont));
                            nameCell.setBorder(Rectangle.NO_BORDER);

                            PdfPCell dateCell = new PdfPCell(new Phrase(work.getDate() != null ? work.getDate() : "", itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            workTable.addCell(nameCell);
                            workTable.addCell(dateCell);
                            document.add(workTable);
                        }
                    }
                    break;
                case "coding_profiles":
                    if (resume.getCodingProfiles() != null && !resume.getCodingProfiles().isEmpty()) {
                        addSectionHeader(document, "Coding Profiles", sectionTitleFont, separator);
                        PdfPTable cpTable = new PdfPTable(2);
                        cpTable.setWidthPercentage(100);
                        cpTable.setSpacingBefore(4);
                        cpTable.setSpacingAfter(4);

                        for (CodingProfileDto cp : resume.getCodingProfiles()) {
                            String cpText = cp.getPlatform() + ": " + cp.getUrl();
                            if (cp.getRating() != null && !cp.getRating().isEmpty()) {
                                cpText += " (Rating: " + cp.getRating() + ")";
                            }
                            PdfPCell cell = new PdfPCell(new Phrase(cpText, bodyFont));
                            cell.setBorder(Rectangle.NO_BORDER);
                            cpTable.addCell(cell);
                        }
                        // Handle odd number of cells in 2-column layout
                        if (resume.getCodingProfiles().size() % 2 != 0) {
                            PdfPCell empty = new PdfPCell(new Phrase(""));
                            empty.setBorder(Rectangle.NO_BORDER);
                            cpTable.addCell(empty);
                        }
                        document.add(cpTable);
                    }
                    break;
                case "languages":
                    if (resume.getLanguages() != null && !resume.getLanguages().isEmpty()) {
                        addSectionHeader(document, "Languages", sectionTitleFont, separator);
                        StringBuilder langBuilder = new StringBuilder();
                        for (int i = 0; i < resume.getLanguages().size(); i++) {
                            LanguageDto lang = resume.getLanguages().get(i);
                            langBuilder.append(lang.getName());
                            List<String> details = new ArrayList<>();
                            if (lang.getLevel() != null && !lang.getLevel().isEmpty()) details.add(lang.getLevel());
                            if ("yes".equalsIgnoreCase(lang.getSpeaking())) details.add("Speaking");
                            if ("yes".equalsIgnoreCase(lang.getReading())) details.add("Reading");
                            if ("yes".equalsIgnoreCase(lang.getWriting())) details.add("Writing");

                            if (!details.isEmpty()) {
                                langBuilder.append(" (").append(String.join(", ", details)).append(")");
                            }
                            if (i < resume.getLanguages().size() - 1) {
                                langBuilder.append("  |  ");
                            }
                        }
                        Paragraph langPara = new Paragraph(langBuilder.toString(), bodyFont);
                        langPara.setSpacingBefore(4);
                        langPara.setSpacingAfter(8);
                        document.add(langPara);
                    }
                    break;
                case "achievements":
                    if (resume.getAchievements() != null && !resume.getAchievements().isEmpty()) {
                        addSectionHeader(document, "Achievements & Awards", sectionTitleFont, separator);
                        for (AchievementDto ach : resume.getAchievements()) {
                            Paragraph achPara = new Paragraph("• [" + ach.getCategory() + "] " + ach.getDescription(), bodyFont);
                            achPara.setLeading(leading);
                            achPara.setSpacingBefore(2);
                            achPara.setSpacingAfter(2);
                            document.add(achPara);
                        }
                    }
                    break;
                case "references":
                    if (resume.getReferences() != null && !resume.getReferences().isEmpty()) {
                        addSectionHeader(document, "References", sectionTitleFont, separator);
                        PdfPTable refTable = new PdfPTable(2);
                        refTable.setWidthPercentage(100);
                        refTable.setSpacingBefore(5);
                        refTable.setSpacingAfter(5);

                        for (ReferenceDto ref : resume.getReferences()) {
                            StringBuilder refDetails = new StringBuilder();
                            refDetails.append(ref.getName());
                            if (ref.getRelationship() != null && !ref.getRelationship().isEmpty()) {
                                refDetails.append(" (").append(ref.getRelationship()).append(")");
                            }
                            if (ref.getCompany() != null && !ref.getCompany().isEmpty()) {
                                refDetails.append("\n").append(ref.getCompany());
                            }
                            if (ref.getEmail() != null && !ref.getEmail().isEmpty()) {
                                refDetails.append("\nEmail: ").append(ref.getEmail());
                            }
                            if (ref.getPhone() != null && !ref.getPhone().isEmpty()) {
                                refDetails.append(" | Phone: ").append(ref.getPhone());
                            }

                            PdfPCell cell = new PdfPCell(new Phrase(refDetails.toString(), bodyFont));
                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPaddingBottom(8);
                            refTable.addCell(cell);
                        }
                        if (resume.getReferences().size() % 2 != 0) {
                            PdfPCell empty = new PdfPCell(new Phrase(""));
                            empty.setBorder(Rectangle.NO_BORDER);
                            refTable.addCell(empty);
                        }
                        document.add(refTable);
                    }
                    break;
                case "interests":
                    if (resume.getInterests() != null && !resume.getInterests().isEmpty()) {
                        addSectionHeader(document, "Interests", sectionTitleFont, separator);
                        StringBuilder intBuilder = new StringBuilder();
                        for (int i = 0; i < resume.getInterests().size(); i++) {
                            intBuilder.append(resume.getInterests().get(i).getName());
                            if (i < resume.getInterests().size() - 1) {
                                intBuilder.append("  •  ");
                            }
                        }
                        Paragraph intPara = new Paragraph(intBuilder.toString(), bodyFont);
                        intPara.setSpacingBefore(4);
                        intPara.setSpacingAfter(8);
                        document.add(intPara);
                    }
                    break;
                case "skills":
                    if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                        addSectionHeader(document, "Technical Skills", sectionTitleFont, separator);
                        
                        // Group skills by category
                        Map<String, List<String>> grouped = new LinkedHashMap<>();
                        for (SkillDto skill : resume.getSkills()) {
                            String cat = skill.getCategory() != null && !skill.getCategory().trim().isEmpty() ? skill.getCategory() : "General";
                            String sText = skill.getName() + (skill.getLevel() != null && !skill.getLevel().trim().isEmpty() ? " (" + skill.getLevel() + ")" : "");
                            grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(sText);
                        }

                        for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                            Phrase phrase = new Phrase();
                            phrase.add(new Chunk(entry.getKey() + ": ", itemTitleFont));
                            phrase.add(new Chunk(String.join(", ", entry.getValue()), bodyFont));
                            
                            Paragraph p = new Paragraph(phrase);
                            p.setLeading(leading);
                            p.setSpacingBefore(3);
                            p.setSpacingAfter(3);
                            document.add(p);
                        }
                    }
                    break;
            }
        }
    }

    // PROFESSIONAL TWO-COLUMN SPLIT LAYOUT
    private void renderSplitLayout(Document document, ResumeDto resume, PdfWriter writer, float margin) throws DocumentException {
        // Build 2-column layout using a root PdfPTable
        PdfPTable rootTable = new PdfPTable(2);
        rootTable.setWidthPercentage(100);
        rootTable.setWidths(new float[]{30f, 70f}); // Left Sidebar 30%, Right Body 70%

        // Base Styling Tokens
        Color primary = parseColor(resume.getPrimaryColor(), new Color(79, 70, 229));
        Color textMain = Color.BLACK;
        Color textMuted = Color.GRAY;
        String fontFamily = resume.getFontFamily() != null ? resume.getFontFamily() : "sans-serif";

        float baseSize = 10f;
        if ("small".equalsIgnoreCase(resume.getFontSize())) {
            baseSize = 8.5f;
        } else if ("large".equalsIgnoreCase(resume.getFontSize())) {
            baseSize = 11.5f;
        }

        // Fonts
        Font nameFont = getFont(fontFamily, baseSize + 10, Font.BOLD, primary);
        Font titleFont = getFont(fontFamily, baseSize + 1, Font.BOLD, textMain);
        Font sideTitleFont = getFont(fontFamily, baseSize + 2, Font.BOLD, primary);
        Font sideTextFont = getFont(fontFamily, baseSize - 1f, Font.NORMAL, textMain);
        Font mainTitleFont = getFont(fontFamily, baseSize + 3, Font.BOLD, primary);
        Font itemTitleFont = getFont(fontFamily, baseSize + 1, Font.BOLD, textMain);
        Font itemSubFont = getFont(fontFamily, baseSize, Font.ITALIC, textMuted);
        Font bodyFont = getFont(fontFamily, baseSize, Font.NORMAL, textMain);

        float leading = 13f;
        if ("compact".equalsIgnoreCase(resume.getLineSpacing())) {
            leading = 11f;
        } else if ("large".equalsIgnoreCase(resume.getLineSpacing())) {
            leading = 16f;
        }

        // Separators
        LineSeparator sideSep = new LineSeparator(1f, 100, primary, Element.ALIGN_LEFT, -2);
        LineSeparator mainSep = new LineSeparator(1.2f, 100, primary, Element.ALIGN_LEFT, -4);

        // --- CELL 1: LEFT SIDEBAR ---
        PdfPCell sidebarCell = new PdfPCell();
        sidebarCell.setPaddingRight(10);
        sidebarCell.setBorder(Rectangle.NO_BORDER);

        // Sidebar content: Personal details, Contact Links, Skills, Languages, Coding Profiles, Interests
        // Name & Title
        Paragraph namePara = new Paragraph(resume.getFirstName() + "\n" + resume.getLastName(), nameFont);
        namePara.setSpacingAfter(4);
        sidebarCell.addElement(namePara);

        if (resume.getTitle() != null && !resume.getTitle().isEmpty()) {
            Paragraph titlePara = new Paragraph(resume.getTitle(), titleFont);
            titlePara.setSpacingAfter(10);
            sidebarCell.addElement(titlePara);
        }

        // Contact Info
        addSideSectionHeader(sidebarCell, "Contact Info", sideTitleFont, sideSep);
        Paragraph contactDetails = new Paragraph();
        contactDetails.setFont(sideTextFont);
        contactDetails.setLeading(11f);
        
        contactDetails.add(resume.getEmail() + "\n");
        if (resume.getPhone() != null && !resume.getPhone().trim().isEmpty()) {
            contactDetails.add(resume.getPhone() + "\n");
        }
        if (resume.getAddress() != null && !resume.getAddress().trim().isEmpty()) {
            String addr = resume.getAddress();
            if (resume.getCity() != null && !resume.getCity().isEmpty()) addr += ", " + resume.getCity();
            contactDetails.add(addr + "\n");
        }
        
        // Links
        if (resume.getLinkedin() != null && !resume.getLinkedin().isEmpty()) contactDetails.add("LI: " + resume.getLinkedin() + "\n");
        if (resume.getGithub() != null && !resume.getGithub().isEmpty()) contactDetails.add("GH: " + resume.getGithub() + "\n");
        if (resume.getPortfolio() != null && !resume.getPortfolio().isEmpty()) contactDetails.add("Portfolio: " + resume.getPortfolio() + "\n");
        if (resume.getWebsite() != null && !resume.getWebsite().isEmpty()) contactDetails.add("Web: " + resume.getWebsite() + "\n");
        
        contactDetails.setSpacingAfter(10);
        sidebarCell.addElement(contactDetails);

        // Skills (in sidebar)
        if (isSectionVisible(resume, "skills") && resume.getSkills() != null && !resume.getSkills().isEmpty()) {
            addSideSectionHeader(sidebarCell, "Skills", sideTitleFont, sideSep);
            
            Map<String, List<String>> grouped = new LinkedHashMap<>();
            for (SkillDto skill : resume.getSkills()) {
                String cat = skill.getCategory() != null && !skill.getCategory().trim().isEmpty() ? skill.getCategory() : "General";
                String sText = skill.getName() + (skill.getLevel() != null && !skill.getLevel().trim().isEmpty() ? " (" + skill.getLevel() + ")" : "");
                grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(sText);
            }

            for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                Paragraph p = new Paragraph(entry.getKey() + ":\n" + String.join(", ", entry.getValue()), sideTextFont);
                p.setLeading(10f);
                p.setSpacingAfter(4);
                sidebarCell.addElement(p);
            }
        }

        // Languages
        if (isSectionVisible(resume, "languages") && resume.getLanguages() != null && !resume.getLanguages().isEmpty()) {
            addSideSectionHeader(sidebarCell, "Languages", sideTitleFont, sideSep);
            for (LanguageDto lang : resume.getLanguages()) {
                String lText = lang.getName() + (lang.getLevel() != null ? " (" + lang.getLevel() + ")" : "");
                Paragraph p = new Paragraph("• " + lText, sideTextFont);
                p.setSpacingAfter(2);
                sidebarCell.addElement(p);
            }
        }

        // Coding Profiles
        if (isSectionVisible(resume, "coding_profiles") && resume.getCodingProfiles() != null && !resume.getCodingProfiles().isEmpty()) {
            addSideSectionHeader(sidebarCell, "Profiles", sideTitleFont, sideSep);
            for (CodingProfileDto cp : resume.getCodingProfiles()) {
                String label = cp.getPlatform();
                if (cp.getRating() != null && !cp.getRating().isEmpty()) {
                    label += " (" + cp.getRating() + ")";
                }
                Paragraph p = new Paragraph(label + ":\n" + cp.getUrl(), sideTextFont);
                p.setLeading(10f);
                p.setSpacingAfter(4);
                sidebarCell.addElement(p);
            }
        }

        // Interests
        if (isSectionVisible(resume, "interests") && resume.getInterests() != null && !resume.getInterests().isEmpty()) {
            addSideSectionHeader(sidebarCell, "Interests", sideTitleFont, sideSep);
            for (InterestDto interest : resume.getInterests()) {
                Paragraph p = new Paragraph("• " + interest.getName(), sideTextFont);
                p.setSpacingAfter(2);
                sidebarCell.addElement(p);
            }
        }

        rootTable.addCell(sidebarCell);

        // --- CELL 2: RIGHT MAIN CONTENT COLUMN ---
        PdfPCell mainCell = new PdfPCell();
        mainCell.setPaddingLeft(10);
        mainCell.setBorder(Rectangle.NO_BORDER);

        // Section Order for main column
        List<String> orderList = new ArrayList<>(Arrays.asList(
            "summary", "experience", "internships", "projects", "education", 
            "certifications", "publications", "workshops", "achievements", "references"
        ));

        if (resume.getSectionOrder() != null && !resume.getSectionOrder().trim().isEmpty()) {
            String[] userOrder = resume.getSectionOrder().split(",");
            List<String> validOrder = new ArrayList<>();
            for (String s : userOrder) {
                String clean = s.trim().toLowerCase();
                if (orderList.contains(clean)) {
                    validOrder.add(clean);
                }
            }
            for (String s : orderList) {
                if (!validOrder.contains(s)) {
                    validOrder.add(s);
                }
            }
            orderList = validOrder;
        }

        for (String section : orderList) {
            if (!isSectionVisible(resume, section)) {
                continue;
            }

            switch (section) {
                case "summary":
                    if (resume.getSummary() != null && !resume.getSummary().trim().isEmpty()) {
                        addSideSectionHeader(mainCell, "Professional Summary", mainTitleFont, mainSep);
                        Paragraph summaryPara = new Paragraph(resume.getSummary(), bodyFont);
                        summaryPara.setLeading(leading);
                        summaryPara.setSpacingAfter(10);
                        mainCell.addElement(summaryPara);
                    }
                    break;
                case "experience":
                    if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
                        addSideSectionHeader(mainCell, "Work Experience", mainTitleFont, mainSep);
                        for (ExperienceDto exp : resume.getExperience()) {
                            PdfPTable expTable = new PdfPTable(2);
                            expTable.setWidthPercentage(100);
                            expTable.setSpacingBefore(4);

                            String details = exp.getPosition() + " - " + exp.getCompany();
                            if (exp.getEmploymentType() != null && !exp.getEmploymentType().isEmpty()) {
                                details += " (" + exp.getEmploymentType() + ")";
                            }
                            PdfPCell posCell = new PdfPCell(new Phrase(details, itemTitleFont));
                            posCell.setBorder(Rectangle.NO_BORDER);

                            String dateStr = exp.getStartDate() + " - " + (Boolean.TRUE.equals(exp.getIsCurrent()) ? "Present" : exp.getEndDate());
                            if (exp.getLocation() != null && !exp.getLocation().isEmpty()) {
                                dateStr += " | " + exp.getLocation();
                            }
                            PdfPCell dateCell = new PdfPCell(new Phrase(dateStr, itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            expTable.addCell(posCell);
                            expTable.addCell(dateCell);
                            mainCell.addElement(expTable);

                            if (exp.getTechnologies() != null && !exp.getTechnologies().isEmpty()) {
                                Paragraph techPara = new Paragraph("Technologies: " + exp.getTechnologies(), itemSubFont);
                                techPara.setSpacingAfter(1);
                                mainCell.addElement(techPara);
                            }

                            if (exp.getResponsibilities() != null && !exp.getResponsibilities().trim().isEmpty()) {
                                Paragraph respPara = new Paragraph(exp.getResponsibilities(), bodyFont);
                                respPara.setLeading(leading);
                                respPara.setSpacingAfter(6);
                                mainCell.addElement(respPara);
                            }
                        }
                    }
                    break;
                case "internships":
                    if (resume.getInternships() != null && !resume.getInternships().isEmpty()) {
                        addSideSectionHeader(mainCell, "Internships", mainTitleFont, mainSep);
                        for (InternshipDto internship : resume.getInternships()) {
                            PdfPTable intTable = new PdfPTable(2);
                            intTable.setWidthPercentage(100);
                            intTable.setSpacingBefore(4);

                            PdfPCell posCell = new PdfPCell(new Phrase(internship.getPosition() + " @ " + internship.getCompany(), itemTitleFont));
                            posCell.setBorder(Rectangle.NO_BORDER);

                            PdfPCell dateCell = new PdfPCell(new Phrase(internship.getDuration(), itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            intTable.addCell(posCell);
                            intTable.addCell(dateCell);
                            mainCell.addElement(intTable);

                            if (internship.getTechnologies() != null && !internship.getTechnologies().isEmpty()) {
                                Paragraph techPara = new Paragraph("Technologies: " + internship.getTechnologies(), itemSubFont);
                                techPara.setSpacingAfter(1);
                                mainCell.addElement(techPara);
                            }

                            if (internship.getDescription() != null && !internship.getDescription().trim().isEmpty()) {
                                Paragraph descPara = new Paragraph(internship.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(6);
                                mainCell.addElement(descPara);
                            }
                        }
                    }
                    break;
                case "projects":
                    if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                        addSideSectionHeader(mainCell, "Projects", mainTitleFont, mainSep);
                        for (ProjectDto proj : resume.getProjects()) {
                            PdfPTable projTable = new PdfPTable(2);
                            projTable.setWidthPercentage(100);
                            projTable.setSpacingBefore(4);

                            String titleText = proj.getTitle();
                            if (proj.getRole() != null && !proj.getRole().isEmpty()) {
                                titleText += " (" + proj.getRole() + ")";
                            }
                            PdfPCell titleCell = new PdfPCell(new Phrase(titleText, itemTitleFont));
                            titleCell.setBorder(Rectangle.NO_BORDER);

                            String metaText = "";
                            if (proj.getDuration() != null && !proj.getDuration().isEmpty()) {
                                metaText += proj.getDuration();
                            }
                            if (proj.getTeamSize() != null && !proj.getTeamSize().isEmpty()) {
                                metaText += (metaText.isEmpty() ? "" : " | ") + "Team: " + proj.getTeamSize();
                            }
                            PdfPCell metaCell = new PdfPCell(new Phrase(metaText, itemSubFont));
                            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            metaCell.setBorder(Rectangle.NO_BORDER);

                            projTable.addCell(titleCell);
                            projTable.addCell(metaCell);
                            mainCell.addElement(projTable);

                            // Project Links
                            StringBuilder links = new StringBuilder();
                            if (proj.getGithubLink() != null && !proj.getGithubLink().isEmpty()) {
                                links.append("GitHub: ").append(proj.getGithubLink());
                            }
                            if (proj.getDemoLink() != null && !proj.getDemoLink().isEmpty()) {
                                if (links.length() > 0) links.append("  |  ");
                                links.append("Demo: ").append(proj.getDemoLink());
                            }
                            if (links.length() > 0) {
                                Paragraph linkPara = new Paragraph(links.toString(), itemSubFont);
                                linkPara.setSpacingAfter(1);
                                mainCell.addElement(linkPara);
                            }

                            if (proj.getTechnologies() != null && !proj.getTechnologies().isEmpty()) {
                                Paragraph techPara = new Paragraph("Technologies: " + proj.getTechnologies(), itemSubFont);
                                techPara.setSpacingAfter(1);
                                mainCell.addElement(techPara);
                            }

                            if (proj.getFeatures() != null && !proj.getFeatures().isEmpty()) {
                                Paragraph feat = new Paragraph("Key Features:\n" + proj.getFeatures(), bodyFont);
                                feat.setLeading(leading);
                                feat.setSpacingAfter(3);
                                mainCell.addElement(feat);
                            }

                            if (proj.getDescription() != null && !proj.getDescription().trim().isEmpty()) {
                                Paragraph descPara = new Paragraph(proj.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(6);
                                mainCell.addElement(descPara);
                            }
                        }
                    }
                    break;
                case "education":
                    if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
                        addSideSectionHeader(mainCell, "Education", mainTitleFont, mainSep);
                        for (EducationDto edu : resume.getEducation()) {
                            PdfPTable eduTable = new PdfPTable(2);
                            eduTable.setWidthPercentage(100);
                            eduTable.setSpacingBefore(4);

                            String degreeInfo = edu.getDegree() + (edu.getFieldOfStudy() != null && !edu.getFieldOfStudy().isEmpty() ? " in " + edu.getFieldOfStudy() : "");
                            String instText = edu.getInstitution();
                            if (edu.getUniversity() != null && !edu.getUniversity().isEmpty()) {
                                instText += " (" + edu.getUniversity() + ")";
                            }
                            PdfPCell degCell = new PdfPCell(new Phrase(degreeInfo + " @ " + instText, itemTitleFont));
                            degCell.setBorder(Rectangle.NO_BORDER);

                            String dateLoc = edu.getStartDate() + " - " + edu.getEndDate();
                            if (edu.getLocation() != null && !edu.getLocation().isEmpty()) {
                                dateLoc += " | " + edu.getLocation();
                            }
                            PdfPCell dateCell = new PdfPCell(new Phrase(dateLoc, itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            eduTable.addCell(degCell);
                            eduTable.addCell(dateCell);
                            mainCell.addElement(eduTable);

                            StringBuilder grades = new StringBuilder();
                            if (edu.getCgpa() != null && !edu.getCgpa().isEmpty()) {
                                grades.append("CGPA: ").append(edu.getCgpa());
                            }
                            if (edu.getPercentage() != null && !edu.getPercentage().isEmpty()) {
                                if (grades.length() > 0) grades.append("  |  ");
                                grades.append("Percentage: ").append(edu.getPercentage());
                            }
                            if (grades.length() > 0) {
                                Paragraph gradePara = new Paragraph(grades.toString(), itemSubFont);
                                gradePara.setSpacingAfter(1);
                                mainCell.addElement(gradePara);
                            }

                            if (edu.getDescription() != null && !edu.getDescription().trim().isEmpty()) {
                                Paragraph descPara = new Paragraph(edu.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(6);
                                mainCell.addElement(descPara);
                            }
                        }
                    }
                    break;
                case "certifications":
                    if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
                        addSideSectionHeader(mainCell, "Certifications", mainTitleFont, mainSep);
                        for (CertificationDto cert : resume.getCertifications()) {
                            PdfPTable certTable = new PdfPTable(2);
                            certTable.setWidthPercentage(100);
                            certTable.setSpacingBefore(4);

                            PdfPCell nameCell = new PdfPCell(new Phrase(cert.getName() + " - " + cert.getOrganization(), itemTitleFont));
                            nameCell.setBorder(Rectangle.NO_BORDER);

                            String dateStr = (cert.getIssueDate() != null ? cert.getIssueDate() : "") + (cert.getExpiryDate() != null && !cert.getExpiryDate().isEmpty() ? " - " + cert.getExpiryDate() : "");
                            PdfPCell dateCell = new PdfPCell(new Phrase(dateStr, itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            certTable.addCell(nameCell);
                            certTable.addCell(dateCell);
                            mainCell.addElement(certTable);

                            StringBuilder details = new StringBuilder();
                            if (cert.getCredentialId() != null && !cert.getCredentialId().isEmpty()) {
                                details.append("ID: ").append(cert.getCredentialId());
                            }
                            if (cert.getCredentialUrl() != null && !cert.getCredentialUrl().isEmpty()) {
                                if (details.length() > 0) details.append("  |  ");
                                details.append("Link: ").append(cert.getCredentialUrl());
                            }
                            if (details.length() > 0) {
                                Paragraph detPara = new Paragraph(details.toString(), itemSubFont);
                                detPara.setSpacingAfter(3);
                                mainCell.addElement(detPara);
                            }
                        }
                    }
                    break;
                case "publications":
                    if (resume.getPublications() != null && !resume.getPublications().isEmpty()) {
                        addSideSectionHeader(mainCell, "Research Publications", mainTitleFont, mainSep);
                        for (PublicationDto pub : resume.getPublications()) {
                            PdfPTable pubTable = new PdfPTable(2);
                            pubTable.setWidthPercentage(100);
                            pubTable.setSpacingBefore(4);

                            PdfPCell titleCell = new PdfPCell(new Phrase(pub.getTitle(), itemTitleFont));
                            titleCell.setBorder(Rectangle.NO_BORDER);

                            String pubMeta = (pub.getPublisher() != null ? pub.getPublisher() : "");
                            PdfPCell metaCell = new PdfPCell(new Phrase(pubMeta, itemSubFont));
                            metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            metaCell.setBorder(Rectangle.NO_BORDER);

                            pubTable.addCell(titleCell);
                            pubTable.addCell(metaCell);
                            mainCell.addElement(pubTable);

                            StringBuilder links = new StringBuilder();
                            if (pub.getDoi() != null && !pub.getDoi().isEmpty()) {
                                links.append("DOI: ").append(pub.getDoi());
                            }
                            if (pub.getLink() != null && !pub.getLink().isEmpty()) {
                                if (links.length() > 0) links.append("  |  ");
                                links.append("Link: ").append(pub.getLink());
                            }
                            if (links.length() > 0) {
                                Paragraph linkPara = new Paragraph(links.toString(), itemSubFont);
                                linkPara.setSpacingAfter(2);
                                mainCell.addElement(linkPara);
                            }

                            if (pub.getDescription() != null && !pub.getDescription().isEmpty()) {
                                Paragraph descPara = new Paragraph(pub.getDescription(), bodyFont);
                                descPara.setLeading(leading);
                                descPara.setSpacingAfter(4);
                                mainCell.addElement(descPara);
                            }
                        }
                    }
                    break;
                case "workshops":
                    if (resume.getWorkshops() != null && !resume.getWorkshops().isEmpty()) {
                        addSideSectionHeader(mainCell, "Workshops", mainTitleFont, mainSep);
                        for (WorkshopDto work : resume.getWorkshops()) {
                            PdfPTable workTable = new PdfPTable(2);
                            workTable.setWidthPercentage(100);
                            workTable.setSpacingBefore(4);

                            PdfPCell nameCell = new PdfPCell(new Phrase(work.getName() + " (" + work.getOrganization() + ")", itemTitleFont));
                            nameCell.setBorder(Rectangle.NO_BORDER);

                            PdfPCell dateCell = new PdfPCell(new Phrase(work.getDate() != null ? work.getDate() : "", itemSubFont));
                            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            dateCell.setBorder(Rectangle.NO_BORDER);

                            workTable.addCell(nameCell);
                            workTable.addCell(dateCell);
                            mainCell.addElement(workTable);
                        }
                    }
                    break;
                case "achievements":
                    if (resume.getAchievements() != null && !resume.getAchievements().isEmpty()) {
                        addSideSectionHeader(mainCell, "Achievements & Awards", mainTitleFont, mainSep);
                        for (AchievementDto ach : resume.getAchievements()) {
                            Paragraph achPara = new Paragraph("• [" + ach.getCategory() + "] " + ach.getDescription(), bodyFont);
                            achPara.setLeading(leading);
                            achPara.setSpacingAfter(2);
                            mainCell.addElement(achPara);
                        }
                    }
                    break;
                case "references":
                    if (resume.getReferences() != null && !resume.getReferences().isEmpty()) {
                        addSideSectionHeader(mainCell, "References", mainTitleFont, mainSep);
                        PdfPTable refTable = new PdfPTable(1);
                        refTable.setWidthPercentage(100);
                        refTable.setSpacingBefore(4);

                        for (ReferenceDto ref : resume.getReferences()) {
                            StringBuilder refDetails = new StringBuilder();
                            refDetails.append(ref.getName());
                            if (ref.getRelationship() != null && !ref.getRelationship().isEmpty()) {
                                refDetails.append(" (").append(ref.getRelationship()).append(")");
                            }
                            if (ref.getCompany() != null && !ref.getCompany().isEmpty()) {
                                refDetails.append("\n").append(ref.getCompany());
                            }
                            if (ref.getEmail() != null && !ref.getEmail().isEmpty()) {
                                refDetails.append("\nEmail: ").append(ref.getEmail());
                            }
                            if (ref.getPhone() != null && !ref.getPhone().isEmpty()) {
                                refDetails.append(" | Phone: ").append(ref.getPhone());
                            }

                            PdfPCell cell = new PdfPCell(new Phrase(refDetails.toString(), bodyFont));
                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setPaddingBottom(6);
                            refTable.addCell(cell);
                        }
                        mainCell.addElement(refTable);
                    }
                    break;
            }
        }

        rootTable.addCell(mainCell);
        document.add(rootTable);
    }

    // Helper to format contact line
    private void renderContactInfo(Document document, ResumeDto resume, Font font, int alignment) throws DocumentException {
        StringBuilder contactBuilder = new StringBuilder();
        contactBuilder.append(resume.getEmail());
        if (resume.getPhone() != null && !resume.getPhone().trim().isEmpty()) {
            contactBuilder.append("  |  ").append(resume.getPhone());
        }
        
        StringBuilder location = new StringBuilder();
        if (resume.getAddress() != null && !resume.getAddress().trim().isEmpty()) {
            location.append(resume.getAddress().trim());
        }
        if (resume.getCity() != null && !resume.getCity().trim().isEmpty()) {
            if (location.length() > 0) location.append(", ");
            location.append(resume.getCity().trim());
        }
        if (resume.getCountry() != null && !resume.getCountry().trim().isEmpty()) {
            if (location.length() > 0) location.append(", ");
            location.append(resume.getCountry().trim());
        }
        if (location.length() > 0) {
            contactBuilder.append("  |  ").append(location);
        }

        Paragraph contactPara = new Paragraph(contactBuilder.toString(), font);
        contactPara.setAlignment(alignment);
        document.add(contactPara);

        // Social links on second header line
        StringBuilder socialBuilder = new StringBuilder();
        if (resume.getLinkedin() != null && !resume.getLinkedin().trim().isEmpty()) {
            socialBuilder.append("LinkedIn: ").append(resume.getLinkedin().trim());
        }
        if (resume.getGithub() != null && !resume.getGithub().trim().isEmpty()) {
            if (socialBuilder.length() > 0) socialBuilder.append("   |   ");
            socialBuilder.append("GitHub: ").append(resume.getGithub().trim());
        }
        if (resume.getPortfolio() != null && !resume.getPortfolio().trim().isEmpty()) {
            if (socialBuilder.length() > 0) socialBuilder.append("   |   ");
            socialBuilder.append("Portfolio: ").append(resume.getPortfolio().trim());
        }
        if (resume.getWebsite() != null && !resume.getWebsite().trim().isEmpty()) {
            if (socialBuilder.length() > 0) socialBuilder.append("   |   ");
            socialBuilder.append("Web: ").append(resume.getWebsite().trim());
        }

        if (socialBuilder.length() > 0) {
            Paragraph socialPara = new Paragraph(socialBuilder.toString(), font);
            socialPara.setAlignment(alignment);
            socialPara.setSpacingBefore(1);
            socialPara.setSpacingAfter(8);
            document.add(socialPara);
        } else {
            contactPara.setSpacingAfter(8);
        }
    }

    private void addSectionHeader(Document document, String title, Font font, LineSeparator separator) throws DocumentException {
        Paragraph titlePara = new Paragraph(title.toUpperCase(), font);
        titlePara.setSpacingBefore(8);
        titlePara.setSpacingAfter(2);
        document.add(titlePara);
        document.add(separator);
    }

    private void addSideSectionHeader(PdfPCell cell, String title, Font font, LineSeparator separator) {
        Paragraph titlePara = new Paragraph(title.toUpperCase(), font);
        titlePara.setSpacingBefore(10);
        titlePara.setSpacingAfter(2);
        cell.addElement(titlePara);
        
        // We put separator inside a table to prevent rendering issues in cell context
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(4);
        
        PdfPCell sepCell = new PdfPCell();
        sepCell.setBorder(Rectangle.NO_BORDER);
        sepCell.addElement(separator);
        table.addCell(sepCell);
        
        cell.addElement(table);
    }

    private void addSideSectionHeader(Document document, String title, Font font, LineSeparator separator) throws DocumentException {
        addSectionHeader(document, title, font, separator);
    }
}
