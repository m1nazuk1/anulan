import React, { useState } from 'react';

const CreateChat: React.FC = () => {
    const [userIds, setUserIds] = useState<number[]>([]);
    const [chatName, setChatName] = useState('');

    const createChat = () => {
        const chatData = {
            name: chatName,
            userIds,
        };

        fetch('/chats/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt-token')}`,
            },
            body: JSON.stringify(chatData),
        })
            .then(response => response.json())
            .then(data => console.log('Чат создан', data))
            .catch(error => console.error('Ошибка при создании чата:', error));
    };

    return (
        <div>
            <h2>Создать чат</h2>
            <input
                type="text"
                placeholder="Название чата"
                value={chatName}
                onChange={(e) => setChatName(e.target.value)}
            />
            <button onClick={createChat}>Создать</button>
        </div>
    );
};

export default CreateChat;