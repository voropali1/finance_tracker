package cz.cvut.fel.pm2.TransactionMicroservice.service;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Expense;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.ExpenseCategory;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.ExpenseCategoryRepository;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ExpenseService  {
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;


    public ExpenseService(ExpenseRepository expenseRepository, ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }


    public Expense getExpenseById(int expenseId, int userId) {
        Optional<Expense> optionalExpense = expenseRepository.findByIdAndUserId(expenseId, userId);
        return optionalExpense.orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId + " for user: " + userId));
    }


    @Transactional
    public void updateExpense(Expense updatedExpense, int userId) {
        Objects.requireNonNull(updatedExpense);
        Expense existingExpense = getExpenseById(updatedExpense.getId(), userId);
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setName(updatedExpense.getName());
        existingExpense.setTransactionDate(updatedExpense.getTransactionDate());
        existingExpense.setExpenseCategory(updatedExpense.getExpenseCategory());
        expenseRepository.save(existingExpense);
    }


    @Transactional
    public void createExpense(Expense expense){
        Objects.requireNonNull(expense);
        expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(int expenseId, int userId) {
        getExpenseById(expenseId, userId);
        expenseRepository.deleteById(expenseId);
    }


    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }


    public void addExpenseCategory(String categoryName) {
        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setCategoryName(categoryName);
        expenseCategoryRepository.save(expenseCategory);
    }


    @Transactional(readOnly = true)
    public List<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryRepository.findAll();
    }

    public List<Expense> getExpensesByExpenseCategory(ExpenseCategory expenseCategory, int userId) {
        return expenseRepository.findByExpenseCategoryAndUserId(expenseCategory, userId);
    }

    public List<Expense> getAllExpensesDescendingOrder(int userId) {
        return expenseRepository.findAllByOrderByTransactionDateDesc(userId);
    }


    public List<Expense> getAllExpensesAscendingOrder(int userId) {
        return expenseRepository.findAllByOrderByTransactionDateAsc(userId);
    }
    public List<Expense> filterExpensesByAmountRange(int userId, float fromAmount, float toAmount) {
        return expenseRepository.findByAmountBetweenOrderByAmountAsc(userId, fromAmount, toAmount);
    }


    @Transactional(readOnly = true)
    public List<Expense> getExpensesByExpenseCategoryAndUserId(ExpenseCategory expenseCategory, int userId) {
        return expenseRepository.findByExpenseCategoryAndUserId(expenseCategory, userId);
    }

    public List<Expense> filterExpensesByAmountStartingFrom(int userId, float fromAmount) {
        return expenseRepository.findByAmountGreaterThanEqualOrderByAmountAsc(userId, fromAmount);
    }

}

