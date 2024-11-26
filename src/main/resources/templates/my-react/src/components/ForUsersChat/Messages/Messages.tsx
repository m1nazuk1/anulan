/**
 * @author-Nizami-Alekperov
 */

import React, { useState, useEffect, useRef, ChangeEvent, KeyboardEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiSend } from 'react-icons/fi';
import { FiTrash2 } from 'react-icons/fi';
import './Messages.css';
import LoadingIndicator from '../../LoadingIndificator/LoadingInficator';

interface Message {
    id: number;
    senderName: string;
    receiverName: string;
    content: string;
    sendTime: string;
    sender: {
        id: number;
        username: string;
    };
    isDeleting?: boolean;
}

const Messages: React.FC = () => {
    const [userId, setUserId] = useState<number | null>(null);
    const [contactId, setContactId] = useState<number | null>(null);
    const [messages, setMessages] = useState<Message[]>([]);
    const [messageText, setMessageText] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(true);
    const messageListRef = useRef<HTMLDivElement | null>(null);
    const navigate = useNavigate();

    const fetchUserData = async () => {
        const jwtToken = localStorage.getItem('jwt-token');
        if (!jwtToken) {
            navigate('/login');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/messages/ids', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`,
                },
            });

            if (!response.ok) {
                throw new Error('Ошибка получения данных пользователя');
            }

            const data = await response.json();
            setUserId(data.userId);
        } catch (error) {
            console.error('Ошибка:', error);
        }
    };

    const fetchMessages = (userId: number, contactId: number) => {
        setLoading(false);
        fetch(`http://localhost:8080/messages/${userId}/${contactId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
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
                setLoading(false);
                scrollToBottom();
            })
            .catch((error) => {
                setLoading(false);
                console.error('Ошибка:', error);
            });
    };

    const sendMessage = () => {
        setLoading(false);

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
            },
            body: JSON.stringify(messageData),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Ошибка отправки сообщения');
                }
                setMessageText('');
                fetchMessages(userId, contactId);
            })
            .catch((error) => console.error('Ошибка:', error));
    };

    const handleMessageChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        setMessageText(event.target.value);
    };

    const handleKeyDown = (event: KeyboardEvent<HTMLTextAreaElement>) => {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }
    };

    const scrollToBottom = () => {
        if (messageListRef.current) {
            messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
        }
    };

    useEffect(() => {
        const savedContactId = localStorage.getItem('contactId');
        if (savedContactId) {
            setContactId(Number(savedContactId));
            fetchUserData();
        } else {
            navigate('/user-contacts');
        }
    }, []);

    useEffect(() => {
        if (userId && contactId) {
            fetchMessages(userId, contactId);
        }
    }, [userId, contactId]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const handleDeleteMessage = (messageId: number) => {
        fetch(`http://localhost:8080/messages/delete/${messageId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
            },
        })
            .then((response) => {
                if (response.ok) {
                    const updatedMessages = messages.map(message =>
                        message.id === messageId ? { ...message, isDeleting: true } : message
                    );
                    setMessages(updatedMessages);

                    setTimeout(() => {
                        setMessages(prevMessages => prevMessages.filter(message => message.id !== messageId));
                    }, 500);
                } else {
                    console.error('Ошибка удаления сообщения');
                }
            })
            .catch((error) => console.error('Ошибка:', error));
    };

    return (
        <div className="chat-wrapper">
            <div className="headerZ">
                <h1 className="brand-name">anulan</h1>
                <nav className="navigation">
                    <button onClick={() => navigate('/user-contacts')}>Сообщения</button>
                    <button onClick={() => navigate('/users')}>Пользователи</button>
                    <button onClick={() => navigate('/user-info')}>Моя страница</button>
                </nav>
            </div>
            <button className="back-button" onClick={() => navigate('/user-contacts')}>
                <FiArrowLeft size={20} color="blue" />
            </button>
            <div className="chat-container">
                {loading ? (
                    <LoadingIndicator />
                ) : (
                    <>
                        <div ref={messageListRef} className="message-list">
                            {messages.map((message, index) => {
                                const isCurrentUserSender = message.senderName === localStorage.getItem("currentUserName");

                                return (
                                    <div key={message.id || index}
                                         className={`message ${isCurrentUserSender ? 'sent' : 'received'} ${message.isDeleting ? 'delete' : ''}`}>
                                        <div className="message-content">{message.content}</div>
                                        <div className="message-time">{new Date(message.sendTime).toLocaleString()}</div>

                                        <div className="delete-button" onClick={() => handleDeleteMessage(message.id)}>
                                            <FiTrash2 size={16} color="red" />
                                        </div>
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
                            <button className="send-button" onClick={sendMessage}>
                                <FiSend size={40} color="white" />
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default Messages;