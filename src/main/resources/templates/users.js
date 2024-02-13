document.addEventListener('DOMContentLoaded', function() {
    console.log(localStorage.getItem('jwt-token'))
    fetch('http://localhost:8080/users/all', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`
        }
    })
        .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
        .then(data => {
            const usersList = document.getElementById('usersList');
            data.myUsers.forEach(user => {
                const userCard = document.createElement('div');
                userCard.classList.add('user-card');
                userCard.innerHTML = `
        <img src="http://localhost:8080/images/${user.username}" alt="User Image">
        <p>${user.firstname} ${user.lastname}</p>
      `;
                userCard.addEventListener('click', () => {
                    localStorage.setItem("usernameForShow", user.username);
                    window.location.href = 'showProfile.html';

                })
                usersList.appendChild(userCard);
            });
        })
        .catch(error => console.error('Ошибка:', error));
});
