import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './UserContacts.css';

const UserContacts: React.FC = () => {
    const [contacts, setContacts] = useState<any[]>([]);
    const [imageUrls, setImageUrls] = useState<any>({});
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (!token) {
            navigate('/login');
            return;
        }

        fetch('http://localhost:8080/messages/contacts', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        })
            .then(response => response.json())
            .then(data => {
                setContacts(data);
                // Fetch profile images for each contact
                data.forEach((contact: any) => {
                    const imageUrl = `http://localhost:8080/images/${contact.username}`;
                    fetch(imageUrl)
                        .then(response => response.blob())
                        .then(imageBlob => {
                            const imageObjectURL = URL.createObjectURL(imageBlob);
                            setImageUrls((prevUrls: any) => ({
                                ...prevUrls,
                                [contact.username]: imageObjectURL,
                            }));
                        })
                        .catch(error => console.error('Ошибка загрузки изображения:', error));
                });
            })
            .catch(error => {
                console.error('Ошибка:', error);
            });
    }, [navigate]);

    const handleChatClick = (contactUsername: string, contactId: number) => {
        // Сохраняем данные контакта в localStorage
        localStorage.setItem('contactUsername', contactUsername);
        localStorage.setItem('contactId', contactId.toString()); // Добавляем contactId
        navigate('/messages');
    };

    return (
        <div className="user-contacts-container">
            {/* Шапка с навигационным меню */}
            <div className="header">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/user-contacts')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </div>

            <div className="contacts-list">
                <h2>Ваши сообщения</h2>
                {contacts.length > 0 ? (
                    <div className="contacts-cards">
                        {contacts.map((contact, index) => (
                            <div
                                key={index}
                                className="contact-card"
                                onClick={() => handleChatClick(contact.username, contact.id)}
                            >
                                <div className="contact-image">
                                    <img
                                        src={imageUrls[contact.username] || '/default-avatar.png'} // Default image if not loaded
                                        alt={`${contact.firstname} ${contact.lastname}`}
                                    />
                                </div>
                                <div className="contact-info">
                                    <p className="contact-name">
                                        <strong>{contact.firstname} {contact.lastname}</strong>
                                    </p>
                                    <p className="last-message">{contact.lastMessage}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p>У вас нет сообщений.</p>
                )}
            </div>
        </div>
    );
};

export default UserContacts;