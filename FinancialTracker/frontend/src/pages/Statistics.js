import React, { useState, useEffect } from 'react';
import { useAuth } from './AuthContext';
import { useNavigate } from 'react-router-dom';
import Header from './Header';
import styles from './Statistics.module.css';

// Import Recharts components
import {
    LineChart,
    BarChart,
    Bar,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from 'recharts';

function Statistics() {
    const { userId, isLoggedIn } = useAuth();
    const navigate = useNavigate();
    const [expenses, setExpenses] = useState([]);
    const [incomes, setIncomes] = useState([]);
    const [chartData, setChartData] = useState([]);

    useEffect(() => {
        if (!isLoggedIn) {
            return;
        }
        fetchData();
    }, [isLoggedIn]);

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

    const fetchData = async () => {
        try {
            const [expensesResponse, incomesResponse] = await Promise.all([
                fetch(`http://localhost:8080/transactions/expenses/all_expenses_desc?userId=${userId}`),
                fetch(`http://localhost:8080/transactions/incomes/all_incomes_desc?userId=${userId}`),
            ]);

            if (!expensesResponse.ok || !incomesResponse.ok) {
                throw new Error('Failed to fetch data');
            }

            const [expensesData, incomesData] = await Promise.all([
                expensesResponse.json(),
                incomesResponse.json(),
            ]);

            setExpenses(expensesData);
            setIncomes(incomesData);

            processChartData(expensesData, incomesData);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    const processChartData = (expensesData, incomesData) => {
        // Combine incomes and expenses by month
        const dataMap = {};

        expensesData.forEach((expense) => {
            const date = new Date(expense.transactionDate);
            const month = `${date.getFullYear()}-${(date.getMonth() + 1)
                .toString()
                .padStart(2, '0')}`;

            if (!dataMap[month]) {
                dataMap[month] = { month, income: 0, expense: 0 };
            }
            dataMap[month].expense += expense.amount;
        });

        incomesData.forEach((income) => {
            const date = new Date(income.transactionDate);
            const month = `${date.getFullYear()}-${(date.getMonth() + 1)
                .toString()
                .padStart(2, '0')}`;

            if (!dataMap[month]) {
                dataMap[month] = { month, income: 0, expense: 0 };
            }
            dataMap[month].income += income.amount;
        });

        // Convert dataMap to array and sort by month
        const chartDataArray = Object.values(dataMap).sort(
            (a, b) => new Date(a.month) - new Date(b.month)
        );

        setChartData(chartDataArray);
    };

    return (
        <div className={styles.container}>
            <Header />
            <h2 className={styles.pageTitle}>Statistics</h2>
            <div className={styles.chartContainer}>
                <ResponsiveContainer width="100%" height={400}>
                    <BarChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="month" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="income" fill="#82ca9d" />
                        <Bar dataKey="expense" fill="#FF0000" />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}

export default Statistics;
