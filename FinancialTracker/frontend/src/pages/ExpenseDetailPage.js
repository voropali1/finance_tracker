import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from './Header';
import { useAuth } from './AuthContext';
import styles from './ExpenseDetailPage.module.css';

function ExpenseDetailPage() {
    const { id } = useParams();
    const { userId, isLoggedIn } = useAuth();
    const [expense, setExpense] = useState({ amount: '', name: '', transactionDate: '', expenseCategory: { id: '', categoryName: '' } });
    const [categories, setCategories] = useState([]);
    const [formExpense, setFormExpense] = useState({ amount: '', name: '', transactionDate: '', expenseCategory: { id: '', categoryName: '' } });
    const navigate = useNavigate();

    useEffect(() => {
        if (userId) {
            fetchExpense();
            fetchCategories();
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

    const fetchExpense = async () => {
        const url = `http://localhost:8080/transactions/expenses/${id}?userId=${userId}`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching expense');
            const data = await response.json();
            setExpense(data);
            setFormExpense(data);
        } catch (error) {
            console.error('Error fetching expense:', error);
        }
    };

    const fetchCategories = async () => {
        const url = `http://localhost:8080/transactions/expenses/categories`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching categories');
            const data = await response.json();
            setCategories(data);
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    };

    const updateExpense = async () => {
        const url = `http://localhost:8080/transactions/expenses/${id}?userId=${userId}`;
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formExpense),
            });
            if (!response.ok) throw new Error('Error updating expense');
            navigate('/expense'); // Navigate back to the expenses list
        } catch (error) {
            console.error('Error updating expense:', error);
        }
    };

    const handleExpenseChange = (e) => {
        const { name, value } = e.target;
        if (name === 'expenseCategory') {
            const selectedCategory = categories.find(category => category.id === parseInt(value));
            setFormExpense({ ...formExpense, expenseCategory: selectedCategory });
        } else {
            setFormExpense({ ...formExpense, [name]: value });
        }
    };

    return (
        <div className={styles.container}>
            <Header />
            <h2 className={styles.pageTitle}>Expense Details</h2>
            <div className={styles.expenseInfo}>
                <p><strong>Name:</strong> {expense.name}</p>
                <p><strong>Amount:</strong> ${expense.amount}</p>
                <p><strong>Date:</strong> {new Date(expense.transactionDate).toLocaleDateString()}</p>
                <p><strong>Category:</strong> {expense.expenseCategory.categoryName}</p>
            </div>
            <div className={styles.form}>
                <input
                    type="text"
                    name="name"
                    placeholder="Expense Name"
                    value={formExpense.name}
                    onChange={handleExpenseChange}
                    className={styles.inputField}
                />
                <input
                    type="number"
                    name="amount"
                    placeholder="Amount"
                    value={formExpense.amount}
                    onChange={handleExpenseChange}
                    className={styles.inputField}
                />
                <input
                    type="date"
                    name="transactionDate"
                    placeholder="Transaction Date"
                    value={formExpense.transactionDate}
                    onChange={handleExpenseChange}
                    className={styles.inputField}
                />
                <select name="expenseCategory" value={formExpense.expenseCategory.id} onChange={handleExpenseChange} className={styles.inputField}>
                    <option value="">Select Category</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.id}>{category.categoryName}</option>
                    ))}
                </select>
                <button onClick={updateExpense} className={styles.updateButton}>Update Expense</button>
            </div>
        </div>
    );
}

export default ExpenseDetailPage;
