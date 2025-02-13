import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export function useAuth() {
    return useContext(AuthContext);
}

export const AuthProvider = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [username, setUsername] = useState('');
    const [userId, setUserId] = useState(null);

    const fetchUserDetails = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            setIsLoggedIn(false);
            return;
        }

        fetch('http://localhost:8080/users/info', {
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch user details');
                }
                return response.json();
            })
            .then(data => {
                setIsLoggedIn(true);
                setUsername(data.username);
                setUserId(data.id);
                console.log('User ID set in context:', data.id);
            })
            .catch(error => {
                console.error(error);
                setIsLoggedIn(false);
                localStorage.removeItem('token');
            });
    };

    useEffect(() => {
        fetchUserDetails();
    }, []);

    const login = async (loginUsername, loginPassword) => {
        try {
            const response = await fetch('http://localhost:8080/users/authenticate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: loginUsername, password: loginPassword }),
            });

            if (!response.ok) throw new Error('Authentication failed');

            const data = await response.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', loginUsername);
            setIsLoggedIn(true);
            setUsername(loginUsername);
            fetchUserDetails();
            return true;
        } catch (error) {
            console.error('Login Error:', error);
            return false;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);
        setUsername('');
        setUserId(null);
    };

    return (
        <AuthContext.Provider value={{ isLoggedIn, username, userId, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

