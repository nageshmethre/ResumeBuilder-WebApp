document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username') || 'User';
    
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    // Set Greeting & User Initials Avatar
    document.getElementById('user-greeting').innerText = `Welcome, ${username}`;
    const initials = username.charAt(0).toUpperCase();
    document.getElementById('user-avatar').innerText = initials;

    // Collapsible Sidebar Navigation Toggle
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('sidebar-toggle-btn');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            const collapsed = sidebar.classList.contains('collapsed');
            toggleBtn.innerHTML = collapsed ? '»' : 'Collapse Menu';
        });
    }

    // Logout Action
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '/';
    });

    let resumesList = []; // Global in-memory cache
    let selectedResumeIdForRename = null;
    const renameModal = new bootstrap.Modal(document.getElementById('renameModal'));

    // Fetch and display resumes
    loadDashboardData();

    // Event listeners for search & filter
    document.getElementById('search-input').addEventListener('input', applySearchFilters);
    document.getElementById('template-filter').addEventListener('change', applySearchFilters);

    // Save rename action
    document.getElementById('save-rename-btn').addEventListener('click', saveRename);

    async function loadDashboardData() {
        const alertBox = document.getElementById('alert-box');

        try {
            const response = await fetch('/api/resumes', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                resumesList = await response.json();
                renderResumes(resumesList);
                updateStats(resumesList);
            } else if (response.status === 401 || response.status === 403) {
                localStorage.clear();
                window.location.href = '/login.html';
            } else {
                alertBox.innerText = 'Failed to load resumes list.';
                alertBox.classList.remove('d-none');
            }
        } catch (err) {
            console.error(err);
            alertBox.innerText = 'Server communication error.';
            alertBox.classList.remove('d-none');
        }
    }

    function renderResumes(list) {
        const tbody = document.getElementById('resumes-table-body');
        tbody.innerHTML = '';

        if (list.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center py-5 text-secondary">
                        <div class="mb-3">No resumes found.</div>
                        <a href="/builder.html" class="btn-flat btn-flat-primary btn-sm px-4">Create Your First Resume</a>
                    </td>
                </tr>
            `;
            return;
        }

        list.forEach(resume => {
            const temp = resume.template || 'classic';
            let templateName = 'Classic Minimalist';

            if (temp === 'modern') {
                templateName = 'Modern Sidebar';
            } else if (temp === 'professional') {
                templateName = 'Professional';
            } else if (temp === 'creative') {
                templateName = 'Creative Splash';
            } else if (temp === 'executive') {
                templateName = 'Executive Serif';
            } else if (temp === 'minimal') {
                templateName = 'Minimal Monospace';
            } else if (temp === 'compact') {
                templateName = 'Compact & Dense';
            } else if (temp === 'left-border') {
                templateName = 'Left Accent Bar';
            } else if (temp === 'bold-header') {
                templateName = 'Bold Color Banner';
            } else if (temp === 'academic') {
                templateName = 'Extended Academic';
            } else if (temp === 'elegant') {
                templateName = 'Centered Serif';
            } else if (temp === 'developer') {
                templateName = 'Developer Monospace';
            }

            // Simple date extraction from database
            let dateStr = 'Just now';
            if (resume.id && resume.title) {
                dateStr = 'Active Draft';
            }

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <a class="text-dark fw-semibold text-decoration-none" href="/builder.html?id=${resume.id}">${resume.title}</a>
                </td>
                <td class="text-secondary small">${resume.headline || 'Not specified'}</td>
                <td>
                    <span class="pill-badge pill-brand">${templateName}</span>
                </td>
                <td class="text-secondary small">${dateStr}</td>
                <td style="text-align:right;">
                    <div class="d-inline-flex gap-2">
                        <button class="btn btn-outline-secondary btn-sm py-1 px-2 btn-pdf-download" data-id="${resume.id}" title="Download PDF">
                            PDF
                        </button>
                        <div class="dropdown">
                            <button class="btn btn-outline-secondary btn-sm py-1 px-2" type="button" data-bs-toggle="dropdown">
                                •••
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow-sm border" style="font-size:13px;">
                                <li><a class="dropdown-item btn-duplicate" href="#" data-id="${resume.id}">Duplicate</a></li>
                                <li><a class="dropdown-item btn-rename" href="#" data-id="${resume.id}" data-title="${resume.title}">Rename</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger btn-delete" href="#" data-id="${resume.id}">Delete</a></li>
                            </ul>
                        </div>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

        // Add Event Listeners dynamically
        document.querySelectorAll('.btn-pdf-download').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                downloadPdf(btn.getAttribute('data-id'));
            });
        });
        document.querySelectorAll('.btn-duplicate').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                duplicateResume(btn.getAttribute('data-id'));
            });
        });
        document.querySelectorAll('.btn-rename').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                selectedResumeIdForRename = btn.getAttribute('data-id');
                document.getElementById('new-title-input').value = btn.getAttribute('data-title');
                renameModal.show();
            });
        });
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                if (confirm('Are you sure you want to delete this resume?')) {
                    deleteResume(btn.getAttribute('data-id'));
                }
            });
        });
    }

    function applySearchFilters() {
        const query = document.getElementById('search-input').value.toLowerCase().trim();
        const selectedTemplate = document.getElementById('template-filter').value;

        const filtered = resumesList.filter(resume => {
            const matchesSearch = !query || 
                resume.title.toLowerCase().includes(query) ||
                (resume.firstName && resume.firstName.toLowerCase().includes(query)) ||
                (resume.lastName && resume.lastName.toLowerCase().includes(query)) ||
                (resume.summary && resume.summary.toLowerCase().includes(query)) ||
                (resume.skills && resume.skills.some(s => s.name.toLowerCase().includes(query)));

            const matchesTemplate = !selectedTemplate || resume.template === selectedTemplate;

            return matchesSearch && matchesTemplate;
        });

        renderResumes(filtered);
    }

    function updateStats(list) {
        document.getElementById('stat-total').innerText = list.length;
        
        const templatesUsed = new Set(list.map(r => r.template || 'classic'));
        document.getElementById('stat-templates').innerText = templatesUsed.size;
    }

    async function downloadPdf(id) {
        try {
            const response = await fetch(`/api/resumes/${id}/pdf`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `Resume_${id}.pdf`;
                document.body.appendChild(a);
                a.click();
                a.remove();
            } else {
                alert('Error generating PDF.');
            }
        } catch (err) {
            console.error(err);
            alert('Connection error.');
        }
    }

    async function deleteResume(id) {
        try {
            const response = await fetch(`/api/resumes/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                loadDashboardData();
            } else {
                alert('Failed to delete resume.');
            }
        } catch (err) {
            console.error(err);
            alert('Connection error.');
        }
    }

    async function duplicateResume(id) {
        try {
            const response = await fetch(`/api/resumes/${id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const original = await response.json();
                const clone = { ...original };
                delete clone.id;
                clone.title = `Copy of ${clone.title}`;
                
                // Clear child entity IDs to prevent database mapping collisions
                if (clone.education) clone.education.forEach(e => delete e.id);
                if (clone.experience) clone.experience.forEach(e => delete e.id);
                if (clone.projects) clone.projects.forEach(e => delete e.id);
                if (clone.skills) clone.skills.forEach(e => delete e.id);
                if (clone.certifications) clone.certifications.forEach(c => delete c.id);
                if (clone.internships) clone.internships.forEach(i => delete i.id);
                if (clone.publications) clone.publications.forEach(p => delete p.id);
                if (clone.workshops) clone.workshops.forEach(w => delete w.id);
                if (clone.achievements) clone.achievements.forEach(a => delete a.id);
                if (clone.codingProfiles) clone.codingProfiles.forEach(c => delete c.id);
                if (clone.languages) clone.languages.forEach(l => delete l.id);
                if (clone.interests) clone.interests.forEach(i => delete i.id);
                if (clone.references) clone.references.forEach(r => delete r.id);

                const saveResponse = await fetch('/api/resumes', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(clone)
                });

                if (saveResponse.ok) {
                    loadDashboardData();
                } else {
                    alert('Error writing duplicate resume.');
                }
            } else {
                alert('Error loading original resume.');
            }
        } catch (err) {
            console.error(err);
            alert('Connection error.');
        }
    }

    async function saveRename() {
        const newTitle = document.getElementById('new-title-input').value.trim();
        if (!newTitle) return;

        try {
            const response = await fetch(`/api/resumes/${selectedResumeIdForRename}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const resume = await response.json();
                resume.title = newTitle;

                const putResponse = await fetch(`/api/resumes/${selectedResumeIdForRename}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(resume)
                });

                if (putResponse.ok) {
                    renameModal.hide();
                    loadDashboardData();
                } else {
                    alert('Failed to rename.');
                }
            } else {
                alert('Error fetching resume.');
            }
        } catch (err) {
            console.error(err);
            alert('Connection error.');
        }
    }
});
