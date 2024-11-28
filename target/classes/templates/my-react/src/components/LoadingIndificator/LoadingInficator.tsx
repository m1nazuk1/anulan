/**
 * @author-Nizami-Alekperov
 */

import React from 'react';
import './LoadingIndicator.css';

const LoadingIndicator: React.FC = () => {
    return (
        <div className="loading-container">
            <div className="spinner"></div>
        </div>
    );
};

export default LoadingIndicator;