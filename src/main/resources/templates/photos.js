// document.getElementById('registrationForm').addEventListener('submit', function(e) {
//     e.preventDefault(); // Предотвращаем стандартную отправку формы
//
//     // Создаем объект FormData и добавляем файл
//     const formData = new FormData();
//     formData.append('image', document.getElementById('profileImage').files[0]);
//     formData.append('username',  document.getElementById('username').value)
//
//     // Отправляем файл на сервер
//     fetch(`http://localhost:8080/path-to-upload`, {
//         method: 'POST',
//         body: formData,
//     })
//         .then(response => response.json())
//         .then(data => {
//             console.log('Успешная загрузка:', data);
//         })
//         .catch((error) => {
//             console.error('Ошибка загрузки:', error);
//         });
// });
