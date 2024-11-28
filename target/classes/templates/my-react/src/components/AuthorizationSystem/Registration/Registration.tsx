/**
 * @author-Nizami-Alekperov
 */

import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Registration.css';
import LoadingIndicator from '../../LoadingIndificator/LoadingInficator';

const Registration: React.FC = () => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [yearOfBirth, setYearOfBirth] = useState<string>('');
    const [firstname, setFirstname] = useState<string>('');
    const [lastname, setLastname] = useState<string>('');
    const [description, setDescription] = useState<string>('');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const navigate = useNavigate();

    const handleRegistrationSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        try {
            const response = await fetch('http://localhost:8080/auth/registration', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username,
                    yearOfBirth: parseInt(yearOfBirth),
                    password,
                    firstname,
                    lastname,
                    description,
                }),
            });

            const data = await response.json();

            if (data.message) {
                setErrorMessage(data.message);
            } else {
                console.log('JWT Token:', data['jwt-token']);
                navigate('/');
            }
        } catch (error) {
            console.error('Ошибка:', error);
        }
    };

    return (
        <div className="form-container">
            <form id="registrationForm" onSubmit={handleRegistrationSubmit}>
                <h2>Регистрация</h2>
                <div className="form-group">
                    <label htmlFor="username">Логин:</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Пароль:</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
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
                    <label htmlFor="description">О себе:</label>
                    <input
                        type="text"
                        id="description"
                        name="description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Зарегистрироваться</button>
                <p className="registration-invite">
                    Если у вас уже есть аккаунт, то можете <Link to="/">авторизоваться</Link>.
                </p>
                {errorMessage && <p id="errorMessage" className="error-message">{errorMessage}</p>}
            </form>
        </div>
    );
};

export default Registration;