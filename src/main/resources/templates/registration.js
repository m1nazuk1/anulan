document.getElementById('registrationForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const username = document.getElementById('username').value;
    const yearOfBirth = document.getElementById('yearOfBirth').value;
    const password = document.getElementById('password').value;
    const firstname = document.getElementById('firstname').value;
    const lastname = document.getElementById('lastname').value;
    const description = document.getElementById('description').value;
    const errorMessage = document.getElementById('errorMessage');

    fetch(`http://localhost:8080/auth/registration`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, yearOfBirth: parseInt(yearOfBirth), password, firstname, lastname,
            description
        }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.message) {
                errorMessage.textContent = data.message;
            } else {
                console.log('JWT Token:', data['jwt-token']);
                window.location.href = 'login.html';
            }
        })
        .catch(error => console.error('Ошибка:', error));
});