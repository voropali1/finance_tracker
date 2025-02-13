package cz.cvut.fel.pm2.TransactionMicroservice.controller;

import cz.cvut.fel.pm2.TransactionMicroservice.dto.ExpenseDTO;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.Expense;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.ExpenseCategory;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.ExpenseRepository;
import cz.cvut.fel.pm2.TransactionMicroservice.service.ExpenseService;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.ExpenseCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExpenseCategoryRepository expenseCategoryRepository;

    @MockBean
    private ExpenseRepository expenseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddExpense_Success() throws Exception {
        // Arrange
        int userId = 1;
        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setId(1L);
        expenseCategory.setCategoryName("Food");

        // Mock the repository to return the expenseCategory
        when(expenseCategoryRepository.findById(Math.toIntExact(expenseCategory.getId())))
                .thenReturn(Optional.of(expenseCategory));

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(100.0f);
        expenseDTO.setName("Groceries");
        expenseDTO.setTransactionDate(LocalDate.now());
        expenseDTO.setExpenseCategory(expenseCategory);

        // Mock the createExpense method
        doNothing().when(expenseService).createExpense(any(Expense.class));

        // Act & Assert
        mockMvc.perform(post("/transactions/expenses/add-expense")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    public void testAddExpense_InvalidDate() throws Exception {
        // Arrange
        int userId = 1;
        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setId(1L);
        expenseCategory.setCategoryName("Food");

        // Mock the repository to return the expenseCategory
        when(expenseCategoryRepository.findById(Math.toIntExact(expenseCategory.getId())))
                .thenReturn(Optional.of(expenseCategory));

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(100.0f);
        expenseDTO.setName("Groceries");
        // Invalid date before 2000
        expenseDTO.setTransactionDate(LocalDate.of(1999, 12, 31));
        expenseDTO.setExpenseCategory(expenseCategory);

        // Act & Assert
        mockMvc.perform(post("/transactions/expenses/add-expense")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction date must be between 2000 and the current date"));
    }

    @Test
    public void testUpdateExpense_Success() throws Exception {
        // Arrange
        int userId = 1;
        int expenseId = 1;

        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setId(1L);
        expenseCategory.setCategoryName("Food");

        Expense updatedExpense = new Expense();
        updatedExpense.setId(expenseId);
        updatedExpense.setAmount(150.0f);
        updatedExpense.setName("Updated Expense");
        updatedExpense.setTransactionDate(LocalDate.now());
        updatedExpense.setExpenseCategory(expenseCategory);
        updatedExpense.setUserId(userId);

        // Mock expenseRepository.existsById(id) to return true
        when(expenseRepository.existsById(expenseId)).thenReturn(true);

        // Mock the updateExpense method
        doNothing().when(expenseService).updateExpense(any(Expense.class), eq(userId));

        // Act & Assert
        mockMvc.perform(put("/transactions/expenses/{id}", expenseId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExpense)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testDeleteExpense_Success() throws Exception {
        // Arrange
        int userId = 1;
        int expenseId = 1;

        // Mock expenseRepository.existsById(id) to return true
        when(expenseRepository.existsById(expenseId)).thenReturn(true);

        // Mock the deleteExpense method
        doNothing().when(expenseService).deleteExpense(expenseId, userId);

        // Act & Assert
        mockMvc.perform(delete("/transactions/expenses/{id}", expenseId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void testDeleteExpense_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int expenseId = 1;

        // Mock expenseRepository.existsById(id) to return false
        when(expenseRepository.existsById(expenseId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/transactions/expenses/{id}", expenseId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense with id " + expenseId + " not found"));
    }

    @Test
    public void testGetAllExpensesDesc() throws Exception {
        // Arrange
        int userId = 1;

        ExpenseCategory expenseCategory = new ExpenseCategory();
        expenseCategory.setId(1L);
        expenseCategory.setCategoryName("Food");

        Expense expense1 = new Expense();
        expense1.setId(1);
        expense1.setAmount(100.0f);
        expense1.setName("Expense 1");
        expense1.setTransactionDate(LocalDate.now());
        expense1.setExpenseCategory(expenseCategory);
        expense1.setUserId(userId);

        Expense expense2 = new Expense();
        expense2.setId(2);
        expense2.setAmount(200.0f);
        expense2.setName("Expense 2");
        expense2.setTransactionDate(LocalDate.now());
        expense2.setExpenseCategory(expenseCategory);
        expense2.setUserId(userId);

        List<Expense> expenses = Arrays.asList(expense1, expense2);

        // Mock the service to return the expenses list
        when(expenseService.getAllExpensesDescendingOrder(userId)).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/transactions/expenses/all_expenses_desc")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expenses.size()))
                .andExpect(jsonPath("$[0].id").value(expense1.getId()))
                .andExpect(jsonPath("$[0].name").value(expense1.getName()))
                .andExpect(jsonPath("$[1].id").value(expense2.getId()))
                .andExpect(jsonPath("$[1].name").value(expense2.getName()));
    }
}
