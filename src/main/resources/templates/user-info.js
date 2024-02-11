document.addEventListener('DOMContentLoaded', function() {
    const userInfoElement = document.getElementById('userInfo');
    const token = localStorage.getItem('jwt-token');

    if (!token) {
        window.location.href = 'login.html'; // Если токен не найден, перенаправляем на страницу авторизации
        return;
    }

    fetch('http://localhost:8080/showUserInfo', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Сетевой запрос на получение информации о пользователе не удался');
            }
            return response.json(); // Преобразование полученного ответа в JSON
        })
        .then(data => {
            // Предполагается, что сервер возвращает объект с ключом "userInfo"
            userInfoElement.textContent = `Имя пользователя: ${data.userInfo}`;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            userInfoElement.textContent = 'Произошла ошибка при получении информации о пользователе.';
        });
});
