import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

const rootElement = document.getElementById('root'); // Получаем элемент с id="root"
if (rootElement) {
    const root = ReactDOM.createRoot(rootElement); // Используем createRoot для монтирования
    root.render(<App />);
} else {
    console.error('Root element not found!');
}