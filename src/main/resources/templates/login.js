document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');

    fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    })
        .then(response => response.json())
        .then(data => {
            console.log(data)
            if (data.message) {
                errorMessage.textContent = data.message;
            } else {
                localStorage.setItem('jwt-token', data['jwt-token']); // Сохранение токена в localStorage
                window.location.href = 'user-info.html'; // Перенаправление на страницу с информацией о пользователе
            }
        })
        .catch(error => console.error('Ошибка:', error));
});

document.getElementById('togglePassword').addEventListener('click', function(e) {
    const password = document.getElementById('password');
    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);
});
