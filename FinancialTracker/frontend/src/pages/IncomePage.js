import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from './Header';
import { useAuth } from './AuthContext';
import styles from './IncomePage.module.css';

function IncomePage() {
    const { userId, isLoggedIn } = useAuth();
    const [incomes, setIncomes] = useState([]);
    const [categories, setCategories] = useState([]);
    const [newIncome, setNewIncome] = useState({ amount: '', name: '', transactionDate: '', incomeCategory: { id: '', categoryName: '' } });
    const [categoryName, setCategoryName] = useState('');
    const [sortOrder, setSortOrder] = useState('desc'); // default sorting order
    const [filterCategory, setFilterCategory] = useState('');
    const [filterAmount, setFilterAmount] = useState({ from: '', to: '' });
    const navigate = useNavigate();

    useEffect(() => {
        if (userId) {
            fetchIncomes();
            fetchCategories();
        }
    }, [userId, sortOrder, filterCategory, filterAmount]);

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

    const fetchIncomes = async () => {
        let url = `http://localhost:8080/transactions/incomes/all_incomes_${sortOrder}?userId=${userId}`;
        if (filterCategory) {
            url = `http://localhost:8080/transactions/incomes/incomes-by-category/${filterCategory}?userId=${userId}`;
        } else if (filterAmount.from || filterAmount.to) {
            const from = filterAmount.from || 0;
            const to = filterAmount.to || Number.MAX_VALUE;
            url = `http://localhost:8080/transactions/incomes/filter-by-amount?from=${from}&to=${to}&userId=${userId}`;
        }

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error fetching incomes');
            const data = await response.json();
            setIncomes(data);
        } catch (error) {
            console.error('Error fetching incomes:', error);
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

    const addIncome = async () => {
        if (!newIncome.name || !newIncome.amount || !newIncome.transactionDate || !newIncome.incomeCategory.id) {
            alert('Please fill in all fields');
            return;
        }

        const url = `http://localhost:8080/transactions/incomes/add-income?userId=${userId}`;
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newIncome),
            });
            if (!response.ok) throw new Error('Error adding income');
            fetchIncomes(); // Refresh the incomes list
            setNewIncome({ amount: '', name: '', transactionDate: '', incomeCategory: { id: '', categoryName: '' } }); // Reset the form
        } catch (error) {
            console.error('Error adding income:', error);
            alert('Date must be less than today');
            console.log('Id of income category:', newIncome.incomeCategory.id);
        }
    };

    const addCategory = async () => {
        if (!categoryName) {
            alert('Please enter a category name');
            return;
        }

        const url = `http://localhost:8080/transactions/incomes/add-category`;
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ categoryName }),
            });
            if (!response.ok) throw new Error('Error adding category');
            fetchCategories(); // Refresh the categories list
            setCategoryName(''); // Reset the form
        } catch (error) {
            console.error('Error adding category:', error);
        }
    };

    const deleteIncome = async (incomeId) => {
        const url = `http://localhost:8080/transactions/incomes/${incomeId}?userId=${userId}`;
        try {
            const response = await fetch(url, {
                method: 'DELETE',
            });
            if (!response.ok) throw new Error('Error deleting income');
            fetchIncomes(); // Refresh the list of incomes
        } catch (error) {
            console.error('Error deleting income:', error);
        }
    };

    const handleIncomeChange = (e) => {
        const { name, value } = e.target;
        if (name === 'incomeCategory') {
            const selectedCategory = categories.find(category => category.id === parseInt(value));
            setNewIncome({ ...newIncome, incomeCategory: selectedCategory });
        } else {
            setNewIncome({ ...newIncome, [name]: value });
        }
    };

    const handleIncomeClick = (incomeId) => {
        navigate(`/income/${incomeId}`);
    };

    const handleSortChange = (e) => {
        setSortOrder(e.target.value);
        setFilterCategory('');
        setFilterAmount({ from: '', to: '' });
    };

    const handleCategoryFilterChange = (e) => {
        setFilterCategory(e.target.value);
        setFilterAmount({ from: '', to: '' });
    };

    const handleAmountFilterChange = (e) => {
        const { name, value } = e.target;
        setFilterAmount((prev) => ({
            ...prev,
            [name]: value,
        }));
        setFilterCategory('');
    };

    const exportData = async (format) => {
        const url = `http://localhost:8080/transactions/incomes/export/${format}?userId=${userId}`;
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error exporting data');

            const blob = await response.blob();
            const downloadUrl = window.URL.createObjectURL(new Blob([blob]));
            const link = document.createElement('a');
            link.href = downloadUrl;
            link.setAttribute('download', `debts.${format === 'excel' ? 'xlsx' : 'pdf'}`);
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        } catch (error) {
            console.error('Error exporting data:', error);
        }
    };

    return (
        <div className={styles.container}>
            <Header />
            <h2 className={styles.pageTitle}>Incomes</h2>
            {/* Export Buttons */}
            <div className={styles.exportButtons}>
                <button onClick={() => exportData('excel')} className={styles.exportButton}>Export to Excel</button>
                <button onClick={() => exportData('pdf')} className={styles.exportButton}>Export to PDF</button>
            </div>
            <div className={styles.filters}>
                <select onChange={handleSortChange} value={sortOrder} className={styles.inputField}>
                    <option value="desc">Descending</option>
                    <option value="asc">Ascending</option>
                </select>
                <select onChange={handleCategoryFilterChange} value={filterCategory} disabled={!!filterAmount.from || !!filterAmount.to} className={styles.inputField}>
                    <option value="">All Categories</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.id}>{category.categoryName}</option>
                    ))}
                </select>
                <input
                    type="number"
                    name="from"
                    placeholder="From Amount"
                    value={filterAmount.from}
                    onChange={handleAmountFilterChange}
                    className={styles.inputField}
                    disabled={!!filterCategory}
                />
                <input
                    type="number"
                    name="to"
                    placeholder="To Amount"
                    value={filterAmount.to}
                    onChange={handleAmountFilterChange}
                    className={styles.inputField}
                    disabled={!!filterCategory}
                />
            </div>
            <ul className={styles.incomeList}>
                {incomes.map(income => (
                    <li key={income.id} className={styles.incomeItem} onClick={() => handleIncomeClick(income.id)}>
                        <div className={styles.incomeInfo}>
                            {income.name}: ${income.amount} - {new Date(income.transactionDate).toLocaleDateString()}
                            <br />
                            Category: {income.incomeCategory.categoryName}
                        </div>
                        <button
                            className={styles.deleteButton}
                            onClick={(e) => {
                                e.stopPropagation();
                                deleteIncome(income.id);
                            }}
                        >
                            Delete
                        </button>
                    </li>
                ))}
            </ul>
            <h3 className={styles.addIncomeTitle}>Add a New Income</h3>
            <div className={styles.form}>
                <input
                    type="text"
                    name="name"
                    placeholder="Income Name"
                    value={newIncome.name}
                    onChange={handleIncomeChange}
                    className={styles.inputField}
                />
                <input
                    type="number"
                    name="amount"
                    placeholder="Amount"
                    value={newIncome.amount}
                    onChange={handleIncomeChange}
                    className={styles.inputField}
                />
                <input
                    type="date"
                    name="transactionDate"
                    placeholder="Transaction Date"
                    value={newIncome.transactionDate}
                    onChange={handleIncomeChange}
                    className={styles.inputField}
                />
                <select name="incomeCategory" value={newIncome.incomeCategory.id} onChange={handleIncomeChange} className={styles.inputField}>
                    <option value="">Select Category</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.id}>{category.categoryName}</option>
                    ))}
                </select>
                <button onClick={addIncome} className={styles.addButton}>Add Income</button>
            </div>
            <h3 className={styles.addCategoryTitle}>Add a New Category</h3>
            <div className={styles.form}>
                <input
                    type="text"
                    placeholder="Category Name"
                    value={categoryName}
                    onChange={(e) => setCategoryName(e.target.value)}
                    className={styles.inputField}
                />
                <button onClick={addCategory} className={styles.addButton}>Add Category</button>
            </div>
        </div>
    );
}

export default IncomePage;
