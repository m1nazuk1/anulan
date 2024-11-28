/**
 * @author-Nizami-Alekperov
 */
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/AuthorizationSystem/Login/Login';
import Registration from './components/AuthorizationSystem/Registration/Registration';
import UserInfo from "./components/ForUsersProfile/UserInfo/UserInfo";
import ShowProfile from "./components/ForUsersProfile/ShowProfile/ShowProfile";
import EditUser from "./components/ForUsersProfile/EditUser/EditUser";
import EditPhoto from "./components/ForUsersProfile/EditPhoto/EditPhoto";
import Users from "./components/UsersListPage/Users";
import Messages from "./components/ForUsersChat/Messages/Messages";
import UserContacts from "./components/ForUsersChat/UserContacts/UserContacts";
import LoadingIndicator from "./components/LoadingIndificator/LoadingInficator";

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
                <Route path="/user-contacts" element={<UserContacts />} />
                <Route path="/loadingIndicator" element={<LoadingIndicator />} />
            </Routes>
        </Router>
    );
};

export default App;