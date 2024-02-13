document.addEventListener('DOMContentLoaded', function() {
    const userInfo = document.getElementById('userInfo');

        const username = localStorage.getItem("usernameForShow").toString();
        console.log(username);
    fetch(`http://localhost:8080/showProfile/${username}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
        },
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    }).then(data => {
        console.log(data.username);


        const imageUrl = `http://localhost:8080/images/${data.username}`;
        fetch(imageUrl)
            .then(response => response.blob())
            .then(imageBlob => {
                const imageObjectURL = URL.createObjectURL(imageBlob);
                document.getElementById('userImage').src = imageObjectURL;
            })
            .catch(error => console.error('Ошибка загрузки изображения:', error));
        localStorage.setItem("userId", data.senderId);
        localStorage.setItem("contactId", data.sss)

        userInfo.innerHTML = `${data.firstname} ${data.lastname} <br>
    Год рождения: ${data.yearOfBirth} <br> О себе: ${data.description}`;
    }) .catch(error => {
        console.error('Ошибка:', error);
        userInfo.textContent = 'Произошла ошибка при получении информации о пользователе.';
    });

    document.getElementById('sendMessageButton').addEventListener('click', function() {
        window.location.href = 'messages.html';
    });

});
