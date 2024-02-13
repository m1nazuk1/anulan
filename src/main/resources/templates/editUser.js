document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('jwt-token');
    let myUsername = "";
    let myPassword = "";
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // Загрузка данных пользователя и их отображение в форме
    fetch('http://localhost:8080/showUserInfo', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при получении данных пользователя');
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('firstname').value = data.firstname || '';
            document.getElementById('lastname').value = data.lastname || '';
            document.getElementById('yearOfBirth').value = data.yearOfBirth || '';
            document.getElementById('description').value = data.description || '';
            myUsername = data.username;
            myPassword = data.password;
        })
        .catch(error => {
            console.error('Ошибка:', error);
        });

    // Обработчик отправки формы
    document.getElementById('editUserForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const formData = {
            firstname: document.getElementById('firstname').value,
            lastname: document.getElementById('lastname').value,
            yearOfBirth: document.getElementById('yearOfBirth').value,
            description: document.getElementById('description').value,
            username: myUsername,
            password: myPassword
        };

        fetch('http://localhost:8080/editUserInfo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(formData),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при обновлении данных пользователя');
                }
                return response.json();
            })
            .then(data => {
                if (data.message) {
                    alert(data.message);
                    window.location.href = 'user-info.html';
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Произошла ошибка при обновлении данных пользователя.');
            });
    });
});
