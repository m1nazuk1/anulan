document.getElementById('registrationForm-2').addEventListener('submit', function(e) {
    e.preventDefault();


    const image = document.getElementById("profileImage").files[0];
    const formData = new FormData();
    formData.append('image', image);
    const token = localStorage.getItem('jwt-token');
    const errorMessage = document.getElementById('errorMessage');

    fetch(`http://localhost:8080/auth/forImage`, {
        method: 'PUT',
        body: formData,
        headers:{
            'Authorization': `Bearer ${token}`,
        }
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
        .catch(error => console.error('Ошибка:', error));


});