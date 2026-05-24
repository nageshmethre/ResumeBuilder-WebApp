document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const resumeId = urlParams.get('id');

    // Register button actions
    document.getElementById('add-edu-btn').addEventListener('click', () => addEducationItem());
    document.getElementById('add-exp-btn').addEventListener('click', () => addExperienceItem());
    document.getElementById('add-proj-btn').addEventListener('click', () => addProjectItem());
    document.getElementById('add-skill-btn').addEventListener('click', () => addSkillItem());

    document.getElementById('save-btn').addEventListener('click', () => saveResume());
    document.getElementById('pdf-btn').addEventListener('click', () => downloadPdf());

    // Listen to changes in the forms to trigger live preview
    const formContainer = document.querySelector('.form-editor');
    formContainer.addEventListener('input', () => updatePreview());

    // Initial load
    if (resumeId) {
        loadResume(resumeId);
    } else {
        // Render one empty item for each section to guide the user
        addEducationItem();
        addExperienceItem();
        addProjectItem();
        addSkillItem();
        updatePreview();
    }
});

// Dynamic lists indices
let eduCount = 0;
let expCount = 0;
let projCount = 0;
let skillCount = 0;

// Dynamic Education Block
function addEducationItem(data = null) {
    const list = document.getElementById('education-list');
    const index = eduCount++;
    const item = document.createElement('div');
    item.className = 'accordion-item mb-2 border rounded';
    item.id = `edu-item-${index}`;
    item.innerHTML = `
        <div class="p-3">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fw-semibold text-primary">Education #${index + 1}</span>
                <button type="button" class="btn btn-outline-danger btn-sm border-0" onclick="removeItem('edu-item-${index}')">Remove</button>
            </div>
            <div class="row g-2">
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm edu-inst" placeholder="School/University" required value="${data?.institution || ''}">
                </div>
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm edu-degree" placeholder="Degree (e.g. B.S.)" required value="${data?.degree || ''}">
                </div>
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm edu-field" placeholder="Field of Study" value="${data?.fieldOfStudy || ''}">
                </div>
                <div class="col-md-3">
                    <input type="text" class="form-control form-control-sm edu-start" placeholder="Start Date" value="${data?.startDate || ''}">
                </div>
                <div class="col-md-3">
                    <input type="text" class="form-control form-control-sm edu-end" placeholder="End Date" value="${data?.endDate || ''}">
                </div>
                <div class="col-12">
                    <textarea class="form-control form-control-sm edu-desc" rows="2" placeholder="Description/Achievements">${data?.description || ''}</textarea>
                </div>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

// Dynamic Experience Block
function addExperienceItem(data = null) {
    const list = document.getElementById('experience-list');
    const index = expCount++;
    const item = document.createElement('div');
    item.className = 'accordion-item mb-2 border rounded';
    item.id = `exp-item-${index}`;
    item.innerHTML = `
        <div class="p-3">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fw-semibold text-primary">Experience #${index + 1}</span>
                <button type="button" class="btn btn-outline-danger btn-sm border-0" onclick="removeItem('exp-item-${index}')">Remove</button>
            </div>
            <div class="row g-2">
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm exp-comp" placeholder="Company" required value="${data?.company || ''}">
                </div>
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm exp-pos" placeholder="Position" required value="${data?.position || ''}">
                </div>
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm exp-start" placeholder="Start Date (E.g., Jan 2024)" value="${data?.startDate || ''}">
                </div>
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm exp-end" placeholder="End Date (E.g., Present)" value="${data?.endDate || ''}">
                </div>
                <div class="col-12">
                    <textarea class="form-control form-control-sm exp-desc" rows="2" placeholder="Responsibilities & Achievements">${data?.description || ''}</textarea>
                </div>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

// Dynamic Project Block
function addProjectItem(data = null) {
    const list = document.getElementById('projects-list');
    const index = projCount++;
    const item = document.createElement('div');
    item.className = 'accordion-item mb-2 border rounded';
    item.id = `proj-item-${index}`;
    item.innerHTML = `
        <div class="p-3">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="fw-semibold text-primary">Project #${index + 1}</span>
                <button type="button" class="btn btn-outline-danger btn-sm border-0" onclick="removeItem('proj-item-${index}')">Remove</button>
            </div>
            <div class="row g-2">
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm proj-title" placeholder="Project Title" required value="${data?.title || ''}">
                </div>
                <div class="col-md-6">
                    <input type="text" class="form-control form-control-sm proj-link" placeholder="Project Link" value="${data?.link || ''}">
                </div>
                <div class="col-12">
                    <input type="text" class="form-control form-control-sm proj-tech" placeholder="Technologies Used" value="${data?.technologies || ''}">
                </div>
                <div class="col-12">
                    <textarea class="form-control form-control-sm proj-desc" rows="2" placeholder="Project description">${data?.description || ''}</textarea>
                </div>
            </div>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

// Dynamic Skill Block
function addSkillItem(data = null) {
    const list = document.getElementById('skills-list');
    const index = skillCount++;
    const item = document.createElement('div');
    item.className = 'col-md-6';
    item.id = `skill-item-${index}`;
    item.innerHTML = `
        <div class="d-flex g-1 border rounded p-2 align-items-center">
            <input type="text" class="form-control form-control-sm skill-name me-2" placeholder="Skill" required value="${data?.name || ''}">
            <select class="form-select form-select-sm skill-level me-2">
                <option value="" ${!data?.level ? 'selected' : ''}>Level</option>
                <option value="Beginner" ${data?.level === 'Beginner' ? 'selected' : ''}>Beginner</option>
                <option value="Intermediate" ${data?.level === 'Intermediate' ? 'selected' : ''}>Intermediate</option>
                <option value="Expert" ${data?.level === 'Expert' ? 'selected' : ''}>Expert</option>
            </select>
            <button type="button" class="btn btn-outline-danger btn-sm border-0" onclick="removeItem('skill-item-${index}')">X</button>
        </div>
    `;
    list.appendChild(item);
    updatePreview();
}

function removeItem(id) {
    const item = document.getElementById(id);
    if (item) {
        item.remove();
        updatePreview();
    }
}

// Live Preview compilation
function updatePreview() {
    const preview = document.getElementById('resume-preview');
    
    const firstName = document.getElementById('firstName').value || 'John';
    const lastName = document.getElementById('lastName').value || 'Doe';
    const email = document.getElementById('email').value || 'john.doe@example.com';
    const phone = document.getElementById('phone').value || '';
    const address = document.getElementById('address').value || '';
    const summary = document.getElementById('summary').value || '';

    // Contact line formatting
    let contactInfo = `${email}`;
    if (phone) contactInfo += ` &nbsp;|&nbsp; ${phone}`;
    if (address) contactInfo += ` &nbsp;|&nbsp; ${address}`;

    let html = `
        <h1 class="text-center fw-bold">${firstName} ${lastName}</h1>
        <div class="contact-line text-center mb-4">${contactInfo}</div>
    `;

    // Summary block
    if (summary) {
        html += `
            <h2>Professional Summary</h2>
            <p>${summary}</p>
        `;
    }

    // Experience Block
    const expElements = document.querySelectorAll('#experience-list [id^="exp-item-"]');
    if (expElements.length > 0) {
        let expHtml = '';
        expElements.forEach(el => {
            const comp = el.querySelector('.exp-comp').value;
            const pos = el.querySelector('.exp-pos').value;
            const start = el.querySelector('.exp-start').value;
            const end = el.querySelector('.exp-end').value || 'Present';
            const desc = el.querySelector('.exp-desc').value;

            if (comp || pos) {
                expHtml += `
                    <div class="mb-3">
                        <div class="d-flex justify-content-between align-items-baseline">
                            <span class="item-title">${pos || 'Position'} - ${comp || 'Company'}</span>
                            <span class="item-meta">${start} - ${end}</span>
                        </div>
                        ${desc ? `<p class="mt-1">${desc.replace(/\n/g, '<br>')}</p>` : ''}
                    </div>
                `;
            }
        });
        if (expHtml) {
            html += `<h2>Work Experience</h2>${expHtml}`;
        }
    }

    // Education Block
    const eduElements = document.querySelectorAll('#education-list [id^="edu-item-"]');
    if (eduElements.length > 0) {
        let eduHtml = '';
        eduElements.forEach(el => {
            const inst = el.querySelector('.edu-inst').value;
            const deg = el.querySelector('.edu-degree').value;
            const field = el.querySelector('.edu-field').value;
            const start = el.querySelector('.edu-start').value;
            const end = el.querySelector('.edu-end').value;
            const desc = el.querySelector('.edu-desc').value;

            if (inst || deg) {
                eduHtml += `
                    <div class="mb-3">
                        <div class="d-flex justify-content-between align-items-baseline">
                            <span class="item-title">${deg || 'Degree'}${field ? ' in ' + field : ''} @ ${inst || 'Institution'}</span>
                            <span class="item-meta">${start} - ${end}</span>
                        </div>
                        ${desc ? `<p class="mt-1">${desc.replace(/\n/g, '<br>')}</p>` : ''}
                    </div>
                `;
            }
        });
        if (eduHtml) {
            html += `<h2>Education</h2>${eduHtml}`;
        }
    }

    // Projects Block
    const projElements = document.querySelectorAll('#projects-list [id^="proj-item-"]');
    if (projElements.length > 0) {
        let projHtml = '';
        projElements.forEach(el => {
            const title = el.querySelector('.proj-title').value;
            const link = el.querySelector('.proj-link').value;
            const tech = el.querySelector('.proj-tech').value;
            const desc = el.querySelector('.proj-desc').value;

            if (title) {
                projHtml += `
                    <div class="mb-3">
                        <div class="d-flex justify-content-between align-items-baseline">
                            <span class="item-title">${title}</span>
                            ${link ? `<span class="item-meta">${link}</span>` : ''}
                        </div>
                        ${tech ? `<div class="item-meta">Technologies: ${tech}</div>` : ''}
                        ${desc ? `<p class="mt-1">${desc.replace(/\n/g, '<br>')}</p>` : ''}
                    </div>
                `;
            }
        });
        if (projHtml) {
            html += `<h2>Projects</h2>${projHtml}`;
        }
    }

    // Skills Block
    const skillElements = document.querySelectorAll('#skills-list [id^="skill-item-"]');
    if (skillElements.length > 0) {
        let skillsList = [];
        skillElements.forEach(el => {
            const name = el.querySelector('.skill-name').value;
            const level = el.querySelector('.skill-level').value;
            if (name) {
                skillsList.push(name + (level ? ` (${level})` : ''));
            }
        });
        if (skillsList.length > 0) {
            html += `
                <h2>Skills</h2>
                <p>${skillsList.join(', ')}</p>
            `;
        }
    }

    preview.innerHTML = html;
}

// Load Resume on Init
async function loadResume(id) {
    const token = localStorage.getItem('token');
    const alertBox = document.getElementById('alert-box');

    try {
        const response = await fetch(`/api/resumes/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const resume = await response.json();
            
            // Populating simple fields
            document.getElementById('title').value = resume.title;
            document.getElementById('firstName').value = resume.firstName;
            document.getElementById('lastName').value = resume.lastName;
            document.getElementById('email').value = resume.email;
            document.getElementById('phone').value = resume.phone || '';
            document.getElementById('address').value = resume.address || '';
            document.getElementById('summary').value = resume.summary || '';

            // Clean list inputs
            document.getElementById('education-list').innerHTML = '';
            document.getElementById('experience-list').innerHTML = '';
            document.getElementById('projects-list').innerHTML = '';
            document.getElementById('skills-list').innerHTML = '';

            // Populating list structures
            if (resume.education && resume.education.length > 0) {
                resume.education.forEach(edu => addEducationItem(edu));
            }
            if (resume.experience && resume.experience.length > 0) {
                resume.experience.forEach(exp => addExperienceItem(exp));
            }
            if (resume.projects && resume.projects.length > 0) {
                resume.projects.forEach(proj => addProjectItem(proj));
            }
            if (resume.skills && resume.skills.length > 0) {
                resume.skills.forEach(skill => addSkillItem(skill));
            }

            // Expose PDF Download button
            document.getElementById('pdf-btn').classList.remove('d-none');

            updatePreview();
        } else {
            alertBox.innerText = 'Failed to load resume details.';
            alertBox.classList.remove('d-none');
        }
    } catch (err) {
        console.error(err);
        alertBox.innerText = 'Server connection error.';
        alertBox.classList.remove('d-none');
    }
}

// Save Resume logic
async function saveResume() {
    const token = localStorage.getItem('token');
    const alertBox = document.getElementById('alert-box');
    const successBox = document.getElementById('success-box');
    const urlParams = new URLSearchParams(window.location.search);
    const resumeId = urlParams.get('id');

    alertBox.classList.add('d-none');
    successBox.classList.add('d-none');

    // Pull metadata & details
    const title = document.getElementById('title').value;
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const summary = document.getElementById('summary').value;

    if (!title || !firstName || !lastName || !email) {
        alertBox.innerText = 'Please fill out the required personal information fields (Title, First/Last Name, and Email).';
        alertBox.classList.remove('d-none');
        return;
    }

    // Pull list elements
    const education = [];
    document.querySelectorAll('#education-list [id^="edu-item-"]').forEach(el => {
        education.push({
            institution: el.querySelector('.edu-inst').value,
            degree: el.querySelector('.edu-degree').value,
            fieldOfStudy: el.querySelector('.edu-field').value,
            startDate: el.querySelector('.edu-start').value,
            endDate: el.querySelector('.edu-end').value,
            description: el.querySelector('.edu-desc').value
        });
    });

    const experience = [];
    document.querySelectorAll('#experience-list [id^="exp-item-"]').forEach(el => {
        experience.push({
            company: el.querySelector('.exp-comp').value,
            position: el.querySelector('.exp-pos').value,
            startDate: el.querySelector('.exp-start').value,
            endDate: el.querySelector('.exp-end').value,
            description: el.querySelector('.exp-desc').value
        });
    });

    const projects = [];
    document.querySelectorAll('#projects-list [id^="proj-item-"]').forEach(el => {
        projects.push({
            title: el.querySelector('.proj-title').value,
            link: el.querySelector('.proj-link').value,
            technologies: el.querySelector('.proj-tech').value,
            description: el.querySelector('.proj-desc').value
        });
    });

    const skills = [];
    document.querySelectorAll('#skills-list [id^="skill-item-"]').forEach(el => {
        skills.push({
            name: el.querySelector('.skill-name').value,
            level: el.querySelector('.skill-level').value
        });
    });

    const payload = {
        title, firstName, lastName, email, phone, address, summary,
        education, experience, projects, skills
    };

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
            successBox.innerText = 'Resume saved successfully!';
            successBox.classList.remove('d-none');
            
            if (!resumeId) {
                // If it was a new creation, redirect to workspace with new ID
                setTimeout(() => {
                    window.location.href = `/builder.html?id=${data.id}`;
                }, 1000);
            } else {
                updatePreview();
            }
        } else {
            const errData = await response.json().catch(() => ({ message: 'Validation / Save Failed' }));
            alertBox.innerText = errData.message || 'Failed to save changes.';
            if (errData.errors) {
                alertBox.innerText += ' ' + errData.errors.join(', ');
            }
            alertBox.classList.remove('d-none');
        }
    } catch (err) {
        console.error(err);
        alertBox.innerText = 'Server connection error.';
        alertBox.classList.remove('d-none');
    }
}

// Download PDF
async function downloadPdf() {
    const token = localStorage.getItem('token');
    const urlParams = new URLSearchParams(window.location.search);
    const resumeId = urlParams.get('id');

    if (!resumeId) {
        alert('Please save the resume before downloading.');
        return;
    }

    try {
        const response = await fetch(`/api/resumes/${resumeId}/pdf`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            const firstName = document.getElementById('firstName').value || 'Resume';
            const lastName = document.getElementById('lastName').value || '';
            a.href = url;
            a.download = `Resume_${firstName}_${lastName}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } else {
            alert('Failed to generate PDF download.');
        }
    } catch (err) {
        console.error(err);
        alert('Connection error while downloading PDF.');
    }
}
