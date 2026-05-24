document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    // Set greeting
    const greeting = document.getElementById('user-greeting');
    if (greeting && username) {
        greeting.innerText = `Welcome, ${username}`;
    }

    // Logout logic
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.clear();
            window.location.href = '/index.html';
        });
    }

    fetchResumes();
});

async function fetchResumes() {
    const token = localStorage.getItem('token');
    const grid = document.getElementById('resumes-grid');
    const alertBox = document.getElementById('alert-box');

    try {
        const response = await fetch('/api/resumes', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const resumes = await response.json();
            renderResumes(resumes);
        } else {
            alertBox.innerText = 'Failed to load resumes. Session may have expired.';
            alertBox.classList.remove('d-none');
            grid.innerHTML = '';
        }
    } catch (err) {
        console.error(err);
        alertBox.innerText = 'Unable to connect to the server.';
        alertBox.classList.remove('d-none');
        grid.innerHTML = '';
    }
}

function renderResumes(resumes) {
    const grid = document.getElementById('resumes-grid');
    grid.innerHTML = '';

    if (resumes.length === 0) {
        grid.innerHTML = `
            <div class="col-12 text-center py-5">
                <p class="text-muted fs-5 mb-3">You haven't created any resumes yet.</p>
                <a href="/builder.html" class="btn btn-primary btn-md">Create Your First Resume</a>
            </div>
        `;
        return;
    }

    resumes.forEach(resume => {
        const col = document.createElement('div');
        col.className = 'col-md-4';
        col.innerHTML = `
            <div class="card h-100 border-0 shadow-sm">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title fw-bold text-truncate">${resume.title}</h5>
                    <h6 class="card-subtitle mb-2 text-muted">${resume.firstName} ${resume.lastName}</h6>
                    <p class="card-text text-truncate-3 text-muted fs-7 mb-4">${resume.summary || 'No summary provided.'}</p>
                    <div class="mt-auto d-flex justify-content-between">
                        <a href="/builder.html?id=${resume.id}" class="btn btn-outline-primary btn-sm px-3 fw-semibold">Edit</a>
                        <button onclick="deleteResume(${resume.id})" class="btn btn-outline-danger btn-sm px-3 fw-semibold">Delete</button>
                    </div>
                </div>
            </div>
        `;
        grid.appendChild(col);
    });
}

async function deleteResume(id) {
    if (!confirm('Are you sure you want to delete this resume?')) return;

    const token = localStorage.getItem('token');
    try {
        const response = await fetch(`/api/resumes/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            fetchResumes();
        } else {
            alert('Failed to delete resume.');
        }
    } catch (err) {
        console.error(err);
        alert('Server connection error.');
    }
}
