document.getElementById('registrationForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const yearOfBirth = document.getElementById('yearOfBirth').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');

    fetch('http://localhost:8080/auth/registration', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, yearOfBirth: parseInt(yearOfBirth), password }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.message) {
                errorMessage.textContent = data.message;
            } else {
                console.log('JWT Token:', data['jwt-token']);
                window.location.href = 'login.html'; // Перенаправление на страницу авторизации
            }
        })
        .catch(error => console.error('Ошибка:', error));
});
