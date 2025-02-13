package cz.cvut.fel.pm2.TransactionMicroservice.repository;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Income;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Integer> {
    boolean existsById(Long id);
    List<Income> findByIncomeCategory(IncomeCategory incomeCategory);

    @Query("SELECT e FROM Income e WHERE e.id = :incomeId AND e.userId = :userId")
    Optional<Income> findByIdAndUserId(@Param("incomeId") int expenseId, @Param("userId") int userId);

    @Query("SELECT e FROM Income e WHERE e.userId = :userId ORDER BY e.transactionDate DESC")
    List<Income> findAllByOrderByTransactionDateDesc(@Param("userId") int userId);

    @Query("SELECT i FROM Income i WHERE i.incomeCategory = :incomeCategory AND i.userId = :userId")
    List<Income> findByIncomeCategoryAndUserId(@Param("incomeCategory") IncomeCategory incomeCategory, @Param("userId") int userId);

    @Query("SELECT e FROM Income e WHERE e.userId = :userId ORDER BY e.transactionDate ASC")
    List<Income> findAllByOrderByTransactionDateAsc(@Param("userId") int userId);

    @Query("SELECT e FROM Income e WHERE e.userId = :userId AND e.amount BETWEEN :fromAmount AND :toAmount ORDER BY e.amount ASC")
    List<Income> findByAmountBetweenOrderByAmountAsc(@Param("userId") int userId, @Param("fromAmount") float fromAmount, @Param("toAmount") float toAmount);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.amount >= :fromAmount ORDER BY i.amount ASC")
    List<Income> findByAmountGreaterThanEqualOrderByAmountAsc(@Param("userId") int userId, @Param("fromAmount") float fromAmount);
}

