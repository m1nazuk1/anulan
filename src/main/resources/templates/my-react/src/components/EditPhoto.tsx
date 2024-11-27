import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft } from "react-icons/fi";
import './EditPhoto.css';
import LoadingIndicator from './LoadingInficator'; // Импортируем компонент индикатора загрузки

const EditPhoto: React.FC = () => {
    const [image, setImage] = useState<File | null>(null);
    const [errorMessage, setErrorMessage] = useState<string>('');
    const navigate = useNavigate();
    const [loading, setLoading] = useState<boolean>(false); // Статус загрузки

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            setImage(event.target.files[0]);
        }
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        if (!image) {
            setErrorMessage('Пожалуйста, выберите изображение для загрузки.');
            return;
        }

        const formData = new FormData();
        formData.append('image', image);

        const token = localStorage.getItem('jwt-token');
        if (!token) {
            setErrorMessage('Вы не авторизованы.');
            return;
        }

        setLoading(true); // Включаем индикатор загрузки

        try {
            const response = await fetch('http://localhost:8080/auth/forImage', {
                method: 'PUT',
                body: formData,
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            setLoading(false); // Отключаем индикатор загрузки

            if (!response.ok) {
                throw new Error('Ошибка при обновлении данных пользователя');
            }

            const data = await response.json();
            if (data.message) {
                alert(data.message);
                navigate('/user-info');
            }
        } catch (error) {
            console.error('Ошибка:', error);
            setLoading(false); // Отключаем индикатор загрузки в случае ошибки
            setErrorMessage('Произошла ошибка при обновлении аватара.');
        }
    };

    return (
        <div className="form-container">
            <form id="registrationForm-2" onSubmit={handleSubmit} encType="multipart/form-data">
                <h2>Добавление аватара</h2>

                {/* Показать индикатор загрузки, если происходит загрузка */}
                {loading ? (
                    <LoadingIndicator />
                ) : (
                    <div className="form-group">
                        <label htmlFor="profileImage">Фотография профиля</label>
                        <input
                            type="file"
                            id="profileImage"
                            name="profileImage"
                            onChange={handleImageChange}
                            required
                        />
                    </div>
                )}

                <button type="submit" disabled={loading}>Изменить!</button>
                {errorMessage && <p id="errorMessage" className="error-message">{errorMessage}</p>}
            </form>

            {/* Кнопка назад */}
            <button className="back-buttonD" onClick={() => navigate('/user-info')}>
                <FiArrowLeft size={30} color="blue" />
            </button>
        </div>
    );
};

export default EditPhoto;