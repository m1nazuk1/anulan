document.addEventListener('DOMContentLoaded', function() {
    const userInfo = document.getElementById('userInfo');
    const token = localStorage.getItem('jwt-token');
    if (token) {
        console.log('JWT Token:', token);
    } else {
        console.log('Токен не найден.');
    }

    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    console.log('Sending token:', token);
    fetch(`http://localhost:8080/showUserInfo`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Сетевой запрос на получение информации о пользователе не удался');
            }
            return response.json();
        })
        .then(data => {
            userInfo.innerHTML = `${data.firstname} ${data.lastname} <br>
    Год рождения: ${data.yearOfBirth} <br> О себе: ${data.description}`;

        })
        .catch(error => {
            console.error('Ошибка:', error);
            userInfo.textContent = 'Произошла ошибка при получении информации о пользователе.';
        });

    const logoutButton = document.getElementById('logoutButton');
    logoutButton.addEventListener('click', function() {
        localStorage.removeItem('jwt-token');
        window.location.href = 'login.html';
    });

    document.getElementById('editUserButton').addEventListener('click', function() {
        window.location.href = 'editUser.html';
    });

});
