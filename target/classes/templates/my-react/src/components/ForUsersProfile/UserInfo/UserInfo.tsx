/**
 * @author-Nizami-Alekperov
 */

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './UserInfo.css';
import LoadingIndicator from '../../LoadingIndificator/LoadingInficator';

const UserInfo: React.FC = () => {
    const [userInfo, setUserInfo] = useState<any>(null);
    const [imageUrl, setImageUrl] = useState<string>('');
    const navigate = useNavigate();
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (!token) {
            navigate('/login');
            return;
        }

        const fetchUserInfo = async () => {
            try {
                const response = await fetch('http://localhost:8080/showUserInfo', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const data = await response.json();
                setUserInfo(data);
                localStorage.setItem("currentUserName", data.firstname);
                const userImageUrl = `http://localhost:8080/images/${data.username}`;
                setImageUrl(userImageUrl);
            } catch (error) {
                console.error('Ошибка:', error);
                setUserInfo(null);
            } finally {
                setLoading(false);
            }
        };

        fetchUserInfo();
    }, [navigate]);

    if (loading) {
        return <LoadingIndicator />;
    }

    const handleLogout = () => {
        localStorage.removeItem('jwt-token');
        navigate('/login');
    };

    const handleEditProfile = () => {
        navigate('/edit-user');
    };

    const handleEditPhoto = () => {
        navigate('/edit-photo');
    };

    return (
        <div className="user-info-container">
            <div className="header">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/user-contacts')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </div>

            {userInfo ? (
                <div className="user-info">
                    <h2 className="info-heading">Информация о пользователе</h2>
                    <img id="userImage" src={imageUrl} alt="User" />
                    <p className="info-item"><strong>Имя:</strong> <span>{userInfo.firstname}</span></p>
                    <p className="info-item"><strong>Фамилия:</strong> <span>{userInfo.lastname}</span></p>
                    <p className="info-item"><strong>Год рождения:</strong> <span>{userInfo.yearOfBirth}</span></p>
                    <p className="info-item"><strong>О себе:</strong> <span>{userInfo.description}</span></p>

                    <div className="edit-buttons-container">
                        <button className="edit-user-button" onClick={handleEditProfile}>Редактировать профиль</button>
                        <button className="edit-user-button" onClick={handleEditPhoto}>Редактировать фотографию</button>
                        <button className="logout-button" onClick={handleLogout}>Выйти</button>
                    </div>
                </div>
            ) : (
                <p>Данные пользователя не найдены</p>
            )}
        </div>
    );
};

export default UserInfo;