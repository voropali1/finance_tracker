import React, {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import HomeIcon from './homesvg';
import UserIcon from "./UserSvg";
import styles from './Home.module.css';
import Popup from "./Popup";
import { useAuth } from './AuthContext';
import popstyle from './Popup.module.css';


const Header = ({ isHomePage, searchQuery, handleSearchChange }) => {
    const navigate = useNavigate();
    const { isLoggedIn, username, login, logout } = useAuth();
    const [showPopup, setShowPopup] = useState(false);
    const [loginUsername, setLoginUsername] = useState('');
    const [loginPassword, setLoginPassword] = useState('');
    const [loginError, setLoginError] = useState('');

    const togglePopup = () => {
        if (showPopup) {
            clearLoginForm();
        }
        setShowPopup(!showPopup);
    };

    const clearLoginForm = () => {
        setLoginUsername('');
        setLoginPassword('');
        setLoginError('');
    };

    const handleLoginAttempt = async (event) => {
        event.preventDefault();
        const success = await login(loginUsername, loginPassword);
        if (success) {
            togglePopup();
            setLoginUsername('');
            setLoginPassword('');
            setLoginError('');
        } else {
            setLoginPassword('');
            setLoginError('Invalid username or password.');
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <header className={styles.header}>
            <div onClick={() => navigate('/home')} style={{ cursor: 'pointer' }}>
                <HomeIcon width="50" height="50" className={styles.headerIcon} />
            </div>
            <div className={styles.userIcon} onClick={togglePopup} style={{ cursor: 'pointer' }}>
                <UserIcon width="50" height="50" className={styles.headerIcon} />
            </div>
            <Popup show={showPopup} onClose={togglePopup}>
                {isLoggedIn ? (
                    <div className={popstyle['form-container']}>
                        Welcome, {username}!
                        <button onClick={handleLogout} className={popstyle.logoutButton}>Logout</button>
                    </div>
                ) : (
                    <div className={popstyle.formContainer}>
                    <form onSubmit={handleLoginAttempt}>
                        <div>
                            <label htmlFor="username">Username:</label>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                value={loginUsername}
                                onChange={(e) => setLoginUsername(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label htmlFor="password">Password:</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={loginPassword}
                                onChange={(e) => setLoginPassword(e.target.value)}
                                required
                            />
                        </div>
                        {loginError && <div className={popstyle.errorMessage}>{loginError}</div>}
                        <button type="submit">Login</button>
                    </form>
                        <a href="/login" className={popstyle.signupLink}>Don't have an account? Sign up</a>
                    </div>
                )}
            </Popup>
        </header>
    );
};

export default Header;
