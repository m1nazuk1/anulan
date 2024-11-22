// src/components/Users.tsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Users.css';

interface User {
    username: string;
    firstname: string;
    lastname: string;
}

const Users: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [searchQuery, setSearchQuery] = useState<string>('');
    const [error, setError] = useState<string>('');
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (!token) {
            navigate('/login');
            return;
        }

        const fetchUsers = async () => {
            try {
                const response = await fetch('http://localhost:8080/users/all', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const data = await response.json();
                setUsers(data.myUsers);
            } catch (error) {
                setError('Ошибка при загрузке пользователей');
                console.error('Ошибка:', error);
            }
        };

        fetchUsers();
    }, [navigate]);

    const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchQuery(e.target.value.toLowerCase());
    };

    const filteredUsers = users.filter((user) => {
        const fullName = `${user.firstname} ${user.lastname}`.toLowerCase();
        return fullName.includes(searchQuery);
    });

    const handleUserClick = (username: string) => {
        localStorage.setItem('usernameForShow', username);
        navigate('/showProfile');
    };

    const getAvatarUrl = (username: string) => {
        return `http://localhost:8080/images/${username}`;
    };

    return (
        <div className="users-page">
            <header className="header">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/messages')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </header>

            <div className="search-container">
                <input
                    type="text"
                    id="searchInput"
                    placeholder="Поиск..."
                    value={searchQuery}
                    onChange={handleSearch}
                />
            </div>

            <div className="users-container">
                {filteredUsers.length > 0 ? (
                    filteredUsers.map((user) => (
                        <div
                            key={user.username}
                            className="user-card"
                            onClick={() => handleUserClick(user.username)}
                        >
                            <div className="user-image-container">
                                <img
                                    src={getAvatarUrl(user.username)} // Используем путь к изображению с именем пользователя
                                    alt={`${user.firstname} ${user.lastname}`}
                                    className="user-avatar"
                                    onError={(e) => {
                                        e.currentTarget.src = '/path/to/default-avatar.jpg'; // Фолбек на дефолтное изображение
                                    }}
                                />
                            </div>
                            <p className="user-name">{user.firstname} {user.lastname}</p>
                        </div>
                    ))
                ) : (
                    <p className="no-users">Нет пользователей для отображения</p>
                )}
            </div>
            {error && <div className="error-message">{error}</div>}
        </div>
    );
};

export default Users;