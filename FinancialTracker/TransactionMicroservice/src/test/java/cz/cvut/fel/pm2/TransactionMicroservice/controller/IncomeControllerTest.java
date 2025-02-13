package cz.cvut.fel.pm2.TransactionMicroservice.controller;

import cz.cvut.fel.pm2.TransactionMicroservice.dto.IncomeDTO;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.Income;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.IncomeCategory;
import cz.cvut.fel.pm2.TransactionMicroservice.service.IncomeService;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.IncomeCategoryRepository;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.IncomeRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

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

@WebMvcTest(IncomeController.class)
public class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncomeService incomeService;

    @MockBean
    private IncomeCategoryRepository incomeCategoryRepository;

    @MockBean
    private IncomeRepository incomeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddIncome_Success() throws Exception {
        // Arrange
        int userId = 1;
        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(1L);
        incomeCategory.setCategoryName("Salary");

        // Mock the repository to return the incomeCategory
        when(incomeCategoryRepository.findById(Math.toIntExact(incomeCategory.getId())))
                .thenReturn(Optional.of(incomeCategory));

        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setAmount(1000.0f);
        incomeDTO.setName("Monthly Salary");
        incomeDTO.setTransactionDate(LocalDate.now());
        incomeDTO.setIncomeCategory(incomeCategory);

        // Mock the createIncome method
        doNothing().when(incomeService).createIncome(any(Income.class));

        // Act & Assert
        mockMvc.perform(post("/transactions/incomes/add-income")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    public void testAddIncome_InvalidDate() throws Exception {
        // Arrange
        int userId = 1;
        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(1L);
        incomeCategory.setCategoryName("Salary");

        // Mock the repository to return the incomeCategory
        when(incomeCategoryRepository.findById(Math.toIntExact(incomeCategory.getId())))
                .thenReturn(Optional.of(incomeCategory));

        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setAmount(1000.0f);
        incomeDTO.setName("Monthly Salary");
        // Invalid date before 2000
        incomeDTO.setTransactionDate(LocalDate.of(1999, 12, 31));
        incomeDTO.setIncomeCategory(incomeCategory);

        // Act & Assert
        mockMvc.perform(post("/transactions/incomes/add-income")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Transaction date must be between 2000 and the current date"));
    }

    @Test
    public void testUpdateIncome_Success() throws Exception {
        // Arrange
        int userId = 1;
        int incomeId = 1;

        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(1L);
        incomeCategory.setCategoryName("Salary");

        Income updatedIncome = new Income();
        updatedIncome.setId(incomeId);
        updatedIncome.setAmount(1500.0f);
        updatedIncome.setName("Updated Salary");
        updatedIncome.setTransactionDate(LocalDate.now());
        updatedIncome.setIncomeCategory(incomeCategory);
        updatedIncome.setUserId(userId);

        // Mock incomeRepository.existsById(id) to return true
        when(incomeRepository.existsById(incomeId)).thenReturn(true);

        // Mock the updateIncome method
        doNothing().when(incomeService).updateIncome(any(Income.class), eq(userId));

        // Act & Assert
        mockMvc.perform(put("/transactions/incomes/{id}", incomeId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedIncome)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testDeleteIncome_Success() throws Exception {
        // Arrange
        int userId = 1;
        int incomeId = 1;

        // Mock incomeRepository.existsById(id) to return true
        when(incomeRepository.existsById(incomeId)).thenReturn(true);

        // Mock the deleteIncome method
        doNothing().when(incomeService).deleteIncome(incomeId, userId);

        // Act & Assert
        mockMvc.perform(delete("/transactions/incomes/{id}", incomeId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void testDeleteIncome_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int incomeId = 1;

        // Mock incomeRepository.existsById(id) to return false
        when(incomeRepository.existsById(incomeId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/transactions/incomes/{id}", incomeId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Income with id " + incomeId + " not found"));
    }

    @Test
    public void testGetIncomeById_Success() throws Exception {
        // Arrange
        int userId = 1;
        int incomeId = 1;

        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(1L);
        incomeCategory.setCategoryName("Salary");

        Income income = new Income();
        income.setId(incomeId);
        income.setAmount(1000.0f);
        income.setName("Monthly Salary");
        income.setTransactionDate(LocalDate.now());
        income.setIncomeCategory(incomeCategory);
        income.setUserId(userId);

        // Mock the service method
        when(incomeService.getIncomeById(incomeId, userId)).thenReturn(income);

        // Act & Assert
        mockMvc.perform(get("/transactions/incomes/{id}", incomeId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incomeId))
                .andExpect(jsonPath("$.name").value("Monthly Salary"));
    }

    @Test
    public void testGetIncomeById_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int incomeId = 1;

        // Mock the service method to return null
        when(incomeService.getIncomeById(incomeId, userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/transactions/incomes/{id}", incomeId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Income with id " + incomeId + " not found"));
    }

    @Test
    public void testGetAllIncomesDesc() throws Exception {
        // Arrange
        int userId = 1;

        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(1L);
        incomeCategory.setCategoryName("Salary");

        Income income1 = new Income();
        income1.setId(1);
        income1.setAmount(1000.0f);
        income1.setName("Income 1");
        income1.setTransactionDate(LocalDate.now());
        income1.setIncomeCategory(incomeCategory);
        income1.setUserId(userId);

        Income income2 = new Income();
        income2.setId(2);
        income2.setAmount(2000.0f);
        income2.setName("Income 2");
        income2.setTransactionDate(LocalDate.now());
        income2.setIncomeCategory(incomeCategory);
        income2.setUserId(userId);

        List<Income> incomes = Arrays.asList(income1, income2);

        // Mock the service method
        when(incomeService.getAllExpensesDescendingOrder(userId)).thenReturn(incomes);

        // Act & Assert
        mockMvc.perform(get("/transactions/incomes/all_incomes_desc")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(incomes.size()))
                .andExpect(jsonPath("$[0].id").value(income1.getId()))
                .andExpect(jsonPath("$[0].name").value(income1.getName()))
                .andExpect(jsonPath("$[1].id").value(income2.getId()))
                .andExpect(jsonPath("$[1].name").value(income2.getName()));
    }

    @Test
    public void testFilterIncomesByAmountRange() throws Exception {
        // Arrange
        int userId = 1;
        float fromAmount = 500.0f;
        float toAmount = 1500.0f;

        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(1L);
        incomeCategory.setCategoryName("Salary");

        Income income1 = new Income();
        income1.setId(1);
        income1.setAmount(1000.0f);
        income1.setName("Income 1");
        income1.setTransactionDate(LocalDate.now());
        income1.setIncomeCategory(incomeCategory);
        income1.setUserId(userId);

        Income income2 = new Income();
        income2.setId(2);
        income2.setAmount(1200.0f);
        income2.setName("Income 2");
        income2.setTransactionDate(LocalDate.now());
        income2.setIncomeCategory(incomeCategory);
        income2.setUserId(userId);

        List<Income> incomes = Arrays.asList(income1, income2);

        // Mock the service method
        when(incomeService.filterIncomesByAmountRange(userId, fromAmount, toAmount)).thenReturn(incomes);

        // Act & Assert
        mockMvc.perform(get("/transactions/incomes/filter-by-amount")
                        .param("from", String.valueOf(fromAmount))
                        .param("to", String.valueOf(toAmount))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(incomes.size()))
                .andExpect(jsonPath("$[0].amount").value(income1.getAmount()))
                .andExpect(jsonPath("$[1].amount").value(income2.getAmount()));
    }

    @Test
    public void testFilterIncomesByAmountRange_InvalidRange() throws Exception {
        // Arrange
        int userId = 1;
        float fromAmount = 1500.0f;
        float toAmount = 500.0f;

        // Act & Assert
        mockMvc.perform(get("/transactions/incomes/filter-by-amount")
                        .param("from", String.valueOf(fromAmount))
                        .param("to", String.valueOf(toAmount))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("[]")); // Since the controller returns an empty list
    }

    @Test
    public void testAddIncomeCategory_Success() throws Exception {
        // Arrange
        String categoryName = "Investment";
        doNothing().when(incomeService).addIncomeCategory(categoryName);

        // Act & Assert
        mockMvc.perform(post("/transactions/incomes/add-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"" + categoryName + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    public void testGetAllIncomeCategories() throws Exception {
        // Arrange
        IncomeCategory category1 = new IncomeCategory();
        category1.setId(1L);
        category1.setCategoryName("Salary");

        IncomeCategory category2 = new IncomeCategory();
        category2.setId(2L);
        category2.setCategoryName("Investment");

        List<IncomeCategory> categories = Arrays.asList(category1, category2);

        // Mock the service method
        when(incomeService.getAllIncomeCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/transactions/incomes/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(categories.size()))
                .andExpect(jsonPath("$[0].categoryName").value("Salary"))
                .andExpect(jsonPath("$[1].categoryName").value("Investment"));
    }
}
