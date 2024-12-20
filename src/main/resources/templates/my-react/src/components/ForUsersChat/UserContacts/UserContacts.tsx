/**
 * @author-Nizami-Alekperov
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './UserContacts.css';
import LoadingIndicator from '../../LoadingIndificator/LoadingInficator';

const UserContacts: React.FC = () => {
    const [contacts, setContacts] = useState<any[]>([]);
    const [imageUrls, setImageUrls] = useState<any>({});
    const [loading, setLoading] = useState<boolean>(true)
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('jwt-token');
        if (!token) {
            navigate('/login');
            return;
        }

        const fetchContacts = async () => {
            try {
                const response = await fetch('http://localhost:8080/messages/contacts', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }

                const data = await response.json();
                setContacts(data);

                const imageFetchPromises = data.map((contact: any) => {
                    const imageUrl = `http://localhost:8080/images/${contact.username}`;
                    return fetch(imageUrl)
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

                await Promise.all(imageFetchPromises);
            } catch (error) {
                console.error('Ошибка:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchContacts();
    }, [navigate]);

    const handleChatClick = (contactUsername: string, contactId: number) => {
        localStorage.setItem('contactUsername', contactUsername);
        localStorage.setItem('usernameForShow', contactUsername);
        localStorage.setItem('contactUsernames', contactUsername);
        localStorage.setItem('contactId', contactId.toString());
        navigate('/messages');
    };

    if (loading) {
        return <LoadingIndicator />;
    }

    return (
        <div className="user-contacts-container">
            <div className="headerS">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/user-contacts')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </div>

            <div className="contacts-list">
                <h2>Ваши чаты</h2>
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
                                        src={imageUrls[contact.username] || '/default-avatar.png'}
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