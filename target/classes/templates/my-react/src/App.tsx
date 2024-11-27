import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import Registration from './components/Registration';
import UserInfo from "./components/UserInfo";
import ShowProfile from "./components/ShowProfile";
import EditUser from "./components/EditUser";
import EditPhoto from "./components/EditPhoto";
import Users from "./components/Users";
import Messages from "./components/Messages";

const App: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/registration" element={<Registration />} />
                <Route path="/login" element={<Login />} />
                <Route path="/user-info" element={<UserInfo />} />
                <Route path="/showProfile" element={<ShowProfile />} />
                <Route path="/edit-user" element={<EditUser />} />
                <Route path="/edit-photo" element={<EditPhoto />} />
                <Route path="/users" element={<Users />} />
                <Route path="/messages" element={<Messages />} />
            </Routes>
        </Router>
    );
};

export default App;