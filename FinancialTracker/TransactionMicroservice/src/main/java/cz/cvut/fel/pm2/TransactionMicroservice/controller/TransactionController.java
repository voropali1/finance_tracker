package cz.cvut.fel.pm2.TransactionMicroservice.controller;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Transaction;
import cz.cvut.fel.pm2.TransactionMicroservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    /**
     * Retrieves a transaction by its ID and user ID.
     *
     * @param id the ID of the transaction
     * @param userId the ID of the user
     * @return the transaction with the given ID and user ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable("id") int id,  @RequestParam int userId) {
        try {
            Transaction transaction = transactionService.getTransactionById(id, userId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    /**
     * Retrieves all transactions in descending order by transaction date for a user.
     *
     * @param userId the ID of the user
     * @return a list of transactions in descending order by transaction date
     */
    @GetMapping("/all_transactions_desc")
    public ResponseEntity<List<Transaction>> getAllTransactionsDesc(@RequestParam int userId) {
        List<Transaction> transactions = transactionService.getAllExpensesDescendingOrder(userId);
        return ResponseEntity.ok().body(transactions);
    }

    /**
     * Retrieves all transactions in ascending order by transaction date for a user.
     *
     * @param userId the ID of the user
     * @return a list of transactions in ascending order by transaction date
     */
    @GetMapping("/all_transactions_asc")
    public ResponseEntity<List<Transaction>> getAllTransactionsAsc(@RequestParam int userId) {
        List<Transaction> transactions = transactionService.getAllExpensesAscendingOrder(userId);
        return ResponseEntity.ok().body(transactions);
    }



    /**
     * Retrieves all transactions for a user.
     *
     * @param userId the ID of the user
     * @return a list of all transactions for the user
     */
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestParam int userId) {
        List<Transaction> transactions = transactionService.getAllTransactions(userId);
        return ResponseEntity.ok().body(transactions);
    }

    /**
     * Filters transactions by amount range.
     *
     * @param fromAmount the minimum amount
     * @param toAmount the maximum amount
     * @return a list of transactions within the specified amount range
     */
    @GetMapping("/filter-by-amount")
    public ResponseEntity<List<Transaction>> filterTransactionsByAmountRange(@RequestParam("from") float fromAmount,
                                                                             @RequestParam("to") float toAmount) {
        if (fromAmount > toAmount) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Transaction> filteredTransactions = transactionService.filterTransactionsByAmountRange(fromAmount, toAmount);

        return ResponseEntity.ok().body(filteredTransactions);
    }
}