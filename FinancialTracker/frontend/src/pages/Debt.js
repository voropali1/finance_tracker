import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from './Header';
import styles from './Debt.module.css';
import { useAuth } from './AuthContext';

function Debt() {
    const [debts, setDebts] = useState([]);
    const [newDebt, setNewDebt] = useState({ name: '', amount: '', dueDate: '', nameOfPersonToGiveBack: '', interestRate: '' });
    const { userId, isLoggedIn } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (userId) {
            fetchDebts(userId);
        }
    }, [userId]);

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

    const fetchDebts = async (userId) => {
        const url = `http://localhost:8080/finances/debts/all?userId=${userId}`;
        console.log('Fetching debts from:', url);

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching debts');
            const data = await response.json();
            setDebts(data); // Assuming your API returns an array of debts
            console.log('First debt name:', data[0]?.name); // Print the name of the first debt
        } catch (error) {
            console.error('Error fetching debts:', error);
            setDebts([]); // In case of error, clear the debts
        }
    };

    const addDebt = async () => {
        const url = `http://localhost:8080/finances/debts/add-debt?userId=${userId}`;
        const debtData = {
            name: newDebt.name,
            amount: newDebt.amount,
            dueDate: newDebt.dueDate,
            nameOfPersonToGiveBack: newDebt.nameOfPersonToGiveBack,
            interestRate: newDebt.interestRate
        };

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(debtData),
            });

            if (!response.ok) throw new Error('Error adding debt');

            // Refresh the debts list after adding a new debt
            fetchDebts(userId);
            setNewDebt({ name: '', amount: '', dueDate: '', nameOfPersonToGiveBack: '', interestRate: '' }); // Reset the form
        } catch (error) {
            console.error('Error adding debt:', error);
        }
    };

    const deleteDebt = async (debtId) => {
        const url = `http://localhost:8080/finances/debts/${debtId}?userId=${userId}`;

        try {
            const response = await fetch(url, {
                method: 'DELETE',
            });
            if (!response.ok) throw new Error('Error deleting debt');
            fetchDebts(userId);
        } catch (error) {
            console.error('Error deleting debt:', error);
        }
    };

    const handleDebtClick = (debtId) => {
        navigate(`/debt/${debtId}`);
    };

        return (
        <div className={styles.container}>
            <Header />
            <h2 className={styles.pageTitle}>Here are all your debts</h2>
            <ul className={styles.debtList}>
                {debts.map(debt => (
                    <li key={debt.id} className={styles.debtItem} onClick={() => handleDebtClick(debt.id)}>
                        <div className={styles.debtInfo}>
                            {debt.name}: ${debt.amount} - Due by {new Date(debt.dueDate).toLocaleDateString()}
                            <br />
                            To: {debt.nameOfPersonToGiveBack} - Interest Rate: {debt.interestRate}%
                        </div>
                        <button
                            className={styles.deleteButton}
                            onClick={(e) => {
                                e.stopPropagation();
                                deleteDebt(debt.id);
                            }}
                        >
                            Delete
                        </button>
                    </li>
                ))}
            </ul>
            <h3 className={styles.addDebtTitle}>Add a New Debt</h3>
            <div className={styles.form}>
                <input
                    type="text"
                    placeholder="Debt Name"
                    value={newDebt.name}
                    onChange={(e) => setNewDebt({ ...newDebt, name: e.target.value })}
                    className={styles.inputField}
                />
                <input
                    type="number"
                    placeholder="Amount"
                    value={newDebt.amount}
                    onChange={(e) => setNewDebt({ ...newDebt, amount: e.target.value })}
                    className={styles.inputField}
                />
                <input
                    type="date"
                    placeholder="Due Date"
                    value={newDebt.dueDate}
                    onChange={(e) => setNewDebt({ ...newDebt, dueDate: e.target.value })}
                    className={styles.inputField}
                />
                <input
                    type="text"
                    placeholder="Owed To"
                    value={newDebt.nameOfPersonToGiveBack}
                    onChange={(e) => setNewDebt({ ...newDebt, nameOfPersonToGiveBack: e.target.value })}
                    className={styles.inputField}
                />
                <input
                    type="number"
                    placeholder="Interest Rate"
                    value={newDebt.interestRate}
                    onChange={(e) => setNewDebt({ ...newDebt, interestRate: e.target.value })}
                    className={styles.inputField}
                />
                <button onClick={addDebt} className={styles.addButton}>Add Debt</button>
            </div>
        </div>
    );
}

export default Debt;