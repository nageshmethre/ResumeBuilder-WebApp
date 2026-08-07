document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username') || 'Admin';
    const roles = JSON.parse(localStorage.getItem('roles') || '[]');

    // Enforce Admin credentials check
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    if (!roles.includes('ROLE_ADMIN')) {
        window.location.href = '/dashboard.html';
        return;
    }

    // Set Greeting & User Initials Avatar
    document.getElementById('user-greeting').innerText = `Welcome, ${username}`;
    document.getElementById('user-avatar').innerText = username.charAt(0).toUpperCase();

    // Menu Collapse Toggle Action
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('sidebar-toggle-btn');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            const collapsed = sidebar.classList.contains('collapsed');
            toggleBtn.innerHTML = collapsed ? '»' : 'Collapse Menu';
        });
    }

    // Sign Out Action
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '/';
    });

    // Load initial Admin dashboard content
    loadAdminStats();
    loadUsersList();

    async function loadAdminStats() {
        try {
            const res = await fetch('/api/admin/stats', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                const stats = await res.json();
                document.getElementById('stat-users').innerText = stats.totalUsers;
                document.getElementById('stat-resumes').innerText = stats.totalResumes;
            }
        } catch (err) {
            console.error('Failed to load stats:', err);
        }
    }

    async function loadUsersList() {
        const tbody = document.getElementById('users-table-body');
        const alertBox = document.getElementById('alert-box');
        
        try {
            const res = await fetch('/api/admin/users', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            
            if (res.ok) {
                const users = await res.json();
                tbody.innerHTML = '';
                
                if (users.length === 0) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-5 text-secondary">
                                No registered users found.
                            </td>
                        </tr>
                    `;
                    return;
                }

                users.forEach(user => {
                    const tr = document.createElement('tr');
                    
                    // Render badges for roles
                    const roleBadges = user.roles.map(r => {
                        const name = r.replace('ROLE_', '');
                        const badgeClass = name === 'ADMIN' ? 'bg-danger text-white' : 'bg-secondary text-white';
                        return `<span class="badge ${badgeClass} me-1">${name}</span>`;
                    }).join('');

                    const isMainAdmin = user.username.toLowerCase() === 'admin';
                    const deleteBtn = isMainAdmin 
                        ? `<button class="btn btn-outline-danger btn-sm border-0" disabled title="Cannot delete main admin">System Locked</button>`
                        : `<button class="btn btn-danger btn-sm py-1.5 px-3 btn-delete-user" data-id="${user.id}" data-name="${user.username}">Delete User</button>`;

                    tr.innerHTML = `
                        <td><span class="fw-semibold text-secondary">#${user.id}</span></td>
                        <td><span class="fw-bold text-dark">${user.username}</span></td>
                        <td class="text-secondary small">${user.email}</td>
                        <td>${roleBadges}</td>
                        <td><span class="pill-badge pill-brand px-3 py-1 fw-bold fs-7">${user.resumeCount} Resumes</span></td>
                        <td style="text-align:right; padding-right: 24px;">
                            ${deleteBtn}
                        </td>
                    `;
                    tbody.appendChild(tr);
                });

                // Attach delete button listeners
                document.querySelectorAll('.btn-delete-user').forEach(btn => {
                    btn.addEventListener('click', (e) => {
                        e.preventDefault();
                        const userId = btn.getAttribute('data-id');
                        const username = btn.getAttribute('data-name');
                        if (confirm(`Are you sure you want to delete user "${username}" (ID: ${userId})? This will delete all of their saved resumes.`)) {
                            deleteUser(userId);
                        }
                    });
                });

            } else {
                alertBox.innerText = 'Failed to load user registry.';
                alertBox.classList.remove('d-none');
            }
        } catch (err) {
            console.error('Error loading users:', err);
            alertBox.innerText = 'Network error loading user database.';
            alertBox.classList.remove('d-none');
        }
    }

    async function deleteUser(id) {
        const alertBox = document.getElementById('alert-box');
        const successBox = document.getElementById('success-box');
        
        alertBox.classList.add('d-none');
        successBox.classList.add('d-none');

        try {
            const res = await fetch(`/api/admin/users/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (res.ok) {
                successBox.innerText = 'User and all associated resumes deleted successfully.';
                successBox.classList.remove('d-none');
                setTimeout(() => successBox.classList.add('d-none'), 4000);
                loadAdminStats();
                loadUsersList();
            } else {
                const data = await res.json().catch(() => ({ message: 'Failed to delete user' }));
                alertBox.innerText = data.message || 'Error occurred while deleting user.';
                alertBox.classList.remove('d-none');
            }
        } catch (err) {
            console.error(err);
            alertBox.innerText = 'Connection failure while deleting user.';
            alertBox.classList.remove('d-none');
        }
    }
});
