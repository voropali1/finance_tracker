import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from './Header';
import { useAuth } from './AuthContext';
import styles from './IncomePage.module.css';

function IncomeDetailsPage() {
    const { id } = useParams();
    const { userId, isLoggedIn } = useAuth();
    const [income, setIncome] = useState({ amount: '', name: '', transactionDate: '', incomeCategory: { id: '', categoryName: '' } });
    const [categories, setCategories] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (userId) {
            fetchIncome();
            fetchCategories();
        }
    }, [userId, id]);

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

    const fetchIncome = async () => {
        const url = `http://localhost:8080/transactions/incomes/${id}?userId=${userId}`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching income');
            const data = await response.json();
            setIncome(data);
        } catch (error) {
            console.error('Error fetching income:', error);
        }
    };

    const fetchCategories = async () => {
        const url = `http://localhost:8080/transactions/incomes/categories`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching categories');
            const data = await response.json();
            setCategories(data);
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    };

    const updateIncome = async () => {
        if (!income.name || !income.amount || !income.transactionDate || !income.incomeCategory.id) {
            setErrorMessage('Please fill out all fields before submitting.');
            return;
        }

        const url = `http://localhost:8080/transactions/incomes/${id}?userId=${userId}`;
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(income),
            });
            if (!response.ok) throw new Error('Error updating income');
            navigate('/income'); // Redirect back to the income list page
        } catch (error) {
            console.error('Error updating income:', error);
        }
    };

    const handleIncomeChange = (e) => {
        const { name, value } = e.target;
        if (name === 'incomeCategory') {
            const selectedCategory = categories.find(category => category.id === parseInt(value));
            setIncome({ ...income, incomeCategory: selectedCategory });
        } else {
            setIncome({ ...income, [name]: value });
        }
    };

    return (
        <div className={styles.container}>
            <Header />
            <h2 className={styles.pageTitle}>Update Income</h2>
            {errorMessage && <p className={styles.error}>{errorMessage}</p>}
            <div className={styles.form}>
                <input
                    type="text"
                    name="name"
                    placeholder="Income Name"
                    value={income.name}
                    onChange={handleIncomeChange}
                    className={styles.inputField}
                />
                <input
                    type="number"
                    name="amount"
                    placeholder="Amount"
                    value={income.amount}
                    onChange={handleIncomeChange}
                    className={styles.inputField}
                />
                <input
                    type="date"
                    name="transactionDate"
                    placeholder="Transaction Date"
                    value={income.transactionDate}
                    onChange={handleIncomeChange}
                    className={styles.inputField}
                />
                <select name="incomeCategory" value={income.incomeCategory.id} onChange={handleIncomeChange} className={styles.inputField}>
                    <option value="">Select Category</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.id}>{category.categoryName}</option>
                    ))}
                </select>
                <button onClick={updateIncome} className={styles.addButton}>Update Income</button>
            </div>
            <h3 className={styles.addIncomeTitle}>Income Details</h3>
            <div className={styles.incomeDetails}>
                <p><strong>Name:</strong> {income.name}</p>
                <p><strong>Amount:</strong> ${income.amount}</p>
                <p><strong>Transaction Date:</strong> {new Date(income.transactionDate).toLocaleDateString()}</p>
                <p><strong>Category:</strong> {income.incomeCategory.categoryName}</p>
            </div>
        </div>
    );
}

export default IncomeDetailsPage;
