import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import styles from './Login.module.css';
export default function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [loginError, setLoginError] = useState('');
    const [showLogin, setShowLogin] = useState(true);

    const [loginUsername, setLoginUsername] = useState('');
    const [loginPassword, setLoginPassword] = useState('');

    const [registerUsername, setRegisterUsername] = useState('');
    const [registerEmail, setRegisterEmail] = useState('');
    const [registerPassword, setRegisterPassword] = useState('');



    const handleLoginSubmit = async (event) => {
        event.preventDefault();
        const success = await login(loginUsername, loginPassword);
        if (success) {
            navigate('/home');
            setLoginUsername('');
            setLoginPassword('');
        } else {
            setLoginError('Invalid username or password.');
            setLoginPassword('');
        }

    };

    const handleRegisterSubmit = async (event) => {
        event.preventDefault();
        try {
            const response = await fetch('http://localhost:8080/users/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: registerUsername,
                    email: registerEmail,
                    password: registerPassword,
                }),
            });

            if (!response.ok) {
                setRegisterPassword('');
                throw new Error(`Registration failed: ${response.statusText}`);
            }

            const data = await response.json();
            console.log('Registration Success:', data);
            setShowLogin(true); // Switch to the login view
            setRegisterUsername('');
            setRegisterEmail('');
            setRegisterPassword('');
        } catch (error) {
            console.error('Registration Error:', error);
            setRegisterPassword('');
        }
    };

    const switchToRegister = () => {
        setShowLogin(false);
        setLoginError(''); // Clear any login errors
    };

    // Switch to login view and clear errors
    const switchToLogin = () => {
        setShowLogin(true);
        setLoginError(''); // Clear any login errors
    };


    return (
        <main className={styles.container}>
            <section  className={styles.formContainer} style={{ display: showLogin ? 'block' : 'none' }}>
                <h2 className={styles.title}>Login</h2>
                <form onSubmit={handleLoginSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label htmlFor="login-username" className={styles.label}>Username:</label>
                        <input type="text" id="login-username" required className={styles.input} value={loginUsername}
                               onChange={(e) => setLoginUsername(e.target.value)} />
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="login-password" className={styles.label}>Password:</label>
                        <input type="password" id="login-password" required className={styles.input} value={loginPassword}
                               onChange={(e) => setLoginPassword(e.target.value)} />
                    </div>
                    <button type="submit" className={styles.button}>Login</button>
                </form>
                <div className={styles.switchForm}>
                    <p>No account? <button onClick={switchToRegister} className={styles.buttonLink}>Register here</button></p>
                </div>
            </section >

            <section  className={styles.formContainer} style={{ display: !showLogin ? 'block' : 'none' }}>
                <h2 className={styles.title}>Register</h2>
                <form onSubmit={handleRegisterSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label htmlFor="register-username" className={styles.label}>Username:</label>
                        <input type="text" id="register-username" required className={styles.input} value={registerUsername}
                               onChange={(e) => setRegisterUsername(e.target.value)}/>
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="register-email" className={styles.label}>Email:</label>
                        <input type="email" id="register-email" required className={styles.input} value={registerEmail}
                               onChange={(e) => setRegisterEmail(e.target.value)}/>
                    </div>
                    <div className={styles.formGroup}>
                        <label htmlFor="register-password" className={styles.label}>Password:</label>
                        <input type="password" id="register-password" required className={styles.input} value={registerPassword}
                               onChange={(e) => setRegisterPassword(e.target.value)} />
                    </div>
                    <button type="submit" className={styles.button}  >Register</button>
                </form>
                <div className={styles.switchForm}>
                    <p>Already have an account? <button onClick={switchToLogin} className={styles.buttonLink}>Login here</button></p>
                </div>
            </section >
            {loginError && <div className={styles.error}>{loginError}</div>}
        </main>
    );
}

