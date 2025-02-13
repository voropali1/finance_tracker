package cz.cvut.fel.pm2.TransactionMicroservice.controller;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Transaction;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.Expense;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.Income;
import cz.cvut.fel.pm2.TransactionMicroservice.service.TransactionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetTransactionById_Success() throws Exception {
        // Arrange
        int userId = 1;
        int transactionId = 1;

        // Use an instance of Expense or Income
        Expense transaction = new Expense();
        transaction.setId(transactionId);
        transaction.setAmount(500.0f);
        transaction.setName("Test Transaction");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setUserId(userId);

        // Mock the service method
        when(transactionService.getTransactionById(transactionId, userId)).thenReturn(transaction);

        // Act & Assert
        mockMvc.perform(get("/transactions/{id}", transactionId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.name").value("Test Transaction"));
    }

    @Test
    public void testGetTransactionById_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int transactionId = 1;

        // Mock the service method to throw an exception
        when(transactionService.getTransactionById(transactionId, userId))
                .thenThrow(new RuntimeException("Transaction not found with id: " + transactionId + " for user: " + userId));

        // Act & Assert
        mockMvc.perform(get("/transactions/{id}", transactionId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found with id: " + transactionId + " for user: " + userId));
    }

    @Test
    public void testGetAllTransactionsDesc() throws Exception {
        // Arrange
        int userId = 1;

        // Create instances of Expense and Income
        Expense transaction1 = new Expense();
        transaction1.setId(1);
        transaction1.setAmount(100.0f);
        transaction1.setName("Expense 1");
        transaction1.setTransactionDate(LocalDate.now());
        transaction1.setUserId(userId);

        Income transaction2 = new Income();
        transaction2.setId(2);
        transaction2.setAmount(200.0f);
        transaction2.setName("Income 1");
        transaction2.setTransactionDate(LocalDate.now());
        transaction2.setUserId(userId);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        // Mock the service method
        when(transactionService.getAllExpensesDescendingOrder(userId)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transactions/all_transactions_desc")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactions.size()))
                .andExpect(jsonPath("$[0].id").value(transaction1.getId()))
                .andExpect(jsonPath("$[0].name").value(transaction1.getName()))
                .andExpect(jsonPath("$[1].id").value(transaction2.getId()))
                .andExpect(jsonPath("$[1].name").value(transaction2.getName()));
    }

    @Test
    public void testGetAllTransactionsAsc() throws Exception {
        // Arrange
        int userId = 1;

        Expense transaction1 = new Expense();
        transaction1.setId(1);
        transaction1.setAmount(100.0f);
        transaction1.setName("Expense 1");
        transaction1.setTransactionDate(LocalDate.now());
        transaction1.setUserId(userId);

        Income transaction2 = new Income();
        transaction2.setId(2);
        transaction2.setAmount(200.0f);
        transaction2.setName("Income 1");
        transaction2.setTransactionDate(LocalDate.now());
        transaction2.setUserId(userId);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        // Mock the service method
        when(transactionService.getAllExpensesAscendingOrder(userId)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transactions/all_transactions_asc")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactions.size()))
                .andExpect(jsonPath("$[0].id").value(transaction1.getId()))
                .andExpect(jsonPath("$[0].name").value(transaction1.getName()))
                .andExpect(jsonPath("$[1].id").value(transaction2.getId()))
                .andExpect(jsonPath("$[1].name").value(transaction2.getName()));
    }

    @Test
    public void testGetAllTransactions() throws Exception {
        // Arrange
        int userId = 1;

        Expense transaction1 = new Expense();
        transaction1.setId(1);
        transaction1.setAmount(100.0f);
        transaction1.setName("Expense 1");
        transaction1.setTransactionDate(LocalDate.now());
        transaction1.setUserId(userId);

        Income transaction2 = new Income();
        transaction2.setId(2);
        transaction2.setAmount(200.0f);
        transaction2.setName("Income 1");
        transaction2.setTransactionDate(LocalDate.now());
        transaction2.setUserId(userId);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        // Mock the service method
        when(transactionService.getAllTransactions(userId)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transactions/all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactions.size()))
                .andExpect(jsonPath("$[0].id").value(transaction1.getId()))
                .andExpect(jsonPath("$[0].name").value(transaction1.getName()))
                .andExpect(jsonPath("$[1].id").value(transaction2.getId()))
                .andExpect(jsonPath("$[1].name").value(transaction2.getName()));
    }

    @Test
    public void testFilterTransactionsByAmountRange_Success() throws Exception {
        // Arrange
        float fromAmount = 50.0f;
        float toAmount = 150.0f;

        Expense transaction1 = new Expense();
        transaction1.setId(1);
        transaction1.setAmount(100.0f);
        transaction1.setName("Expense 1");
        transaction1.setTransactionDate(LocalDate.now());

        Income transaction2 = new Income();
        transaction2.setId(2);
        transaction2.setAmount(150.0f);
        transaction2.setName("Income 1");
        transaction2.setTransactionDate(LocalDate.now());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        // Mock the service method
        when(transactionService.filterTransactionsByAmountRange(fromAmount, toAmount)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transactions/filter-by-amount")
                        .param("from", String.valueOf(fromAmount))
                        .param("to", String.valueOf(toAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactions.size()))
                .andExpect(jsonPath("$[0].amount").value(transaction1.getAmount()))
                .andExpect(jsonPath("$[1].amount").value(transaction2.getAmount()));
    }

    @Test
    public void testFilterTransactionsByAmountRange_InvalidRange() throws Exception {
        // Arrange
        float fromAmount = 150.0f;
        float toAmount = 50.0f;

        // Act & Assert
        mockMvc.perform(get("/transactions/filter-by-amount")
                        .param("from", String.valueOf(fromAmount))
                        .param("to", String.valueOf(toAmount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("[]")); // Since the controller returns an empty list
    }
}
