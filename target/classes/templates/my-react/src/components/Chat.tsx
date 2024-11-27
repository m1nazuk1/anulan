import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

const Chat: React.FC = () => {
    const { chatId } = useParams();
    const [messages, setMessages] = useState<any[]>([]);
    const [messageText, setMessageText] = useState('');

    useEffect(() => {
        fetch(`/messages/${chatId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
            },
        })
            .then(response => response.json())
            .then(data => setMessages(data))
            .catch(error => console.error('Ошибка:', error));
    }, [chatId]);

    const sendMessage = () => {
        if (!messageText.trim()) return;

        const messageData = {
            chatId,
            content: messageText,
        };

        fetch('/messages/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
            },
            body: JSON.stringify(messageData),
        })
            .then(response => response.json())
            .then(data => {
                setMessages([...messages, data]);
                setMessageText('');
            })
            .catch(error => console.error('Ошибка при отправке сообщения:', error));
    };

    return (
        <div>
            <h2>Чат {chatId}</h2>
            <div>
                {messages.map((message) => (
                    <div key={message.id}>
                        <strong>{message.senderName}</strong>: {message.content}
                    </div>
                ))}
            </div>
            <textarea
                value={messageText}
                onChange={(e) => setMessageText(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
            />
            <button onClick={sendMessage}>Отправить</button>
        </div>
    );
};

export default Chat;