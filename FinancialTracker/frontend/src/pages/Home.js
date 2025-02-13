import React, { useState, useEffect } from 'react';
import styles from './Home.module.css';
import Header from './Header';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

function Home() {
    const navigate = useNavigate();
    const { isLoggedIn } = useAuth();
    const [showPopup, setShowPopup] = useState(false);

    if (!isLoggedIn) {
        return (
            <div className={styles.notLoggedInContainer}>
                <h2>You need to be logged in to use this app</h2>
                <button onClick={() => navigate(`/login`)} className={styles.viewDetailsButton2}>
                    Login
                </button>
            </div>
        );
    }

    return (
        <div className={styles.App}>
            <Header />
            <main className={styles.mainContent}>
                <div className={styles.container}>
                    <div className={styles.box} onClick={() => navigate('/debt')}>
                        <h3>Debts</h3>
                    </div>
                    <div className={styles.box} onClick={() => navigate('/goal')}>
                        <h3>Goals</h3>
                    </div>
                    <div className={styles.box} onClick={() => navigate('/income')}>
                        <h3>Incomes</h3>
                    </div>
                    <div className={styles.box} onClick={() => navigate('/expense')}>
                        <h3>Expenses</h3>
                    </div>
                    <div className={styles.box} onClick={() => navigate('/statistics')}>
                        <h3>Statistics</h3>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default Home;
