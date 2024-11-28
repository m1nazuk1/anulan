/**
 * @author-Nizami-Alekperov
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../UserInfo/UserInfo.css';
import LoadingIndicator from '../../LoadingIndificator/LoadingInficator';

const ShowProfile: React.FC = () => {
    const [userInfo, setUserInfo] = useState<any>(null);
    const [userImage, setUserImage] = useState<string>('');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(true);
    const navigate = useNavigate();

    useEffect(() => {
        const username = localStorage.getItem('usernameForShow') || '';
        const jwtToken = localStorage.getItem('jwt-token');

        if (!username || !jwtToken) {
            setErrorMessage('Ошибка: Нет данных для отображения');
            setLoading(false);
            return;
        }

        setLoading(true);

        fetch(`http://localhost:8080/showProfile/${username}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${jwtToken}`,
            },
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Ошибка получения данных');
                }
                return response.json();
            })
            .then((data) => {
                setUserInfo(data);
                const imageUrl = `http://localhost:8080/images/${data.username}`;
                fetch(imageUrl)
                    .then((response) => response.blob())
                    .then((imageBlob) => {
                        const imageObjectURL = URL.createObjectURL(imageBlob);
                        setUserImage(imageObjectURL);
                    })
                    .catch((error) => {
                        console.error('Ошибка загрузки изображения:', error);
                    })
                    .finally(() => setLoading(false));
            })
            .catch((error) => {
                setErrorMessage('Ошибка при получении информации о пользователе');
                setLoading(false);
                console.error(error);
            });

        window.scrollTo(0, document.body.scrollHeight);
    }, []);

    const handleSendMessage = () => {
        const username = localStorage.getItem('usernameForShow');
        const jwtToken = localStorage.getItem('jwt-token');

        if (!username || !jwtToken) {
            setErrorMessage('Ошибка: Не указан пользователь или токен');
            return;
        }

        fetch(`http://localhost:8080/users/id/${username}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${jwtToken}`,
            },
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.id) {
                    localStorage.setItem('contactId', data.id.toString());
                    navigate('/messages');
                } else {
                    setErrorMessage('Не удалось получить ID пользователя');
                }
            })
            .catch((error) => {
                setErrorMessage('Ошибка при получении ID пользователя');
                console.error(error);
            });
    };

    if (loading) {
        return <LoadingIndicator />;
    }

    return (
        <div className="profile-container">
            <div className="header">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/user-contacts')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </div>

            {errorMessage ? (
                <p className="error-message">{errorMessage}</p>
            ) : (
                userInfo && (
                    <div className="user-info">
                        <div className="user-image">
                            <img id="userImage" src={userImage} alt="User Image" />
                        </div>
                        <div className="user-details">
                            <h2 className="info-heading">Информация о пользователе</h2>
                            <p className="info-item"><strong>Имя:</strong> <span>{userInfo.firstname}</span></p>
                            <p className="info-item"><strong>Фамилия:</strong> <span>{userInfo.lastname}</span></p>
                            <p className="info-item"><strong>Год рождения:</strong> <span>{userInfo.yearOfBirth}</span></p>
                            <p className="info-item"><strong>О себе:</strong> <span>{userInfo.description}</span></p>

                            <div className="message-button-container">
                                <button
                                    className="message-button"
                                    onClick={handleSendMessage}
                                >
                                    Написать сообщение
                                </button>
                            </div>
                        </div>
                    </div>
                )
            )}
        </div>
    );
};

export default ShowProfile;