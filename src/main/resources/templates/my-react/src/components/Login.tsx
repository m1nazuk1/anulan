// src/components/Login.tsx
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import LoadingIndicator from './LoadingInficator';


const Login: React.FC = () => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const navigate = useNavigate();

    const handleLoginSubmit = async (event: React.FormEvent) => {
        event.preventDefault();


        try {
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            if (data.message) {
                setErrorMessage(data.message);
            } else {
                localStorage.setItem('jwt-token', data['jwt-token']);
                navigate('/user-info');
            }
        } catch (error) {
            console.error('Ошибка:', error);
        }
    };

    const togglePasswordVisibility = () => {
        const passwordField = document.getElementById('password') as HTMLInputElement;
        if (passwordField) {
            passwordField.type = passwordField.type === 'password' ? 'text' : 'password';
        }
    };

    return (
        <div className="form-container">
            <form id="loginForm" onSubmit={handleLoginSubmit}>
                <h2>Авторизация</h2>
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
                    <div className="password-container">
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <span className="toggle-password" onClick={togglePasswordVisibility}>
              🔒
            </span>
                    </div>
                </div>
                <button type="submit">Войти</button>
                <p className="registration-invite">
                    Если у вас еще нет аккаунта, то можете <Link to="/registration">зарегистрироваться</Link>.
                </p>
                {errorMessage && <p id="errorMessage" className="error-message">{errorMessage}</p>}
            </form>
        </div>
    );
};

export default Login;