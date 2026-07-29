let resumeId = null;

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    resumeId = urlParams.get('id');

    // WIZARD TABS NAVIGATION LOGIC
    document.querySelectorAll('.wizard-step-link').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.wizard-step-link').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const sectionId = btn.getAttribute('data-section');
            document.querySelectorAll('.wizard-card').forEach(card => card.classList.add('d-none'));
            document.getElementById(sectionId).classList.remove('d-none');
        });
    });

    // TEMPLATE QUICK SELECT DROPDOWN
    const quickSelect = document.getElementById('quick-template-select');
    const mainSelect = document.getElementById('template-select');
    if (quickSelect) {
        quickSelect.addEventListener('change', (e) => {
            const temp = e.target.value;
            if (mainSelect) mainSelect.value = temp;
            updatePreview();
        });
    }
    if (mainSelect) {
        mainSelect.addEventListener('change', (e) => {
            const temp = e.target.value;
            if (quickSelect) quickSelect.value = temp;
            updatePreview();
        });
    }

    // CUSTOMIZE COLOR PICKER
    document.getElementById('color-picker').addEventListener('input', (e) => {
        document.getElementById('color-text-input').value = e.target.value;
        updatePreview();
    });
    document.getElementById('color-text-input').addEventListener('input', (e) => {
        document.getElementById('color-picker').value = e.target.value;
        updatePreview();
    });

    // ZOOM PREVIEW CONTROLS
    let currentZoom = 100;
    const zoomText = document.getElementById('zoom-text');
    const zoomWrapper = document.getElementById('resume-preview-wrapper');
    document.getElementById('zoom-in-btn').addEventListener('click', () => {
        if (currentZoom < 150) {
            currentZoom += 10;
            zoomText.innerText = `${currentZoom}%`;
            zoomWrapper.style.transform = `scale(${currentZoom / 100})`;
        }
    });
    document.getElementById('zoom-out-btn').addEventListener('click', () => {
        if (currentZoom > 50) {
            currentZoom -= 10;
            zoomText.innerText = `${currentZoom}%`;
            zoomWrapper.style.transform = `scale(${currentZoom / 100})`;
        }
    });

    // REGISTER BUTTON ACTIONS FOR LISTS
    document.getElementById('add-edu-btn').addEventListener('click', () => addEducationItem());
    document.getElementById('add-exp-btn').addEventListener('click', () => addExperienceItem());
    document.getElementById('add-proj-btn').addEventListener('click', () => addProjectItem());
    document.getElementById('add-skill-btn').addEventListener('click', () => addSkillItem());
    document.getElementById('add-cert-btn').addEventListener('click', () => addCertificationItem());
    document.getElementById('add-intern-btn').addEventListener('click', () => addInternshipItem());
    document.getElementById('add-pub-btn').addEventListener('click', () => addPublicationItem());
    document.getElementById('add-workshop-btn').addEventListener('click', () => addWorkshopItem());
    document.getElementById('add-ach-btn').addEventListener('click', () => addAchievementItem());
    document.getElementById('add-coding-btn').addEventListener('click', () => addCodingProfileItem());
    document.getElementById('add-lang-btn').addEventListener('click', () => addLanguageItem());
    document.getElementById('add-interest-btn').addEventListener('click', () => addInterestItem());
    document.getElementById('add-ref-btn').addEventListener('click', () => addReferenceItem());

    // STYLE CUSTOMIZER DROPDOWNS
    document.getElementById('template-select').addEventListener('change', updatePreview);
    document.getElementById('font-select').addEventListener('change', updatePreview);
    document.getElementById('font-size-select').addEventListener('change', updatePreview);
    document.getElementById('margin-select').addEventListener('change', updatePreview);
    document.getElementById('spacing-select').addEventListener('change', updatePreview);
    document.getElementById('page-size-select').addEventListener('change', updatePreview);

    // AI DEMO TRIGGERS
    document.querySelector('.btn-ai-suggest-summary').addEventListener('click', triggerAiSummary);
    document.querySelector('.btn-ai-optimize-summary').addEventListener('click', triggerAiOptimizeSummary);
    document.querySelector('.btn-ai-suggest-skills').addEventListener('click', triggerAiSuggestSkills);

    // BACKUP & RESTORE ACTIONS
    document.getElementById('btn-export-json').addEventListener('click', exportJsonBackup);
    document.getElementById('btn-export-html').addEventListener('click', exportHtmlResume);
    document.getElementById('import-json-file').addEventListener('change', importJsonBackup);

    // SAVE & PDF DOWNLOAD
    document.getElementById('save-btn').addEventListener('click', () => saveResume(false));
    document.getElementById('pdf-btn').addEventListener('click', () => downloadPdf());

    // LISTEN FOR ANY EDIT CHANGES TO DYNAMICALLY UPDATE PREVIEW & ATS SCORE
    const centerEditorPane = document.querySelector('.center-editor-pane');
    centerEditorPane.addEventListener('input', () => {
        updateCharCounter();
        updatePreview();
        calculateAtsScore();
    });

    // INITIAL LOAD
    if (resumeId) {
        loadResume(resumeId);
    } else {
        // Setup initial default sections to guide user
        addEducationItem();
        addExperienceItem();
        addProjectItem();
        addSkillItem({ name: 'Java', level: 'Expert', category: 'Programming Languages' });
        addSkillItem({ name: 'Spring Boot', level: 'Intermediate', category: 'Frameworks' });
        addLanguageItem({ name: 'English', level: 'Fluent', reading: 'yes', writing: 'yes', speaking: 'yes' });
        addCodingProfileItem({ platform: 'GitHub', url: 'github.com/myusername', rating: '' });
        addInterestItem({ name: 'Open Source' });
        
        setupSectionVisibilityList();
        updatePreview();
    }

    // Add Next/Previous buttons to each wizard card dynamically
    const cards = Array.from(document.querySelectorAll('.wizard-card'));
    const stepLinks = Array.from(document.querySelectorAll('.wizard-step-link'));
    
    cards.forEach((card, index) => {
        const footer = document.createElement('div');
        footer.className = 'd-flex justify-content-between mt-4 pt-3 border-top';
        
        if (index > 0) {
            const prevBtn = document.createElement('button');
            prevBtn.type = 'button';
            prevBtn.className = 'btn-flat btn-flat-outline btn-sm';
            prevBtn.innerText = 'Previous Step';
            prevBtn.addEventListener('click', () => {
                stepLinks[index - 1].click();
            });
            footer.appendChild(prevBtn);
        } else {
            const spacer = document.createElement('div');
            footer.appendChild(spacer);
        }
        
        if (index < cards.length - 1) {
            const nextBtn = document.createElement('button');
            nextBtn.type = 'button';
            nextBtn.className = 'btn-add-action ms-auto';
            nextBtn.innerText = 'Next Step';
            nextBtn.addEventListener('click', () => {
                stepLinks[index + 1].click();
            });
            footer.appendChild(nextBtn);
        }
        
        card.appendChild(footer);
    });

    // Handle change events (in addition to input) for checkboxes and selects
    centerEditorPane.addEventListener('change', () => {
        updatePreview();
        calculateAtsScore();
    });

    // Auto-save setup (every 15 seconds)
    setInterval(() => {
        const token = localStorage.getItem('token');
        if (token) {
            saveResume(true); // silent auto-save
        }
    }, 15000);
});

// Dynamic lists indices
let eduCount = 0;
let expCount = 0;
let projCount = 0;
let skillCount = 0;
let certCount = 0;
let internCount = 0;
let pubCount = 0;
let workshopCount = 0;
let achCount = 0;
let codingCount = 0;
let langCount = 0;
let interestCount = 0;
let refCount = 0;

// Section Ordering & Visibility lists
let activeSectionsOrder = [
    "summary", "experience", "internships", "projects", "education", 
    "certifications", "publications", "workshops", "coding_profiles", 
    "languages", "achievements", "references", "interests"
];

// Helper to remove any item from DOM safely
window.removeItem = function(id) {
    const el = document.getElementById(id);
    if (el) {
        el.remove();
        updatePreview();
        calculateAtsScore();
    }
};

// DYNAMIC SECTION LISTS RENDERING METHODS
function addEducationItem(data = null) {
    const list = document.getElementById('education-list');
    const index = eduCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `edu-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Education #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('edu-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-6">
                <input type="text" class="form-input-flat edu-inst" placeholder="School/University" required value="${data?.institution || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat edu-degree" placeholder="Degree (e.g. B.Tech / B.S.)" required value="${data?.degree || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat edu-field" placeholder="Field of Study" value="${data?.fieldOfStudy || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat edu-univ" placeholder="University Board" value="${data?.university || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat edu-start" placeholder="Start Date" value="${data?.startDate || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat edu-end" placeholder="End Date" value="${data?.endDate || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat edu-cgpa" placeholder="CGPA" value="${data?.cgpa || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat edu-percentage" placeholder="Percentage %" value="${data?.percentage || ''}">
            </div>
            <div class="col-12">
                <input type="text" class="form-input-flat edu-loc" placeholder="Location (City, Country)" value="${data?.location || ''}">
            </div>
            <div class="col-12">
                <textarea class="form-input-flat edu-desc" rows="2" placeholder="Relevant achievements or honors...">${data?.description || ''}</textarea>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addExperienceItem(data = null) {
    const list = document.getElementById('experience-list');
    const index = expCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `exp-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Work Experience #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('exp-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-6">
                <input type="text" class="form-input-flat exp-comp" placeholder="Company Name" required value="${data?.company || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat exp-pos" placeholder="Position" required value="${data?.position || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat exp-type" placeholder="Employment Type (Full-time, Intern)" value="${data?.employmentType || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat exp-loc" placeholder="Location" value="${data?.location || ''}">
            </div>
            <div class="col-sm-4">
                <input type="text" class="form-input-flat exp-start" placeholder="Start Date" value="${data?.startDate || ''}">
            </div>
            <div class="col-sm-4">
                <input type="text" class="form-input-flat exp-end" placeholder="End Date" value="${data?.endDate || ''}">
            </div>
            <div class="col-sm-4 d-flex align-items-center">
                <div class="form-check">
                    <input class="form-check-input exp-current" type="checkbox" id="exp-current-${index}" ${data?.isCurrent ? 'checked' : ''}>
                    <label class="form-check-label small" for="exp-current-${index}">Currently Work Here</label>
                </div>
            </div>
            <div class="col-12">
                <input type="text" class="form-input-flat exp-tech" placeholder="Technologies Used (Comma-separated)" value="${data?.technologies || ''}">
            </div>
            <div class="col-12">
                <textarea class="form-input-flat exp-resp" rows="2" placeholder="Key Responsibilities...">${data?.responsibilities || ''}</textarea>
            </div>
            <div class="col-12">
                <textarea class="form-input-flat exp-ach" rows="2" placeholder="Key Achievements...">${data?.achievements || ''}</textarea>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addProjectItem(data = null) {
    const list = document.getElementById('projects-list');
    const index = projCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `proj-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Project #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('proj-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-6">
                <input type="text" class="form-input-flat proj-title" placeholder="Project Name" required value="${data?.title || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat proj-role" placeholder="Your Role (e.g. Lead Developer)" value="${data?.role || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat proj-gh" placeholder="GitHub Link" value="${data?.githubLink || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat proj-demo" placeholder="Live Demo Link" value="${data?.demoLink || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat proj-tech" placeholder="Technologies Used" value="${data?.technologies || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat proj-duration" placeholder="Duration" value="${data?.duration || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat proj-team" placeholder="Team Size" value="${data?.teamSize || ''}">
            </div>
            <div class="col-12">
                <textarea class="form-input-flat proj-features" rows="2" placeholder="Key Features...">${data?.features || ''}</textarea>
            </div>
            <div class="col-12">
                <textarea class="form-input-flat proj-desc" rows="2" placeholder="Project description...">${data?.description || ''}</textarea>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addSkillItem(data = null) {
    const list = document.getElementById('skills-list');
    const index = skillCount++;
    const item = document.createElement('div');
    item.className = 'col-sm-6';
    item.id = `skill-item-${index}`;
    item.innerHTML = `
        <div class="d-flex align-items-center gap-1.5 border rounded p-2 bg-white shadow-sm">
            <input type="text" class="form-input-flat skill-name" placeholder="Skill Name" required value="${data?.name || ''}">
            <input type="text" class="form-input-flat skill-cat" placeholder="Category" value="${data?.category || ''}">
            <select class="form-input-flat skill-level py-1.5" style="width: 130px;">
                <option value="" ${!data?.level ? 'selected' : ''}>Level</option>
                <option value="Beginner" ${data?.level === 'Beginner' ? 'selected' : ''}>Beginner</option>
                <option value="Intermediate" ${data?.level === 'Intermediate' ? 'selected' : ''}>Intermediate</option>
                <option value="Expert" ${data?.level === 'Expert' ? 'selected' : ''}>Expert</option>
            </select>
            <button type="button" class="btn-delete-item" onclick="removeItem('skill-item-${index}')">&times;</button>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addCertificationItem(data = null) {
    const list = document.getElementById('certifications-list');
    const index = certCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `cert-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark text-secondary">Certification #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('cert-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-6">
                <input type="text" class="form-input-flat cert-name" placeholder="Certificate Name" required value="${data?.name || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat cert-org" placeholder="Issuing Organization" required value="${data?.organization || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat cert-issue" placeholder="Issue Date" value="${data?.issueDate || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat cert-expiry" placeholder="Expiry Date" value="${data?.expiryDate || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat cert-id" placeholder="Credential ID" value="${data?.credentialId || ''}">
            </div>
            <div class="col-12">
                <input type="text" class="form-input-flat cert-url" placeholder="Credential URL" value="${data?.credentialUrl || ''}">
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addInternshipItem(data = null) {
    const list = document.getElementById('internships-list');
    const index = internCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `intern-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Internship #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('intern-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-6">
                <input type="text" class="form-input-flat int-comp" placeholder="Company Name" required value="${data?.company || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat int-pos" placeholder="Position" required value="${data?.position || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat int-dur" placeholder="Duration (e.g. 3 Months)" value="${data?.duration || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat int-tech" placeholder="Technologies Used" value="${data?.technologies || ''}">
            </div>
            <div class="col-12">
                <textarea class="form-input-flat int-desc" rows="2" placeholder="Description...">${data?.description || ''}</textarea>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addPublicationItem(data = null) {
    const list = document.getElementById('publications-list');
    const index = pubCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `pub-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Publication #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('pub-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-12">
                <input type="text" class="form-input-flat pub-title" placeholder="Publication Title" required value="${data?.title || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat pub-publisher" placeholder="Publisher / Journal" value="${data?.publisher || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat pub-doi" placeholder="DOI" value="${data?.doi || ''}">
            </div>
            <div class="col-sm-3">
                <input type="text" class="form-input-flat pub-link" placeholder="URL Link" value="${data?.link || ''}">
            </div>
            <div class="col-12">
                <textarea class="form-input-flat pub-desc" rows="2" placeholder="Brief summary of findings...">${data?.description || ''}</textarea>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addWorkshopItem(data = null) {
    const list = document.getElementById('workshops-list');
    const index = workshopCount++;
    const item = document.createElement('div');
    item.className = 'col-sm-6';
    item.id = `workshop-item-${index}`;
    item.innerHTML = `
        <div class="border rounded p-3 bg-white shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fw-bold text-dark">Workshop #${index + 1}</span>
                <button type="button" class="btn-delete-item" onclick="removeItem('workshop-item-${index}')">&times;</button>
            </div>
            <input type="text" class="form-input-flat work-name mb-2" placeholder="Workshop Name" required value="${data?.name || ''}">
            <input type="text" class="form-input-flat work-org mb-2" placeholder="Organization" value="${data?.organization || ''}">
            <input type="text" class="form-input-flat work-date" placeholder="Date" value="${data?.date || ''}">
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addAchievementItem(data = null) {
    const list = document.getElementById('achievements-list');
    const index = achCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `ach-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Achievement #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('ach-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-4">
                <select class="form-input-flat ach-cat py-1.5">
                    <option value="Award" ${data?.category === 'Award' ? 'selected' : ''}>Award/Scholarship</option>
                    <option value="Hackathon" ${data?.category === 'Hackathon' ? 'selected' : ''}>Hackathon Win</option>
                    <option value="Contest" ${data?.category === 'Contest' ? 'selected' : ''}>Coding Contest</option>
                    <option value="Academic" ${data?.category === 'Academic' ? 'selected' : ''}>Academic Feat</option>
                </select>
            </div>
            <div class="col-sm-8">
                <input type="text" class="form-input-flat ach-desc" placeholder="Details/Description" required value="${data?.description || ''}">
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addCodingProfileItem(data = null) {
    const list = document.getElementById('coding-list');
    const index = codingCount++;
    const item = document.createElement('div');
    item.className = 'col-sm-6';
    item.id = `coding-item-${index}`;
    item.innerHTML = `
        <div class="border rounded p-2.5 bg-white d-flex align-items-center shadow-sm">
            <select class="form-input-flat coding-platform py-1.5 me-2" style="width: 120px;">
                <option value="GitHub" ${data?.platform === 'GitHub' ? 'selected' : ''}>GitHub</option>
                <option value="LeetCode" ${data?.platform === 'LeetCode' ? 'selected' : ''}>LeetCode</option>
                <option value="HackerRank" ${data?.platform === 'HackerRank' ? 'selected' : ''}>HackerRank</option>
                <option value="CodeChef" ${data?.platform === 'CodeChef' ? 'selected' : ''}>CodeChef</option>
                <option value="Codeforces" ${data?.platform === 'Codeforces' ? 'selected' : ''}>Codeforces</option>
                <option value="GeeksforGeeks" ${data?.platform === 'GeeksforGeeks' ? 'selected' : ''}>GeeksforGeeks</option>
            </select>
            <input type="text" class="form-input-flat coding-url me-2" placeholder="Profile URL" required value="${data?.url || ''}">
            <input type="text" class="form-input-flat coding-rating me-2" placeholder="Rating" value="${data?.rating || ''}" style="width: 80px;">
            <button type="button" class="btn-delete-item" onclick="removeItem('coding-item-${index}')">&times;</button>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addLanguageItem(data = null) {
    const list = document.getElementById('languages-list');
    const index = langCount++;
    const item = document.createElement('div');
    item.className = 'col-sm-6';
    item.id = `lang-item-${index}`;
    item.innerHTML = `
        <div class="border rounded p-3 bg-white shadow-sm">
            <div class="d-flex align-items-center mb-2">
                <input type="text" class="form-input-flat lang-name me-2" placeholder="Language" required value="${data?.name || ''}">
                <select class="form-input-flat lang-level py-1.5 me-2" style="width:110px;">
                    <option value="Fluent" ${data?.level === 'Fluent' ? 'selected' : ''}>Fluent</option>
                    <option value="Native" ${data?.level === 'Native' ? 'selected' : ''}>Native</option>
                    <option value="Professional" ${data?.level === 'Professional' ? 'selected' : ''}>Professional</option>
                    <option value="Beginner" ${data?.level === 'Beginner' ? 'selected' : ''}>Beginner</option>
                </select>
                <button type="button" class="btn-delete-item" onclick="removeItem('lang-item-${index}')">&times;</button>
            </div>
            <div class="d-flex gap-3 small text-secondary">
                <div class="form-check">
                    <input class="form-check-input lang-read" type="checkbox" id="lang-read-${index}" ${data?.reading === 'yes' ? 'checked' : ''}>
                    <label class="form-check-label" for="lang-read-${index}">Read</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input lang-write" type="checkbox" id="lang-write-${index}" ${data?.writing === 'yes' ? 'checked' : ''}>
                    <label class="form-check-label" for="lang-write-${index}">Write</label>
                </div>
                <div class="form-check">
                    <input class="form-check-input lang-speak" type="checkbox" id="lang-speak-${index}" ${data?.speaking === 'yes' ? 'checked' : ''}>
                    <label class="form-check-label" for="lang-speak-${index}">Speak</label>
                </div>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addInterestItem(data = null) {
    const list = document.getElementById('interests-list');
    const index = interestCount++;
    const item = document.createElement('div');
    item.className = 'col-sm-4';
    item.id = `interest-item-${index}`;
    item.innerHTML = `
        <div class="border rounded p-2 bg-white d-flex align-items-center shadow-sm">
            <input type="text" class="form-input-flat int-name me-2" placeholder="Interest" required value="${data?.name || ''}">
            <button type="button" class="btn-delete-item" onclick="removeItem('interest-item-${index}')">&times;</button>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function addReferenceItem(data = null) {
    const list = document.getElementById('references-list');
    const index = refCount++;
    const item = document.createElement('div');
    item.className = 'border rounded p-3 bg-white shadow-sm';
    item.id = `ref-item-${index}`;
    item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="fw-bold text-dark">Reference #${index + 1}</span>
            <button type="button" class="btn-delete-item" onclick="removeItem('ref-item-${index}')">&times;</button>
        </div>
        <div class="row g-2">
            <div class="col-sm-6">
                <input type="text" class="form-input-flat ref-name" placeholder="Reference Name" required value="${data?.name || ''}">
            </div>
            <div class="col-sm-6">
                <input type="text" class="form-input-flat ref-rel" placeholder="Relationship" value="${data?.relationship || ''}">
            </div>
            <div class="col-sm-4">
                <input type="text" class="form-input-flat ref-comp" placeholder="Company" value="${data?.company || ''}">
            </div>
            <div class="col-sm-4">
                <input type="email" class="form-input-flat ref-email" placeholder="Email Address" value="${data?.email || ''}">
            </div>
            <div class="col-sm-4">
                <input type="text" class="form-input-flat ref-phone" placeholder="Phone Number" value="${data?.phone || ''}">
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

// SETUP SECTION VISIBILITY & ORDERING CONTROLS IN THE STYLE TAB
function setupSectionVisibilityList() {
    const container = document.getElementById('section-visibility-list');
    container.innerHTML = '';

    const labels = {
        summary: "Professional Summary",
        experience: "Work Experience",
        internships: "Internships",
        projects: "Key Projects",
        education: "Education history",
        certifications: "Certifications",
        publications: "Research Publications",
        workshops: "Workshops",
        coding_profiles: "Coding Profiles",
        languages: "Languages",
        achievements: "Achievements",
        references: "References",
        interests: "Interests",
        skills: "Technical Skills"
    };

    activeSectionsOrder.forEach(sec => {
        const div = document.createElement('div');
        div.className = 'list-group-item d-flex justify-content-between align-items-center p-2.5';
        div.innerHTML = `
            <div class="d-flex align-items-center">
                <input class="form-check-input sec-vis-check me-2.5" type="checkbox" value="${sec}" id="vis-${sec}" checked>
                <label class="form-check-label fw-semibold small text-dark" for="vis-${sec}">
                    ${labels[sec] || sec}
                </label>
            </div>
            <div class="d-flex gap-1">
                <button type="button" class="btn btn-light btn-sm border-0 py-0" onclick="moveSection('${sec}', -1)" title="Move Up">▲</button>
                <button type="button" class="btn btn-light btn-sm border-0 py-0" onclick="moveSection('${sec}', 1)" title="Move Down">▼</button>
            </div>
        `;
        container.appendChild(div);

        // Visibility checkbox event
        document.getElementById(`vis-${sec}`).addEventListener('change', () => {
            updatePreview();
            calculateAtsScore();
        });
    });
}

window.moveSection = function(section, dir) {
    const idx = activeSectionsOrder.indexOf(section);
    if (idx === -1) return;
    
    const targetIdx = idx + dir;
    if (targetIdx < 0 || targetIdx >= activeSectionsOrder.length) return;

    // Swap items
    const temp = activeSectionsOrder[idx];
    activeSectionsOrder[idx] = activeSectionsOrder[targetIdx];
    activeSectionsOrder[targetIdx] = temp;

    setupSectionVisibilityList();
    updatePreview();
};

function getVisibleSections() {
    const list = [];
    document.querySelectorAll('.sec-vis-check').forEach(chk => {
        if (chk.checked) {
            list.push(chk.value);
        }
    });
    return list;
}

// LIVE PREVIEW COMPILATION ENGINE
function updatePreview() {
    const preview = document.getElementById('resume-preview');
    if (!preview) return;
    
    // Customization styles
    const template = document.getElementById('template-select').value;
    const font = document.getElementById('font-select').value;
    const size = document.getElementById('font-size-select').value;
    const margin = document.getElementById('margin-select').value;
    const spacing = document.getElementById('spacing-select').value;
    const color = document.getElementById('color-picker').value;

    // Reset classes
    preview.className = `resume-paper template-${template} margin-${margin} size-${size} spacing-${spacing}`;
    preview.style.setProperty('--theme-primary', color);

    // Read general details
    const firstName = document.getElementById('firstName').value || 'John';
    const lastName = document.getElementById('lastName').value || 'Doe';
    const headline = document.getElementById('headline').value || '';
    const email = document.getElementById('email').value || 'john.doe@example.com';
    const phone = document.getElementById('phone').value || '';
    const address = document.getElementById('address').value || '';
    const city = document.getElementById('city').value || '';
    const country = document.getElementById('country').value || '';
    
    const linkedin = document.getElementById('linkedin').value || '';
    const github = document.getElementById('github').value || '';
    const portfolio = document.getElementById('portfolio').value || '';
    const website = document.getElementById('website').value || '';

    // Format Locations
    let locationStr = address;
    if (city) locationStr += (locationStr ? ', ' : '') + city;
    if (country) locationStr += (locationStr ? ', ' : '') + country;

    // Compile Header layout depending on template
    let headerHtml = '';
    let contactInfo = `${email}`;
    if (phone) contactInfo += `  |  ${phone}`;
    if (locationStr) contactInfo += `  |  ${locationStr}`;

    let socialLinks = '';
    if (linkedin) socialLinks += `LinkedIn: ${linkedin} &nbsp;&nbsp;`;
    if (github) socialLinks += `GitHub: ${github} &nbsp;&nbsp;`;
    if (portfolio) socialLinks += `Portfolio: ${portfolio} &nbsp;&nbsp;`;
    if (website) socialLinks += `Web: ${website}`;

    if (template === 'modern') {
        // Modern uses a custom dual-panel layout, handled inside preview render below
    } else if (template === 'bold-header') {
        headerHtml = `
            <div class="header-banner" style="background-color: var(--theme-primary, #2563eb); color: #ffffff; padding: 25px; text-align: center; margin: -15mm -15mm 15mm -15mm; border-bottom: 2px solid rgba(0,0,0,0.1);">
                <h1 style="color: #ffffff; font-weight:700; margin-bottom: 4px; font-size: 26px; letter-spacing: -0.5px;">${firstName} ${lastName}</h1>
                ${headline ? `<div style="font-weight: 500; font-size:1.1em; color:#f3f4f6; text-transform:uppercase; margin-bottom:6px;">${headline}</div>` : ''}
                <div style="font-size: 0.9em; color:#e5e7eb;">
                    <div>${contactInfo}</div>
                    ${socialLinks ? `<div class="mt-1" style="opacity: 0.9;">${socialLinks}</div>` : ''}
                </div>
            </div>
        `;
    } else {
        headerHtml = `
            <div style="text-align: center; margin-bottom: 15px;">
                <h1 style="color: var(--theme-primary, #2563eb); font-weight:700; margin-bottom: 4px;">${firstName} ${lastName}</h1>
                ${headline ? `<div style="font-weight: 500; font-size:1.1em; color:#4b5563; text-transform:uppercase; margin-bottom:6px;">${headline}</div>` : ''}
                <div style="font-size: 0.9em; color:#6b7280; border-bottom: 1.5px solid var(--theme-primary, #2563eb); padding-bottom:6px;">
                    <div>${contactInfo}</div>
                    ${socialLinks ? `<div class="mt-1">${socialLinks}</div>` : ''}
                </div>
            </div>
        `;
    }

    // Build lists in specified order
    let mainContentHtml = '';

    const visibleSections = getVisibleSections();

    // Map list elements HTML generator
    const sectionsGenerators = {
        summary: () => {
            const summary = document.getElementById('summary').value;
            if (!summary) return '';
            return `
                <div class="resume-section">
                    <h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Professional Summary</h2>
                    <p>${summary.replace(/\n/g, '<br>')}</p>
                </div>
            `;
        },
        experience: () => {
            const elements = document.querySelectorAll('#experience-list [id^="exp-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const comp = el.querySelector('.exp-comp').value;
                const pos = el.querySelector('.exp-pos').value;
                const start = el.querySelector('.exp-start').value;
                const end = el.querySelector('.exp-end').value || 'Present';
                const isCurrent = el.querySelector('.exp-current').checked;
                const tech = el.querySelector('.exp-tech').value;
                const resp = el.querySelector('.exp-resp').value;
                const ach = el.querySelector('.exp-ach').value;

                if (comp || pos) {
                    html += `
                        <div style="margin-bottom: 10px;">
                            <div style="display:flex; justify-content:space-between; font-weight:700;">
                                <span>${pos || 'Position'} @ ${comp || 'Company'}</span>
                                <span>${start} - ${isCurrent ? 'Present' : end}</span>
                            </div>
                            ${tech ? `<div style="font-style:italic; color:#6b7280; font-size:0.9em; margin-bottom:2px;">Technologies: ${tech}</div>` : ''}
                            ${resp ? `<p style="margin-bottom:2px;">${resp.replace(/\n/g, '<br>')}</p>` : ''}
                            ${ach ? `<p style="color:#6b7280; margin-bottom:0;">Key Achievements: ${ach.replace(/\n/g, '<br>')}</p>` : ''}
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Work Experience</h2>${html}</div>` : '';
        },
        internships: () => {
            const elements = document.querySelectorAll('#internships-list [id^="intern-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const comp = el.querySelector('.int-comp').value;
                const pos = el.querySelector('.int-pos').value;
                const dur = el.querySelector('.int-dur').value;
                const tech = el.querySelector('.int-tech').value;
                const desc = el.querySelector('.int-desc').value;

                if (comp || pos) {
                    html += `
                        <div style="margin-bottom: 10px;">
                            <div style="display:flex; justify-content:space-between; font-weight:700;">
                                <span>${pos} - ${comp}</span>
                                <span>${dur}</span>
                            </div>
                            ${tech ? `<div style="font-style:italic; color:#6b7280; font-size:0.9em; margin-bottom:2px;">Technologies: ${tech}</div>` : ''}
                            ${desc ? `<p>${desc}</p>` : ''}
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Internships</h2>${html}</div>` : '';
        },
        projects: () => {
            const elements = document.querySelectorAll('#projects-list [id^="proj-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const title = el.querySelector('.proj-title').value;
                const role = el.querySelector('.proj-role').value;
                const gh = el.querySelector('.proj-gh').value;
                const demo = el.querySelector('.proj-demo').value;
                const tech = el.querySelector('.proj-tech').value;
                const duration = el.querySelector('.proj-duration').value;
                const team = el.querySelector('.proj-team').value;
                const features = el.querySelector('.proj-features').value;
                const desc = el.querySelector('.proj-desc').value;

                if (title) {
                    let projMeta = duration;
                    if (team) projMeta += (projMeta ? ' | ' : '') + `Team Size: ${team}`;
                    html += `
                        <div style="margin-bottom: 10px;">
                            <div style="display:flex; justify-content:space-between; font-weight:700;">
                                <span>${title} ${role ? `(${role})` : ''}</span>
                                <span>${projMeta}</span>
                            </div>
                            <div style="font-size:0.95em; color:#4b5563;">
                                ${gh ? `GitHub: ${gh} &nbsp;` : ''} ${demo ? `| Demo: ${demo}` : ''}
                            </div>
                            ${tech ? `<div style="font-style:italic; color:#6b7280; font-size:0.9em; margin-bottom:2px;">Technologies: ${tech}</div>` : ''}
                            ${features ? `<p style="margin-bottom:2px;"><strong>Key Features:</strong> ${features.replace(/\n/g, '<br>')}</p>` : ''}
                            ${desc ? `<p style="margin-bottom:0;">${desc}</p>` : ''}
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Key Projects</h2>${html}</div>` : '';
        },
        education: () => {
            const elements = document.querySelectorAll('#education-list [id^="edu-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const inst = el.querySelector('.edu-inst').value;
                const deg = el.querySelector('.edu-degree').value;
                const field = el.querySelector('.edu-field').value;
                const univ = el.querySelector('.edu-univ').value;
                const start = el.querySelector('.edu-start').value;
                const end = el.querySelector('.edu-end').value;
                const cgpa = el.querySelector('.edu-cgpa').value;
                const pct = el.querySelector('.edu-percentage').value;
                const loc = el.querySelector('.edu-loc').value;
                const desc = el.querySelector('.edu-desc').value;

                if (inst || deg) {
                    let gradDetails = cgpa ? `CGPA: ${cgpa}` : '';
                    if (pct) gradDetails += (gradDetails ? ' | ' : '') + `Percentage: ${pct}`;
                    html += `
                        <div style="margin-bottom: 8px;">
                            <div style="display:flex; justify-content:space-between; font-weight:700;">
                                <span>${deg} ${field ? `in ${field}` : ''} @ ${inst} ${univ ? `(${univ})` : ''}</span>
                                <span>${start} - ${end}</span>
                            </div>
                            <div style="font-size:0.9em; color:#4b5563;">${loc ? `${loc} &nbsp;` : ''} ${gradDetails ? `| ${gradDetails}` : ''}</div>
                            ${desc ? `<p>${desc}</p>` : ''}
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Education</h2>${html}</div>` : '';
        },
        certifications: () => {
            const elements = document.querySelectorAll('#certifications-list [id^="cert-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const name = el.querySelector('.cert-name').value;
                const org = el.querySelector('.cert-org').value;
                const issue = el.querySelector('.cert-issue').value;
                const exp = el.querySelector('.cert-expiry').value;
                const cId = el.querySelector('.cert-id').value;
                const url = el.querySelector('.cert-url').value;

                if (name) {
                    html += `
                        <div style="margin-bottom: 6px;">
                            <div style="display:flex; justify-content:space-between; font-weight:700;">
                                <span>${name} - ${org}</span>
                                <span>${issue} ${exp ? `- ${exp}` : ''}</span>
                            </div>
                            <div style="font-size:0.9em; color:#4b5563;">${cId ? `ID: ${cId} &nbsp;` : ''} ${url ? `| Link: ${url}` : ''}</div>
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Certifications</h2>${html}</div>` : '';
        },
        publications: () => {
            const elements = document.querySelectorAll('#publications-list [id^="pub-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const title = el.querySelector('.pub-title').value;
                const pub = el.querySelector('.pub-publisher').value;
                const doi = el.querySelector('.pub-doi').value;
                const link = el.querySelector('.pub-link').value;
                const desc = el.querySelector('.pub-desc').value;

                if (title) {
                    html += `
                        <div style="margin-bottom: 8px;">
                            <div style="display:flex; justify-content:space-between; font-weight:700;">
                                <span>${title}</span>
                                <span>${pub}</span>
                            </div>
                            <div style="font-size:0.9em; color:#4b5563;">${doi ? `DOI: ${doi} &nbsp;` : ''} ${link ? `| Link: ${link}` : ''}</div>
                            ${desc ? `<p>${desc}</p>` : ''}
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Research Publications</h2>${html}</div>` : '';
        },
        workshops: () => {
            const elements = document.querySelectorAll('#workshops-list [id^="workshop-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const name = el.querySelector('.work-name').value;
                const org = el.querySelector('.work-org').value;
                const date = el.querySelector('.work-date').value;
                if (name) {
                    html += `<li><strong>${name}</strong> (${org}) - ${date}</li>`;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Workshops</h2><ul>${html}</ul></div>` : '';
        },
        coding_profiles: () => {
            const elements = document.querySelectorAll('#coding-list [id^="coding-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const plat = el.querySelector('.coding-platform').value;
                const url = el.querySelector('.coding-url').value;
                const rat = el.querySelector('.coding-rating').value;
                if (url) {
                    html += `<li><strong>${plat}:</strong> ${url} ${rat ? `(Rating: ${rat})` : ''}</li>`;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Coding Profiles</h2><ul>${html}</ul></div>` : '';
        },
        languages: () => {
            const elements = document.querySelectorAll('#languages-list [id^="lang-item-"]');
            if (elements.length === 0) return '';
            let list = [];
            elements.forEach(el => {
                const name = el.querySelector('.lang-name').value;
                const level = el.querySelector('.lang-level').value;
                const read = el.querySelector('.lang-read').checked;
                const write = el.querySelector('.lang-write').checked;
                const speak = el.querySelector('.lang-speak').checked;

                if (name) {
                    let details = [];
                    if (level) details.push(level);
                    if (speak) details.push("Speaking");
                    if (read) details.push("Reading");
                    if (write) details.push("Writing");
                    
                    list.push(name + (details.length > 0 ? ` (${details.join(', ')})` : ''));
                }
            });
            return list.length > 0 ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Languages</h2><p>${list.join(' &nbsp;•&nbsp; ')}</p></div>` : '';
        },
        achievements: () => {
            const elements = document.querySelectorAll('#achievements-list [id^="ach-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const cat = el.querySelector('.ach-cat').value;
                const desc = el.querySelector('.ach-desc').value;
                if (desc) {
                    html += `<li><strong>[${cat}]</strong> ${desc}</li>`;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Achievements & Awards</h2><ul>${html}</ul></div>` : '';
        },
        references: () => {
            const elements = document.querySelectorAll('#references-list [id^="ref-item-"]');
            if (elements.length === 0) return '';
            let html = '';
            elements.forEach(el => {
                const name = el.querySelector('.ref-name').value;
                const rel = el.querySelector('.ref-rel').value;
                const comp = el.querySelector('.ref-comp').value;
                const email = el.querySelector('.ref-email').value;
                const phone = el.querySelector('.ref-phone').value;

                if (name) {
                    html += `
                        <div class="col-6 mb-2">
                            <strong>${name}</strong> ${rel ? `(${rel})` : ''}<br>
                            ${comp ? `${comp}<br>` : ''}
                            ${email ? `Email: ${email} &nbsp;` : ''} ${phone ? `| Phone: ${phone}` : ''}
                        </div>
                    `;
                }
            });
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">References</h2><div class="row">${html}</div></div>` : '';
        },
        interests: () => {
            const elements = document.querySelectorAll('#interests-list [id^="interest-item-"]');
            if (elements.length === 0) return '';
            let list = [];
            elements.forEach(el => {
                const name = el.querySelector('.int-name').value;
                if (name) list.push(name);
            });
            return list.length > 0 ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Interests</h2><p>${list.join(', ')}</p></div>` : '';
        },
        skills: () => {
            const elements = document.querySelectorAll('#skills-list [id^="skill-item-"]');
            if (elements.length === 0) return '';
            
            let grouped = {};
            elements.forEach(el => {
                const name = el.querySelector('.skill-name').value;
                const cat = el.querySelector('.skill-cat').value || 'General';
                const level = el.querySelector('.skill-level').value;

                if (name) {
                    if (!grouped[cat]) grouped[cat] = [];
                    grouped[cat].push(name + (level ? ` (${level})` : ''));
                }
            });

            let html = '';
            for (let cat in grouped) {
                html += `<p style="margin-bottom:2px;"><strong>${cat}:</strong> ${grouped[cat].join(', ')}</p>`;
            }
            return html ? `<div class="resume-section"><h2 style="color: var(--theme-primary, #2563eb); font-weight:600; text-transform:uppercase; border-bottom: 1px solid #e5e7eb; padding-bottom:3px; margin-top:12px; margin-bottom:6px;">Technical Skills</h2>${html}</div>` : '';
        }
    };

    if (template === 'modern' || template === 'professional') {
        let sidebarHtml = `
            <h2 style="font-size:22px; font-weight:700; color:var(--theme-primary, #2563eb); margin-bottom:2px;">${firstName} ${lastName}</h2>
            ${headline ? `<div style="font-weight: 500; font-size:0.95em; color:#4b5563; margin-bottom:12px;">${headline}</div>` : ''}
            
            <div style="font-size:0.85em; color:#4b5563; margin-bottom:16px; word-break:break-all;">
                <strong style="color:var(--theme-primary, #2563eb); text-transform:uppercase; font-size:10px; display:block; margin-bottom:4px;">Contact</strong>
                ${email}<br>
                ${phone ? `${phone}<br>` : ''}
                ${locationStr ? `${locationStr}<br>` : ''}
                ${linkedin ? `LI: ${linkedin}<br>` : ''}
                ${github ? `GH: ${github}<br>` : ''}
                ${portfolio ? `Portfolio: ${portfolio}<br>` : ''}
                ${website ? `Web: ${website}<br>` : ''}
            </div>
        `;

        if (visibleSections.includes('skills')) sidebarHtml += sectionsGenerators.skills();
        if (visibleSections.includes('languages')) sidebarHtml += sectionsGenerators.languages();
        if (visibleSections.includes('coding_profiles')) sidebarHtml += sectionsGenerators.coding_profiles();
        if (visibleSections.includes('interests')) sidebarHtml += sectionsGenerators.interests();

        let mainHtml = '';
        activeSectionsOrder.forEach(sec => {
            if (visibleSections.includes(sec) && ['skills', 'languages', 'coding_profiles', 'interests'].indexOf(sec) === -1) {
                mainHtml += sectionsGenerators[sec]();
            }
        });

        // Set Modern template layout: Left Sidebar background, right main
        preview.innerHTML = `
            <div style="display: flex; min-height: 297mm; padding: 0;">
                <div style="width: 32%; background-color:#f9fafb; padding:15px; border-right: 1px solid #e5e7eb; display:flex; flex-direction:column;">
                    ${sidebarHtml}
                </div>
                <div style="width: 68%; padding: 20px;">
                    ${mainHtml}
                </div>
            </div>
        `;
    } else {
        // Standard Layout Rendering
        activeSectionsOrder.forEach(sec => {
            if (visibleSections.includes(sec)) {
                mainContentHtml += sectionsGenerators[sec]();
            }
        });

        preview.innerHTML = headerHtml + mainContentHtml;
    }
}

// ATS SCORE AUTOMATED CALCULATION LOGIC
function calculateAtsScore() {
    let score = 0;
    const list = document.getElementById('ats-suggestions-list');
    if (!list) return;
    list.innerHTML = '';

    const addSuggestion = (text, status = 'Weak') => {
        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-center border-0 px-0 py-1.5';
        li.innerHTML = `${text} <span class="pill-badge pill-brand ${status === 'Good' ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'}">${status}</span>`;
        list.appendChild(li);
    };

    // Rule 1: Name and Headline (10 pts)
    const firstName = document.getElementById('firstName').value.trim();
    const lastName = document.getElementById('lastName').value.trim();
    const headline = document.getElementById('headline').value.trim();
    if (firstName && lastName) {
        score += 5;
    } else {
        addSuggestion("Provide both first and last name", "Missing");
    }
    if (headline) {
        score += 5;
    } else {
        addSuggestion("Add a professional title headline", "Weak");
    }

    // Rule 2: Email & Phone (10 pts)
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    if (email && email.includes('@')) {
        score += 5;
    } else {
        addSuggestion("Email address is missing or invalid", "Missing");
    }
    if (phone) {
        score += 5;
    } else {
        addSuggestion("Include a contact phone number", "Weak");
    }

    // Rule 3: Links (GitHub / LinkedIn) (10 pts)
    const li = document.getElementById('linkedin').value.trim();
    const gh = document.getElementById('github').value.trim();
    if (li || gh) {
        score += 10;
    } else {
        addSuggestion("Add a GitHub or LinkedIn profile link", "Weak");
    }

    // Rule 4: Summary length (10 pts)
    const summary = document.getElementById('summary').value.trim();
    if (summary.length > 80) {
        score += 10;
    } else if (summary.length > 0) {
        score += 5;
        addSuggestion("Summary is too short. Expand objectives.", "Weak");
    } else {
        addSuggestion("Include a professional summary block", "Missing");
    }

    // Rule 5: Work Experience (20 pts)
    const expCountVal = document.querySelectorAll('#experience-list [id^="exp-item-"]').length;
    if (expCountVal >= 2) {
        score += 20;
    } else if (expCountVal === 1) {
        score += 10;
        addSuggestion("Add at least 2 work history entries", "Weak");
    } else {
        addSuggestion("No work experience added", "Missing");
    }

    // Rule 6: Technical Skills categories (15 pts)
    const skillCountVal = document.querySelectorAll('#skills-list [id^="skill-item-"]').length;
    if (skillCountVal >= 5) {
        score += 15;
    } else if (skillCountVal > 0) {
        score += 8;
        addSuggestion("List at least 5 technical skills", "Weak");
    } else {
        addSuggestion("Add technical skills section", "Missing");
    }

    // Rule 7: Key Projects & Education (20 pts)
    const projCountVal = document.querySelectorAll('#projects-list [id^="proj-item-"]').length;
    const eduCountVal = document.querySelectorAll('#education-list [id^="edu-item-"]').length;
    if (projCountVal > 0 && eduCountVal > 0) {
        score += 20;
    } else {
        if (projCountVal === 0) addSuggestion("Add a project entry with repository link", "Weak");
        if (eduCountVal === 0) addSuggestion("Include your college education details", "Missing");
    }

    score = Math.min(score, 100);

    // Update ring widget UI
    const ring = document.getElementById('ats-score-ring');
    if (ring) {
        ring.setAttribute('data-score', score);
        ring.style.setProperty('--score', score);
    }

    const title = document.getElementById('ats-score-title');
    const desc = document.getElementById('ats-score-desc');
    if (title && desc) {
        if (score >= 85) {
            title.innerText = "Excellent ATS Format!";
            desc.innerText = "Your resume is fully optimized for HR scanner screening systems.";
        } else if (score >= 60) {
            title.innerText = "Average Formatting";
            desc.innerText = "Complete recommended sections to boost readability.";
        } else {
            title.innerText = "Poor Compliancy";
            desc.innerText = "Crucial sections are missing. Review suggested changes.";
        }
    }

    if (score === 100) {
        addSuggestion("All checks passed successfully!", "Good");
    }
}

// CHAR COUNTER FOR SUMMARY
function updateCharCounter() {
    const text = document.getElementById('summary').value;
    document.getElementById('summary-char-counter').innerText = `${text.length} chars`;
}

// SIMULATED AI WRITER SUGGESTIONS
function triggerAiSummary() {
    const headline = document.getElementById('headline').value.trim() || "Software Engineer";
    const summaries = {
        "Software Engineer": "Innovative and results-driven Software Engineer with 4+ years of experience designing, developing, and deploying scalable web services. Proven expertise in microservices architecture, clean coding practices, and full-stack integration. Committed to delivering clean, optimized solutions for complex problems.",
        "Product Manager": "Strategic Product Manager with a track record of driving product lifecycles from concept to launch. Skilled in user research, data analysis, and cross-functional team management to deliver user-centric products that align with corporate growth strategy.",
        "Data Scientist": "Analytical Data Scientist with background in machine learning models, statistical analysis, and data engineering. Passionate about translating complex data into actionable insights to solve challenging business operations.",
        "UX/UI Designer": "Creative UI/UX Designer with expertise in wireframing, interactive prototyping, and user-centered design principles. Dedicated to constructing beautiful, accessible interfaces that improve user engagement metrics."
    };

    const text = summaries[headline] || `Motivated ${headline} with strong foundation in project management, analytical thinking, and collaboration. Eager to bring technical competence and a growth mindset to a high-impact development role.`;
    document.getElementById('summary').value = text;
    updateCharCounter();
    updatePreview();
    calculateAtsScore();
}

function triggerAiOptimizeSummary() {
    let text = document.getElementById('summary').value;
    if (!text) {
        alert("Please write or generate a summary first.");
        return;
    }
    const keywords = " Leverages CI/CD pipelines, containerized Docker environments, cloud computing on AWS, and agile methodologies to enhance pipeline delivery speeds and reduce architectural bottlenecks.";
    if (!text.includes("CI/CD")) {
        document.getElementById('summary').value = text + keywords;
        updateCharCounter();
        updatePreview();
        calculateAtsScore();
    }
}

function triggerAiSuggestSkills() {
    const headline = document.getElementById('headline').value.toLowerCase();
    
    let skills = [
        { name: 'Git', level: 'Expert', category: 'Version Control' },
        { name: 'SQL', level: 'Expert', category: 'Databases' }
    ];

    if (headline.includes('software') || headline.includes('developer') || headline.includes('engineer')) {
        skills.push({ name: 'Java', level: 'Expert', category: 'Languages' });
        skills.push({ name: 'Python', level: 'Intermediate', category: 'Languages' });
        skills.push({ name: 'React', level: 'Expert', category: 'Frameworks' });
        skills.push({ name: 'Docker', level: 'Intermediate', category: 'Tools' });
    } else if (headline.includes('data') || headline.includes('scientist') || headline.includes('ml')) {
        skills.push({ name: 'Python', level: 'Expert', category: 'Languages' });
        skills.push({ name: 'TensorFlow', level: 'Intermediate', category: 'Machine Learning' });
        skills.push({ name: 'Pandas', level: 'Expert', category: 'Data Analysis' });
        skills.push({ name: 'Tableau', level: 'Intermediate', category: 'Tools' });
    } else {
        skills.push({ name: 'Agile', level: 'Expert', category: 'Methodology' });
        skills.push({ name: 'Jira', level: 'Expert', category: 'Tools' });
    }

    document.getElementById('skills-list').innerHTML = '';
    skillCount = 0;
    skills.forEach(s => addSkillItem(s));
}

// BACKUP & RESTORE IMPORT/EXPORT
function exportJsonBackup() {
    const payload = compilePayload();
    const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Resume_Backup_${payload.title.replace(/\s+/g, '_')}.json`;
    document.body.appendChild(a);
    a.click();
    a.remove();
}

async function exportHtmlResume() {
    let styles = '';
    try {
        const res = await fetch('/css/style.css');
        if (res.ok) {
            styles = await res.text();
        }
    } catch (e) {
        console.error("Could not fetch style.css dynamically", e);
    }

    const preview = document.getElementById('resume-preview').outerHTML;
    
    const htmlContent = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Resume Export</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Playfair+Display:ital,wght@0,600;1,600&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <style>
        body { background: #f9fafb; padding: 20px; font-family: 'Inter', sans-serif; }
        ${styles}
    </style>
</head>
<body>
    ${preview}
</body>
</html>
    `;
    const blob = new Blob([htmlContent], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `Resume_Web_Export.html`;
    document.body.appendChild(a);
    a.click();
    a.remove();
}

function importJsonBackup(e) {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function(evt) {
        try {
            const data = JSON.parse(evt.target.result);
            populateForm(data);
        } catch (err) {
            console.error(err);
            alert("Invalid backup file layout.");
        }
    };
    reader.readAsText(file);
}

// SAVE RESUME PAYLOAD
function compilePayload() {
    const title = document.getElementById('title').value.trim();
    const firstName = document.getElementById('firstName').value.trim();
    const lastName = document.getElementById('lastName').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const address = document.getElementById('address').value.trim();
    const summary = document.getElementById('summary').value.trim();

    const dob = document.getElementById('dob').value.trim();
    const city = document.getElementById('city').value.trim();
    const country = document.getElementById('country').value.trim();
    const headline = document.getElementById('headline').value.trim();
    
    const linkedin = document.getElementById('linkedin').value.trim();
    const github = document.getElementById('github').value.trim();
    const portfolio = document.getElementById('portfolio').value.trim();
    const website = document.getElementById('website').value.trim();

    // Style variables
    const template = document.getElementById('template-select').value;
    const fontFamily = document.getElementById('font-select').value;
    const fontSize = document.getElementById('font-size-select').value;
    const primaryColor = document.getElementById('color-picker').value;
    const lineSpacing = document.getElementById('spacing-select').value;
    const pageMargins = document.getElementById('margin-select').value;
    const pageSize = document.getElementById('page-size-select').value;

    const showSections = getVisibleSections().join(',');
    const sectionOrder = activeSectionsOrder.join(',');

    // Map Education lists (skipping empty)
    const education = [];
    document.querySelectorAll('#education-list [id^="edu-item-"]').forEach(el => {
        const institution = el.querySelector('.edu-inst').value.trim();
        const degree = el.querySelector('.edu-degree').value.trim();
        const fieldOfStudy = el.querySelector('.edu-field').value.trim();
        const university = el.querySelector('.edu-univ').value.trim();
        const startDate = el.querySelector('.edu-start').value.trim();
        const endDate = el.querySelector('.edu-end').value.trim();
        const cgpa = el.querySelector('.edu-cgpa').value.trim();
        const percentage = el.querySelector('.edu-percentage').value.trim();
        const location = el.querySelector('.edu-loc').value.trim();
        const description = el.querySelector('.edu-desc').value.trim();
        
        if (!institution && !degree && !fieldOfStudy && !university && !startDate && !endDate && !cgpa && !percentage && !location && !description) {
            return;
        }
        
        education.push({
            institution, degree, fieldOfStudy, university, startDate, endDate, cgpa, percentage, location, description
        });
    });

    // Map Experience lists (skipping empty)
    const experience = [];
    document.querySelectorAll('#experience-list [id^="exp-item-"]').forEach(el => {
        const company = el.querySelector('.exp-comp').value.trim();
        const position = el.querySelector('.exp-pos').value.trim();
        const employmentType = el.querySelector('.exp-type').value.trim();
        const location = el.querySelector('.exp-loc').value.trim();
        const startDate = el.querySelector('.exp-start').value.trim();
        const endDate = el.querySelector('.exp-end').value.trim();
        const isCurrent = el.querySelector('.exp-current').checked;
        const technologies = el.querySelector('.exp-tech').value.trim();
        const responsibilities = el.querySelector('.exp-resp').value.trim();
        const achievements = el.querySelector('.exp-ach').value.trim();
        
        if (!company && !position && !employmentType && !location && !startDate && !endDate && !technologies && !responsibilities && !achievements) {
            return;
        }

        experience.push({
            company, position, employmentType, location, startDate, endDate, isCurrent, technologies, responsibilities, achievements
        });
    });

    // Map Skills lists (skipping empty)
    const skills = [];
    document.querySelectorAll('#skills-list [id^="skill-item-"]').forEach(el => {
        const name = el.querySelector('.skill-name').value.trim();
        const level = el.querySelector('.skill-level').value.trim();
        const category = el.querySelector('.skill-cat').value.trim();
        
        if (!name) {
            return;
        }

        skills.push({ name, level, category });
    });

    // Map Projects lists (skipping empty)
    const projects = [];
    document.querySelectorAll('#projects-list [id^="proj-item-"]').forEach(el => {
        const title = el.querySelector('.proj-title').value.trim();
        const role = el.querySelector('.proj-role').value.trim();
        const githubLink = el.querySelector('.proj-gh').value.trim();
        const demoLink = el.querySelector('.proj-demo').value.trim();
        const technologies = el.querySelector('.proj-tech').value.trim();
        const duration = el.querySelector('.proj-duration').value.trim();
        const teamSize = el.querySelector('.proj-team').value.trim();
        const features = el.querySelector('.proj-features').value.trim();
        const description = el.querySelector('.proj-desc').value.trim();
        
        if (!title && !role && !githubLink && !demoLink && !technologies && !duration && !teamSize && !features && !description) {
            return;
        }

        projects.push({
            title, role, githubLink, demoLink, technologies, duration, teamSize, features, description
        });
    });

    // Certifications (skipping empty)
    const certifications = [];
    document.querySelectorAll('#certifications-list [id^="cert-item-"]').forEach(el => {
        const name = el.querySelector('.cert-name').value.trim();
        const organization = el.querySelector('.cert-org').value.trim();
        const issueDate = el.querySelector('.cert-issue').value.trim();
        const expiryDate = el.querySelector('.cert-expiry').value.trim();
        const credentialId = el.querySelector('.cert-id').value.trim();
        const credentialUrl = el.querySelector('.cert-url').value.trim();
        
        if (!name && !organization && !issueDate && !expiryDate && !credentialId && !credentialUrl) {
            return;
        }

        certifications.push({ name, organization, issueDate, expiryDate, credentialId, credentialUrl });
    });

    // Internships (skipping empty)
    const internships = [];
    document.querySelectorAll('#internships-list [id^="intern-item-"]').forEach(el => {
        const company = el.querySelector('.int-comp').value.trim();
        const position = el.querySelector('.int-pos').value.trim();
        const duration = el.querySelector('.int-dur').value.trim();
        const technologies = el.querySelector('.int-tech').value.trim();
        const description = el.querySelector('.int-desc').value.trim();
        
        if (!company && !position && !duration && !technologies && !description) {
            return;
        }

        internships.push({ company, position, duration, technologies, description });
    });

    // Publications (skipping empty)
    const publications = [];
    document.querySelectorAll('#publications-list [id^="pub-item-"]').forEach(el => {
        const title = el.querySelector('.pub-title').value.trim();
        const publisher = el.querySelector('.pub-publisher').value.trim();
        const doi = el.querySelector('.pub-doi').value.trim();
        const link = el.querySelector('.pub-link').value.trim();
        const description = el.querySelector('.pub-desc').value.trim();
        
        if (!title && !publisher && !doi && !link && !description) {
            return;
        }

        publications.push({ title, publisher, doi, link, description });
    });

    // Workshops (skipping empty)
    const workshops = [];
    document.querySelectorAll('#workshops-list [id^="workshop-item-"]').forEach(el => {
        const name = el.querySelector('.work-name').value.trim();
        const organization = el.querySelector('.work-org').value.trim();
        const date = el.querySelector('.work-date').value.trim();
        
        if (!name && !organization && !date) {
            return;
        }

        workshops.push({ name, organization, date });
    });

    // Achievements (skipping empty)
    const achievements = [];
    document.querySelectorAll('#achievements-list [id^="ach-item-"]').forEach(el => {
        const category = el.querySelector('.ach-cat').value.trim();
        const description = el.querySelector('.ach-desc').value.trim();
        
        if (!category && !description) {
            return;
        }

        achievements.push({ category, description });
    });

    // Coding Profiles (skipping empty)
    const codingProfiles = [];
    document.querySelectorAll('#coding-list [id^="coding-item-"]').forEach(el => {
        const platform = el.querySelector('.coding-platform').value.trim();
        const url = el.querySelector('.coding-url').value.trim();
        const rating = el.querySelector('.coding-rating').value.trim();
        
        if (!url) {
            return;
        }

        codingProfiles.push({ platform, url, rating });
    });

    // Languages (skipping empty)
    const languages = [];
    document.querySelectorAll('#languages-list [id^="lang-item-"]').forEach(el => {
        const name = el.querySelector('.lang-name').value.trim();
        const level = el.querySelector('.lang-level').value.trim();
        const reading = el.querySelector('.lang-read').checked ? 'yes' : 'no';
        const writing = el.querySelector('.lang-write').checked ? 'yes' : 'no';
        const speaking = el.querySelector('.lang-speak').checked ? 'yes' : 'no';
        
        if (!name) {
            return;
        }

        languages.push({ name, level, reading, writing, speaking });
    });

    // Interests (skipping empty)
    const interests = [];
    document.querySelectorAll('#interests-list [id^="interest-item-"]').forEach(el => {
        const name = el.querySelector('.int-name').value.trim();
        
        if (!name) {
            return;
        }

        interests.push({ name });
    });

    // References (skipping empty)
    const references = [];
    document.querySelectorAll('#references-list [id^="ref-item-"]').forEach(el => {
        const name = el.querySelector('.ref-name').value.trim();
        const relationship = el.querySelector('.ref-rel').value.trim();
        const company = el.querySelector('.ref-comp').value.trim();
        const email = el.querySelector('.ref-email').value.trim();
        const phone = el.querySelector('.ref-phone').value.trim();
        
        if (!name && !relationship && !company && !email && !phone) {
            return;
        }

        references.push({ name, relationship, company, email, phone });
    });

    return {
        title, firstName, lastName, email, phone, address, summary,
        dob, city, country, headline,
        linkedin, github, portfolio, website,
        template, fontFamily, fontSize, primaryColor, lineSpacing, pageMargins, pageSize,
        showSections, sectionOrder,
        education, experience, projects, skills, certifications, internships,
        publications, workshops, achievements, codingProfiles, languages, interests, references
    };
}

function validateForm(payload) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^\+?[0-9\s\-()]{7,20}$/;
    const urlRegex = /^(https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}(\/\S*)?$/;

    let errors = [];

    if (!payload.title) errors.push("Document Title is required.");
    if (!payload.firstName) errors.push("First Name is required.");
    if (!payload.lastName) errors.push("Last Name is required.");
    
    if (!payload.email) {
        errors.push("Email Address is required.");
    } else if (!emailRegex.test(payload.email)) {
        errors.push("Email Address format is invalid.");
    }

    if (payload.phone && !phoneRegex.test(payload.phone)) {
        errors.push("Phone Number format is invalid.");
    }

    if (payload.portfolio && !urlRegex.test(payload.portfolio)) {
        errors.push("Portfolio URL is invalid.");
    }
    if (payload.website && !urlRegex.test(payload.website)) {
        errors.push("Personal Website URL is invalid.");
    }

    // Validate visible dynamic list sections
    const visibleSecs = getVisibleSections();
    
    if (visibleSecs.includes('education') && payload.education) {
        payload.education.forEach((edu, i) => {
            if (!edu.institution.trim()) errors.push(`Education #${i + 1}: School/University is required.`);
            if (!edu.degree.trim()) errors.push(`Education #${i + 1}: Degree is required.`);
        });
    }

    if (visibleSecs.includes('experience') && payload.experience) {
        payload.experience.forEach((exp, i) => {
            if (!exp.company.trim()) errors.push(`Work Experience #${i + 1}: Company Name is required.`);
            if (!exp.position.trim()) errors.push(`Work Experience #${i + 1}: Position is required.`);
        });
    }

    if (visibleSecs.includes('projects') && payload.projects) {
        payload.projects.forEach((proj, i) => {
            if (!proj.title.trim()) errors.push(`Project #${i + 1}: Project Name is required.`);
        });
    }

    if (visibleSecs.includes('skills') && payload.skills) {
        payload.skills.forEach((skill, i) => {
            if (!skill.name.trim()) errors.push(`Skill #${i + 1}: Skill Name is required.`);
        });
    }

    if (visibleSecs.includes('languages') && payload.languages) {
        payload.languages.forEach((lang, i) => {
            if (!lang.name.trim()) errors.push(`Language #${i + 1}: Language Name is required.`);
        });
    }

    // Date year sequence check
    const parseDateHelper = (str) => {
        if (!str) return null;
        const match = str.match(/\b(19|20)\d{2}\b/);
        return match ? parseInt(match[0]) : null;
    };
    if (payload.education) {
        payload.education.forEach((edu, i) => {
            const startYear = parseDateHelper(edu.startDate);
            const endYear = parseDateHelper(edu.endDate);
            if (startYear && endYear && endYear < startYear) {
                errors.push(`Education #${i + 1}: End Date cannot be before Start Date.`);
            }
        });
    }
    if (payload.experience) {
        payload.experience.forEach((exp, i) => {
            const startYear = parseDateHelper(exp.startDate);
            const endYear = parseDateHelper(exp.endDate);
            if (startYear && endYear && endYear < startYear) {
                errors.push(`Work Experience #${i + 1}: End Date cannot be before Start Date.`);
            }
        });
    }

    return errors;
}

async function saveResume(isSilent = false) {
    const token = localStorage.getItem('token');
    const alertBox = document.getElementById('alert-box');
    const successBox = document.getElementById('success-box');
    const indicator = document.getElementById('autosave-indicator');
    const saveBtn = document.getElementById('save-btn');

    if (alertBox) alertBox.classList.add('d-none');
    if (successBox) successBox.classList.add('d-none');

    const payload = compilePayload();

    // Client-side Validation
    const errors = validateForm(payload);
    if (errors.length > 0) {
        if (!isSilent && alertBox) {
            alertBox.innerHTML = `<strong>Please correct the following errors:</strong><ul class="mb-0 mt-1">${errors.map(e => `<li>${e}</li>`).join('')}</ul>`;
            alertBox.classList.remove('d-none');
        }
        return;
    }

    if (indicator) {
        indicator.innerHTML = `
            <span class="spinner-border spinner-border-sm me-1" style="width:10px; height:10px;" role="status"></span>
            Saving...
        `;
    }
    if (saveBtn) saveBtn.disabled = true;

    const url = resumeId ? `/api/resumes/${resumeId}` : '/api/resumes';
    const method = resumeId ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const data = await response.json();
            
            if (indicator) {
                indicator.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="11" height="11" fill="currentColor" class="bi bi-cloud-check text-success me-1" viewBox="0 0 16 16"><path d="M10.394 1.24a3 3 0 0 0-3.002.04 4 4 0 1 0-5.65 5.65 3 3 0 0 0 .04 3.002A4 4 0 1 0 10.392 1.24zM8.5 12h-3v-1h3v1zm0-2h-3V9h3v1zm0-2h-3V7h3v1zm5-2h-2v10h2V6z"/></svg>
                    Saved Draft
                `;
            }

            if (!resumeId) {
                resumeId = data.id; // set global ID
                // Update URL parameter in place without page reload
                const newUrl = `${window.location.pathname}?id=${data.id}`;
                window.history.replaceState({ id: data.id }, '', newUrl);
                document.getElementById('pdf-btn').classList.remove('d-none');
            }

            if (!isSilent && successBox) {
                successBox.innerText = 'Resume draft saved successfully!';
                successBox.classList.remove('d-none');
            }
        } else if (response.status === 401 || response.status === 403) {
            localStorage.clear();
            window.location.href = '/login.html';
        } else {
            const errData = await response.json().catch(() => ({ message: 'Save validation failed.' }));
            if (indicator) indicator.innerText = 'Save Error';
            if (!isSilent && alertBox) {
                alertBox.innerText = errData.message || 'Save failed.';
                alertBox.classList.remove('d-none');
            }
        }
    } catch (err) {
        console.error(err);
        if (indicator) indicator.innerText = 'Offline';
    } finally {
        if (saveBtn) saveBtn.disabled = false;
    }
}

// RESTORE FORM FROM BACKUP OBJECT
function populateForm(resume) {
    document.getElementById('title').value = resume.title || 'Resume';
    document.getElementById('firstName').value = resume.firstName || '';
    document.getElementById('lastName').value = resume.lastName || '';
    document.getElementById('email').value = resume.email || '';
    document.getElementById('phone').value = resume.phone || '';
    document.getElementById('address').value = resume.address || '';
    document.getElementById('summary').value = resume.summary || '';

    document.getElementById('dob').value = resume.dob || '';
    document.getElementById('city').value = resume.city || '';
    document.getElementById('country').value = resume.country || '';
    document.getElementById('headline').value = resume.headline || '';
    
    document.getElementById('linkedin').value = resume.linkedin || '';
    document.getElementById('github').value = resume.github || '';
    document.getElementById('portfolio').value = resume.portfolio || '';
    document.getElementById('website').value = resume.website || '';

    // Style options
    const tempVal = resume.template || 'classic';
    document.getElementById('template-select').value = tempVal;
    const qSelect = document.getElementById('quick-template-select');
    if (qSelect) qSelect.value = tempVal;
    document.getElementById('font-select').value = resume.fontFamily || 'Inter';
    document.getElementById('font-size-select').value = resume.fontSize || 'medium';
    document.getElementById('color-picker').value = resume.primaryColor || '#2563eb';
    document.getElementById('color-text-input').value = resume.primaryColor || '#2563eb';
    document.getElementById('spacing-select').value = resume.lineSpacing || 'normal';
    document.getElementById('margin-select').value = resume.pageMargins || 'normal';
    document.getElementById('page-size-select').value = resume.pageSize || 'a4';

    // Clear dynamic blocks
    document.getElementById('education-list').innerHTML = '';
    document.getElementById('experience-list').innerHTML = '';
    document.getElementById('projects-list').innerHTML = '';
    document.getElementById('skills-list').innerHTML = '';
    document.getElementById('certifications-list').innerHTML = '';
    document.getElementById('internships-list').innerHTML = '';
    document.getElementById('publications-list').innerHTML = '';
    document.getElementById('workshops-list').innerHTML = '';
    document.getElementById('achievements-list').innerHTML = '';
    document.getElementById('coding-list').innerHTML = '';
    document.getElementById('languages-list').innerHTML = '';
    document.getElementById('interests-list').innerHTML = '';
    document.getElementById('references-list').innerHTML = '';

    // Rebuild lists
    if (resume.education) resume.education.forEach(edu => addEducationItem(edu));
    if (resume.experience) resume.experience.forEach(exp => addExperienceItem(exp));
    if (resume.projects) resume.projects.forEach(proj => addProjectItem(proj));
    if (resume.skills) resume.skills.forEach(skill => addSkillItem(skill));
    if (resume.certifications) resume.certifications.forEach(cert => addCertificationItem(cert));
    if (resume.internships) resume.internships.forEach(intern => addInternshipItem(intern));
    if (resume.publications) resume.publications.forEach(pub => addPublicationItem(pub));
    if (resume.workshops) resume.workshops.forEach(work => addWorkshopItem(work));
    if (resume.achievements) resume.achievements.forEach(ach => addAchievementItem(ach));
    if (resume.codingProfiles) resume.codingProfiles.forEach(cp => addCodingProfileItem(cp));
    
    const langs = resume.languages || resume.languagesList;
    if (langs) langs.forEach(lang => addLanguageItem(lang));
    
    if (resume.interests) resume.interests.forEach(interest => addInterestItem(interest));
    if (resume.references) resume.references.forEach(ref => addReferenceItem(ref));

    if (resume.sectionOrder) {
        activeSectionsOrder = resume.sectionOrder.split(',');
    }
    setupSectionVisibilityList();

    if (resume.showSections) {
        const list = resume.showSections.split(',');
        document.querySelectorAll('.sec-vis-check').forEach(chk => {
            chk.checked = list.includes(chk.value);
        });
    }

    updateCharCounter();
    updatePreview();
    calculateAtsScore();
}

async function loadResume(id) {
    const token = localStorage.getItem('token');
    const alertBox = document.getElementById('alert-box');

    try {
        const response = await fetch(`/api/resumes/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const data = await response.json();
            populateForm(data);
            document.getElementById('pdf-btn').classList.remove('d-none');
        } else if (response.status === 401 || response.status === 403) {
            localStorage.clear();
            window.location.href = '/login.html';
        } else {
            if (alertBox) {
                alertBox.innerText = 'Failed to load resume details.';
                alertBox.classList.remove('d-none');
            }
        }
    } catch (err) {
        console.error(err);
        if (alertBox) {
            alertBox.innerText = 'Server connection error.';
            alertBox.classList.remove('d-none');
        }
    }
}

// PDF DOWNLOAD
async function downloadPdf() {
    const token = localStorage.getItem('token');
    
    // Always save the latest changes before downloading the PDF
    await saveResume(true); 
    
    if (!resumeId) {
        alert('Please fill in the required fields (Document Name, First/Last Name, Email) and save before downloading.');
        return;
    }

    // Detect if content overflows 1 page (A4 height is 297mm)
    const testDiv = document.createElement('div');
    testDiv.style.height = '297mm';
    testDiv.style.visibility = 'hidden';
    testDiv.style.position = 'absolute';
    document.body.appendChild(testDiv);
    const a4HeightPx = testDiv.clientHeight;
    document.body.removeChild(testDiv);

    const previewEl = document.getElementById('resume-preview');
    if (previewEl && previewEl.scrollHeight > a4HeightPx + 5) {
        const confirmDownload = confirm("Your resume exceeds a single A4 page. Recruiter best practice is to keep the resume on 1 page.\n\nWould you like to customize styles (like reducing font size, page margins, or line spacing) to fit it on one page?\n\n- Click OK to download the multi-page PDF anyway.\n- Click Cancel to stay and adjust your layout settings.");
        if (!confirmDownload) {
            const styleTabLink = document.querySelector('.wizard-step-link[data-section="customize-sec"]');
            if (styleTabLink) {
                styleTabLink.click();
            }
            return;
        }
    }

    const pdfBtn = document.getElementById('pdf-btn');
    const originalText = pdfBtn ? pdfBtn.innerHTML : 'Download PDF';
    if (pdfBtn) {
        pdfBtn.disabled = true;
        pdfBtn.innerHTML = `<span class="spinner-border spinner-border-sm me-1" role="status"></span> Generating...`;
    }

    try {
        const response = await fetch(`/api/resumes/${resumeId}/pdf`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            const firstName = document.getElementById('firstName').value.trim() || 'Resume';
            const lastName = document.getElementById('lastName').value.trim() || '';
            const sanitizedFilename = `Resume_${firstName}_${lastName}`.replace(/[\s/\\?%*:|"<>]/g, '_') + '.pdf';
            
            a.href = url;
            a.download = sanitizedFilename;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert('Failed to generate PDF document.');
        }
    } catch (err) {
        console.error(err);
        alert('Connection error.');
    } finally {
        if (pdfBtn) {
            pdfBtn.disabled = false;
            pdfBtn.innerHTML = originalText;
        }
    }
}
