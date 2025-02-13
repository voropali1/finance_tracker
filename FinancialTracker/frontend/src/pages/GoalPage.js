import React, { useEffect, useState } from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import { useAuth } from './AuthContext';
import styles from './GoalPage.module.css';
import Header from "./Header";

function GoalPage() {
    const { id } = useParams();
    const [goal, setGoal] = useState(null);
    const [updatedGoal, setUpdatedGoal] = useState({ name: '', amount: '' });
    const { userId, isLoggedIn } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        fetchGoal();
    }, [id, userId]);

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

    const fetchGoal = async () => {
        if (!userId) {
            console.error('User ID is not available');
            return;
        }
        const url = `http://localhost:8080/finances/goals/${id}?userId=${userId}`;
        try {
            const response = await fetch(url);
            const status = response.status;
            console.log('Response status:', status);
            if (!response.ok) {
                if (response.status === 404) {
                    console.error('Goal not found');
                } else {
                    console.error('Error fetching goal');
                }
                return;
            }
            const text = await response.text();
            console.log('Response text:', text); // Debugging log
            const data = text ? JSON.parse(text) : null;
            if (data) {
                setGoal(data);
                setUpdatedGoal({ name: data.name, amount: data.amount });
            } else {
                console.error('No data found for goal');
            }
        } catch (error) {
            console.error('Error fetching goal:', error);
        }
    };

    const updateGoal = async () => {
        const url = `http://localhost:8080/finances/goals/${id}?userId=${userId}`;
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updatedGoal),
            });
            if (!response.ok) throw new Error('Error updating goal');
            fetchGoal();
        } catch (error) {
            console.error('Error updating goal:', error);
        }
    };

    if (!goal) {
        return <div>Loading...</div>;
    }

    return (
        <div className={styles.goalPage}>
            <Header />
            <h2 className={styles.goalTitle}>{goal.name}</h2>
            <p className={styles.goalAmount}>Amount: ${goal.amount}</p>

            <div className={styles.updateGoal}>
                <h3>Update Goal</h3>
                <input
                    type="text"
                    placeholder="Goal Name"
                    value={updatedGoal.name}
                    onChange={(e) => setUpdatedGoal({ ...updatedGoal, name: e.target.value })}
                    className={styles.inputField}
                />
                <input
                    type="number"
                    placeholder="Amount"
                    value={updatedGoal.amount}
                    onChange={(e) => setUpdatedGoal({ ...updatedGoal, amount: e.target.value })}
                    className={styles.inputField}
                />
                <button onClick={updateGoal} className={styles.updateButton}>Update Goal</button>
            </div>
        </div>
    );
}

export default GoalPage;
