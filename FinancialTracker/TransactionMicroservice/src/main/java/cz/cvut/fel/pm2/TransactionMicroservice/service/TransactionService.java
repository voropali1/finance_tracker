package cz.cvut.fel.pm2.TransactionMicroservice.service;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Transaction;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getTransactionById(int transactionId, int userId) {
        Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndUserId(transactionId, userId);
        return optionalTransaction.orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId + " for user: " + userId));
    }

    @Transactional
    public void createTransaction(Transaction transaction){
        Objects.requireNonNull(transaction);
        try {
            if (transactionRepository.existsById(transaction.getId())) {
                throw new IllegalArgumentException("Transaction with ID " + transaction.getId() + " already exists.");
            } else {
                transactionRepository.save(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getAllExpensesDescendingOrder(int userId) {
        return transactionRepository.findAllByOrderByTransactionDateDesc(userId);
    }

    public List<Transaction> getAllExpensesAscendingOrder(int userId) {
        return transactionRepository.findAllByOrderByTransactionDateAsc(userId);
    }

    @Transactional
    public Transaction updateTransaction(Transaction updatedTransaction, int userId) {
        Objects.requireNonNull(updatedTransaction);
        Transaction existingTransaction = getTransactionById(updatedTransaction.getId(), userId);
        transactionRepository.save(existingTransaction);
        return existingTransaction;
    }

    @Transactional
    public void deleteTransaction(int transactionId, int userId) {
        getTransactionById(transactionId, userId);
        transactionRepository.deleteById(transactionId);
    }

    public List<Transaction> getAllTransactions(int userId) {
        return transactionRepository.findAllByUserId(userId);
    }

    public List<Transaction> filterTransactionsByAmountRange(float fromAmount, float toAmount) {
        return transactionRepository.findByAmountBetweenOrderByAmountAsc(fromAmount, toAmount);
    }
}
