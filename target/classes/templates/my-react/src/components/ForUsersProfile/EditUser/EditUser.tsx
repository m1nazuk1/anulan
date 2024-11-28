/**
 * @author-Nizami-Alekperov
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './EditUser.css';
import { FiArrowLeft } from "react-icons/fi";
import LoadingIndicator from '../../LoadingIndificator/LoadingInficator';

const EditUser: React.FC = () => {
    const [firstname, setFirstname] = useState<string>('');
    const [lastname, setLastname] = useState<string>('');
    const [yearOfBirth, setYearOfBirth] = useState<number | string>('');
    const [description, setDescription] = useState<string>('');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(true);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (!token) {
            navigate('/login');
            return;
        }

        setLoading(true);

        fetch('http://localhost:8080/showUserInfo', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        })
            .then(response => response.json())
            .then(data => {
                setFirstname(data.firstname || '');
                setLastname(data.lastname || '');
                setYearOfBirth(data.yearOfBirth || '');
                setDescription(data.description || '');
                setUsername(data.username);
                setPassword(data.password);
                setLoading(false);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                setErrorMessage('Ошибка при загрузке данных пользователя.');
                setLoading(false);
            });
    }, [navigate]);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        const token = localStorage.getItem('jwt-token');
        if (!token) {
            setErrorMessage('Пожалуйста, войдите в систему.');
            return;
        }

        const formData = {
            firstname,
            lastname,
            yearOfBirth,
            description,
            username,
            password,
        };

        try {
            const response = await fetch('http://localhost:8080/editUserInfo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(formData),
            });

            const data = await response.json();
            if (data.message) {
                alert(data.message);
                navigate('/user-info');
            }
        } catch (error) {
            console.error('Ошибка:', error);
            setErrorMessage('Произошла ошибка при обновлении данных пользователя.');
        }
    };

    return (
        <div className="form-container">
            <button className="back-buttonS" onClick={() => navigate('/user-info')}>
                <FiArrowLeft size={20} color="blue" />
            </button>
            <h2>Редактирование профиля</h2>

            {loading ? (
                <LoadingIndicator />
            ) : (
                <form id="editUserForm" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="firstname">Имя:</label>
                        <input
                            type="text"
                            id="firstname"
                            name="firstname"
                            value={firstname}
                            onChange={(e) => setFirstname(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="lastname">Фамилия:</label>
                        <input
                            type="text"
                            id="lastname"
                            name="lastname"
                            value={lastname}
                            onChange={(e) => setLastname(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="yearOfBirth">Год рождения:</label>
                        <input
                            type="number"
                            id="yearOfBirth"
                            name="yearOfBirth"
                            value={yearOfBirth}
                            onChange={(e) => setYearOfBirth(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="description">О себе:</label>
                        <textarea
                            id="description"
                            name="description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit">Сохранить изменения</button>
                    {errorMessage && <p className="error-message">{errorMessage}</p>}
                </form>
            )}
        </div>
    );
};

export default EditUser;