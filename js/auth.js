document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const alertBox = document.getElementById('error-box') || document.getElementById('alert-box');
    const successBox = document.getElementById('success-box');

    const showAlert = (message) => {
        if (alertBox) {
            alertBox.innerText = message;
            alertBox.classList.remove('d-none');
        }
    };

    const showSuccess = (message) => {
        if (successBox) {
            successBox.innerText = message;
            successBox.classList.remove('d-none');
        }
    };

    const clearAlerts = () => {
        if (alertBox) alertBox.classList.add('d-none');
        if (successBox) successBox.classList.add('d-none');
    };

    // Redirect to dashboard if token exists
    if (localStorage.getItem('token') && (window.location.pathname.endsWith('login.html') || window.location.pathname.endsWith('register.html'))) {
        window.location.href = '/dashboard.html';
    }

    // Login logic
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            clearAlerts();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('username', data.username);
                    localStorage.setItem('email', data.email);
                    localStorage.setItem('roles', JSON.stringify(data.roles));
                    window.location.href = '/dashboard.html';
                } else {
                    const errData = await response.json().catch(() => ({ message: 'Invalid credentials' }));
                    showAlert(errData.message || 'Invalid username or password');
                }
            } catch (err) {
                console.error(err);
                showAlert('Server connection error. Please try again.');
            }
        });
    }

    // Register logic
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            clearAlerts();

            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, email, password })
                });

                if (response.ok) {
                    showSuccess('Account created successfully! Redirecting to login page...');
                    setTimeout(() => {
                        window.location.href = '/login.html';
                    }, 2000);
                } else {
                    const errorText = await response.text();
                    showAlert(errorText || 'Registration failed. Try a different username/email.');
                }
            } catch (err) {
                console.error(err);
                showAlert('Server connection error. Please try again.');
            }
        });
    }
});
