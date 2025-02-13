import React, { useEffect, useState } from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import { useAuth } from './AuthContext';
import styles from './DebtPage.module.css';
import Header from "./Header";

function DebtPage() {
    const { id } = useParams();
    const [debt, setDebt] = useState(null);
    const [updatedDebt, setUpdatedDebt] = useState({ name: '', amount: '', dueDate: '', nameOfPersonToGiveBack: '', interestRate: '' });
    const [interest, setInterest] = useState(null);
    const [strategy, setStrategy] = useState(1);
    const { userId, isLoggedIn } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        fetchDebt();
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

    const fetchDebt = async () => {
        if (!userId) {
            console.error('User ID is not available');
            return;
        }
        const url = `http://localhost:8080/finances/debts/${id}?userId=${userId}`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching debt');
            const data = await response.json();
            setDebt(data);
            setUpdatedDebt({
                name: data.name,
                amount: data.amount,
                dueDate: data.dueDate,
                nameOfPersonToGiveBack: data.nameOfPersonToGiveBack,
                interestRate: data.interestRate
            });
        } catch (error) {
            console.error('Error fetching debt:', error);
        }
    };

    const updateDebt = async () => {
        const url = `http://localhost:8080/finances/debts/${id}?userId=${userId}`;
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updatedDebt),
            });
            if (!response.ok) throw new Error('Error updating debt');
            fetchDebt();
        } catch (error) {
            console.error('Error updating debt:', error);
        }
    };

    const setCalculationStrategy = async (strategyNumber) => {
        const url = `http://localhost:8080/finances/debts/${id}/set-calculation-strategy?userId=${userId}&numberOfStrategy=${strategyNumber}`;
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (!response.ok) throw new Error('Error setting calculation strategy');
        } catch (error) {
            console.error('Error setting calculation strategy:', error);
        }
    };

    const fetchInterestRate = async () => {
        await setCalculationStrategy(strategy);
        const url = `http://localhost:8080/finances/debts/${id}/interest-rate?userId=${userId}`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching interest rate');
            const data = await response.json();
            setInterest(data);
        } catch (error) {
            console.error('Error fetching interest rate:', error);
        }
    };

    if (!debt) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <Header />
            <div className={styles.debtPage}>
                <h2 className={styles.debtTitle}>{debt.name}</h2>
                <div className={styles.debtDetails}>
                    <p>Amount: ${debt.amount}</p>
                    <p>Due Date: {new Date(debt.dueDate).toLocaleDateString()}</p>
                    <p>To: {debt.nameOfPersonToGiveBack}</p>
                    <p>Interest Rate: {debt.interestRate}%</p>
                </div>

                <div className={styles.interestCalculation}>
                    <label htmlFor="strategy">Select Interest Calculation Strategy:</label>
                    <select
                        id="strategy"
                        value={strategy}
                        onChange={(e) => setStrategy(parseInt(e.target.value))}
                        className={styles.strategySelect}
                    >
                        <option value={1}>Compound Interest</option>
                        <option value={2}>Simple Interest</option>
                    </select>
                    <button onClick={fetchInterestRate} className={styles.calculateButton}>Calculate Interest</button>
                    {interest !== null && <p className={styles.interestResult}>Calculated Interest: ${interest}</p>}
                </div>

                <div className={styles.updateDebt}>
                    <h3>Update Debt</h3>
                    <input
                        type="text"
                        placeholder="Debt Name"
                        value={updatedDebt.name}
                        onChange={(e) => setUpdatedDebt({ ...updatedDebt, name: e.target.value })}
                        className={styles.inputField}
                    />
                    <input
                        type="number"
                        placeholder="Amount"
                        value={updatedDebt.amount}
                        onChange={(e) => setUpdatedDebt({ ...updatedDebt, amount: e.target.value })}
                        className={styles.inputField}
                    />
                    <input
                        type="date"
                        placeholder="Due Date"
                        value={updatedDebt.dueDate}
                        onChange={(e) => setUpdatedDebt({ ...updatedDebt, dueDate: e.target.value })}
                        className={styles.inputField}
                    />
                    <input
                        type="text"
                        placeholder="Name of Person to Give Back"
                        value={updatedDebt.nameOfPersonToGiveBack}
                        onChange={(e) => setUpdatedDebt({ ...updatedDebt, nameOfPersonToGiveBack: e.target.value })}
                        className={styles.inputField}
                    />
                    <input
                        type="number"
                        placeholder="Interest Rate"
                        value={updatedDebt.interestRate}
                        onChange={(e) => setUpdatedDebt({ ...updatedDebt, interestRate: e.target.value })}
                        className={styles.inputField}
                    />
                    <button onClick={updateDebt} className={styles.updateButton}>Update Debt</button>
                </div>
            </div>
        </div>
    );
}

export default DebtPage;