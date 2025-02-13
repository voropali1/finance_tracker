package cz.cvut.fel.pm2.TransactionMicroservice.repository;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Override
    boolean existsById(Integer id);

    List<Transaction> findAllByUserId(int userId);
    @Query("SELECT e FROM Transaction e WHERE e.id = :transactionId AND e.userId = :userId")
    Optional<Transaction> findByIdAndUserId(@Param("transactionId") int expenseId, @Param("userId") int userId);

    @Query("SELECT e FROM Transaction e WHERE e.userId = :userId ORDER BY e.transactionDate DESC")
    List<Transaction> findAllByOrderByTransactionDateDesc(@Param("userId") int userId);

    @Query("SELECT e FROM Transaction e WHERE e.userId = :userId ORDER BY e.transactionDate ASC")
    List<Transaction> findAllByOrderByTransactionDateAsc(@Param("userId") int userId);

    @Query("SELECT e FROM Transaction e WHERE e.amount BETWEEN :fromAmount AND :toAmount ORDER BY e.amount ASC")
    List<Transaction> findByAmountBetweenOrderByAmountAsc(float fromAmount, float toAmount);
}

