import React, { useEffect, useState, ChangeEvent, KeyboardEvent, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './Messages.css';

interface Message {
    id: number;
    senderName: string;
    receiverName: string;
    content: string;
    sendTime: string; // ISO string
    sender: {
        id: number;
        username: string;
    };
}

const Messages: React.FC = () => {
    const [userId, setUserId] = useState<number | null>(null);
    const [contactId, setContactId] = useState<number | null>(null);
    const [messages, setMessages] = useState<Message[]>([]);
    const [messageText, setMessageText] = useState<string>('');
    const messageListRef = useRef<HTMLDivElement | null>(null);
    const navigate = useNavigate();

    // Загружаем данные пользователя и контакта
    const fetchUserData = async () => {
        try {
            const response = await fetch('http://localhost:8080/messages/ids', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
                },
            });

            if (!response.ok) {
                throw new Error('Ошибка получения данных пользователя и контакта');
            }

            const data = await response.json();
            setUserId(data.userId);
            setContactId(data.contactId);
        } catch (error) {
            console.error('Ошибка:', error);
        }
    };

    // Функция загрузки сообщений
    const fetchMessages = (userId: number, contactId: number) => {
        fetch(`http://localhost:8080/messages/${userId}/${contactId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
                'Cache-Control': 'no-cache',
            },
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Ошибка получения сообщений');
                }
                return response.json();
            })
            .then((data) => {
                setMessages(data);
                scrollToBottom(); // Скроллим вниз
            })
            .catch((error) => console.error('Ошибка:', error));
    };

    // Изменение текста в поле ввода
    const handleMessageChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        setMessageText(event.target.value);
    };

    // Отправка сообщения
    const sendMessage = () => {
        if (!messageText.trim() || !userId || !contactId) return;

        const messageData = {
            senderId: userId,
            receiverId: contactId,
            content: messageText,
        };

        fetch('http://localhost:8080/messages/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
                'Cache-Control': 'no-cache',
            },
            body: JSON.stringify(messageData),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Ошибка отправки сообщения');
                }
                setMessageText('');
                fetchMessages(userId, contactId); // Обновляем сообщения
            })
            .catch((error) => console.error('Ошибка:', error));
    };

    // Отправка при нажатии Enter
    const handleKeyDown = (event: KeyboardEvent<HTMLTextAreaElement>) => {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }
    };

    // Скроллинг вниз
    const scrollToBottom = () => {
        if (messageListRef.current) {
            messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
        }
    };

    useEffect(() => {
        fetchUserData();
    }, []);

    useEffect(() => {
        if (userId && contactId) {
            fetchMessages(userId, contactId);
        }
    }, [userId, contactId]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    return (
        <div className="chat-wrapper">
            <div className="header">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/messages')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </div>
            <div className="chat-container">
                <div ref={messageListRef} className="message-list">
                    {messages.map((message, index) => {
                        const isCurrentUserSender = message.senderName === localStorage.getItem("currentUserName");

                        return (
                            <div key={message.id || index} className={`message ${isCurrentUserSender ? 'sent' : 'received'}`}>
                                <div className="message-content">{message.content}</div>
                                <div className="message-time">{new Date(message.sendTime).toLocaleString()}</div>
                            </div>
                        );
                    })}
                </div>

                <div className="input-container">
                    <textarea
                        className="message-input"
                        value={messageText}
                        onChange={handleMessageChange}
                        onKeyDown={handleKeyDown}
                        placeholder="Введите сообщение"
                    />
                    <button className="send-button" onClick={sendMessage}></button>
                </div>
            </div>
        </div>
    );
};

export default Messages;