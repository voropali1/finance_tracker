package cz.cvut.fel.pm2.TransactionMicroservice.repository;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Expense;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByExpenseCategory(ExpenseCategory expenseCategory);

    @Query("SELECT e FROM Expense e WHERE e.id = :expenseId AND e.userId = :userId")
    Optional<Expense> findByIdAndUserId(@Param("expenseId") int expenseId, @Param("userId") int userId);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId ORDER BY e.transactionDate DESC")
    List<Expense> findAllByOrderByTransactionDateDesc(@Param("userId") int userId);

    @Query("SELECT i FROM Expense i WHERE i.expenseCategory = :expenseCategory AND i.userId = :userId")
    List<Expense> findByExpenseCategoryAndUserId(@Param("expenseCategory") ExpenseCategory expenseCategory, @Param("userId") int userId);


    @Query("SELECT e FROM Expense e WHERE e.userId = :userId ORDER BY e.transactionDate ASC")
    List<Expense> findAllByOrderByTransactionDateAsc(@Param("userId") int userId);

    @Query("SELECT e FROM Expense e WHERE e.amount BETWEEN :fromAmount AND :toAmount ORDER BY e.amount ASC")
    List<Expense> findByAmountBetweenOrderByAmountAsc(float fromAmount, float toAmount);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.amount BETWEEN :fromAmount AND :toAmount ORDER BY e.amount ASC")
    List<Expense> findByAmountBetweenOrderByAmountAsc(@Param("userId") int userId, @Param("fromAmount") float fromAmount, @Param("toAmount") float toAmount);


    @Query("SELECT i FROM Expense i WHERE i.userId = :userId AND i.amount >= :fromAmount ORDER BY i.amount ASC")
    List<Expense> findByAmountGreaterThanEqualOrderByAmountAsc(@Param("userId") int userId, @Param("fromAmount") float fromAmount);
}

