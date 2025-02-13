package cz.cvut.fel.pm2.FinanceMicroservice.controller;

import cz.cvut.fel.pm2.FinanceMicroservice.entity.Debt;
import cz.cvut.fel.pm2.FinanceMicroservice.entity.Goal;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.FinanceRepository;
import cz.cvut.fel.pm2.FinanceMicroservice.service.FinanceService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinanceController.class)
public class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceService financeService;

    @MockBean
    private FinanceRepository financeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Debt debt;
    private Goal goal;

    @BeforeEach
    public void setup() {
        debt = new Debt();
        debt.setId(1);
        debt.setUserId(1);
        debt.setName("Personal Loan");
        debt.setAmount(1000.0f);
        debt.setNameOfPersonToGiveBack("John Doe");
        debt.setFromDate(LocalDate.now());
        debt.setDueDate(LocalDate.now().plusMonths(6));
        debt.setInterestRate(5);

        goal = new Goal();
        goal.setId(2);
        goal.setUserId(1);
        goal.setName("Vacation Fund");
        goal.setAmount(5000.0f);
        // If Goal has additional fields, set them here
    }

    @Test
    public void testGetFinanceById_ReturnsDebt() throws Exception {
        // Arrange
        int financeId = debt.getId();

        when(financeService.getFinanceById(financeId)).thenReturn(debt);

        // Act & Assert
        mockMvc.perform(get("/finances/{id}", financeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(debt.getId()))
                .andExpect(jsonPath("$.userId").value(debt.getUserId()))
                .andExpect(jsonPath("$.name").value(debt.getName()))
                .andExpect(jsonPath("$.amount").value(debt.getAmount()))
                .andExpect(jsonPath("$.nameOfPersonToGiveBack").value(debt.getNameOfPersonToGiveBack()))
                .andExpect(jsonPath("$.fromDate").value(debt.getFromDate().toString()))
                .andExpect(jsonPath("$.dueDate").value(debt.getDueDate().toString()))
                .andExpect(jsonPath("$.interestRate").value(debt.getInterestRate()));
    }

    @Test
    public void testGetFinanceById_ReturnsGoal() throws Exception {
        // Arrange
        int financeId = goal.getId();

        when(financeService.getFinanceById(financeId)).thenReturn(goal);

        // Act & Assert
        mockMvc.perform(get("/finances/{id}", financeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goal.getId()))
                .andExpect(jsonPath("$.userId").value(goal.getUserId()))
                .andExpect(jsonPath("$.name").value(goal.getName()))
                .andExpect(jsonPath("$.amount").value(goal.getAmount()));
    }

    @Test
    public void testGetFinanceById_NotFound() throws Exception {
        // Arrange
        int financeId = 999;

        when(financeService.getFinanceById(financeId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/finances/{id}", financeId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Finance with id " + financeId + " not found"));
    }

    @Test
    public void testGetFinanceById_InternalServerError() throws Exception {
        // Arrange
        int financeId = 1;

        when(financeService.getFinanceById(financeId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/finances/{id}", financeId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error occurred"));
    }
}
