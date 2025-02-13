package cz.cvut.fel.pm2.FinanceMicroservice.controller;

import cz.cvut.fel.pm2.FinanceMicroservice.dto.DebtDTO;
import cz.cvut.fel.pm2.FinanceMicroservice.entity.Debt;
import cz.cvut.fel.pm2.FinanceMicroservice.interest.InterestCalculator;
import cz.cvut.fel.pm2.FinanceMicroservice.interest.CompoundInterestCalculationStrategy;
import cz.cvut.fel.pm2.FinanceMicroservice.interest.SimpleInterestCalculationStrategy;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.DebtRepository;
import cz.cvut.fel.pm2.FinanceMicroservice.service.DebtService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DebtController.class)
public class DebtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DebtService debtService;

    @MockBean
    private DebtRepository debtRepository;

    @MockBean
    private InterestCalculator interestCalculator;

    @Autowired
    private ObjectMapper objectMapper;

    private Debt debt;

    @BeforeEach
    public void setup() {
        debt = new Debt();
        debt.setId(1);
        debt.setUserId(1);
        debt.setAmount(1000.0f);
        debt.setName("Personal Loan");
        debt.setNameOfPersonToGiveBack("John Doe");
        debt.setFromDate(LocalDate.now());
        debt.setDueDate(LocalDate.now().plusMonths(6));
        debt.setInterestRate(5);
    }

    @Test
    public void testGetDebtById_Success() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtService.getDebtById(debtId, userId)).thenReturn(debt);

        // Act & Assert
        mockMvc.perform(get("/finances/debts/{id}", debtId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(debtId))
                .andExpect(jsonPath("$.name").value("Personal Loan"));
    }

    @Test
    public void testGetDebtById_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtService.getDebtById(debtId, userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/finances/debts/{id}", debtId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Debt with id " + debtId + " not found"));
    }

    @Test
    public void testGetAllDebtById_Success() throws Exception {
        // Arrange
        int userId = 1;

        Debt debt2 = new Debt();
        debt2.setId(2);
        debt2.setUserId(userId);
        debt2.setAmount(2000.0f);
        debt2.setName("Car Loan");
        debt2.setNameOfPersonToGiveBack("Jane Smith");
        debt2.setFromDate(LocalDate.now());
        debt2.setDueDate(LocalDate.now().plusMonths(12));
        debt2.setInterestRate(7);

        List<Debt> debts = Arrays.asList(debt, debt2);

        when(debtService.getAllById(userId)).thenReturn(debts);

        // Act & Assert
        mockMvc.perform(get("/finances/debts/all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(debts.size()))
                .andExpect(jsonPath("$[0].id").value(debt.getId()))
                .andExpect(jsonPath("$[0].name").value(debt.getName()))
                .andExpect(jsonPath("$[1].id").value(debt2.getId()))
                .andExpect(jsonPath("$[1].name").value(debt2.getName()));
    }

    @Test
    public void testGetAllDebtById_NotFound() throws Exception {
        // Arrange
        int userId = 1;

        when(debtService.getAllById(userId)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/finances/debts/all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Debts was not fount"));
    }

    @Test
    public void testGetInterestRate_Success() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        long daysBetween = ChronoUnit.DAYS.between(debt.getFromDate(), debt.getDueDate());
        float expectedInterest = 50.0f;

        when(debtService.getDebtById(debtId, userId)).thenReturn(debt);
        when(interestCalculator.calculateInterest(debt.getAmount(), daysBetween, debt.getInterestRate()))
                .thenReturn(expectedInterest);

        // Act & Assert
        mockMvc.perform(get("/finances/debts/{id}/interest-rate", debtId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedInterest)));
    }

    @Test
    public void testGetInterestRate_DebtNotFound() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtService.getDebtById(debtId, userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/finances/debts/{id}/interest-rate", debtId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Debt with id " + debtId + " not found"));
    }

    @Test
    public void testSetInterestCalculationStrategy_Compound() throws Exception {
        // Arrange
        int userId = 1;
        int strategyNumber = 1;

        // Act
        mockMvc.perform(post("/finances/debts/{id}/set-calculation-strategy", debt.getId())
                        .param("userId", String.valueOf(userId))
                        .param("numberOfStrategy", String.valueOf(strategyNumber)))
                .andExpect(status().isOk());

        // Assert
        verify(interestCalculator).setStrategy(any(CompoundInterestCalculationStrategy.class));
    }

    @Test
    public void testSetInterestCalculationStrategy_Simple() throws Exception {
        // Arrange
        int userId = 1;
        int strategyNumber = 2;

        // Act
        mockMvc.perform(post("/finances/debts/{id}/set-calculation-strategy", debt.getId())
                        .param("userId", String.valueOf(userId))
                        .param("numberOfStrategy", String.valueOf(strategyNumber)))
                .andExpect(status().isOk());

        // Assert
        verify(interestCalculator).setStrategy(any(SimpleInterestCalculationStrategy.class));
    }

    @Test
    public void testAddDebt_Success() throws Exception {
        // Arrange
        int userId = 1;

        DebtDTO debtDTO = new DebtDTO();
        debtDTO.setAmount(1000.0f);
        debtDTO.setName("New Loan");
        debtDTO.setNameOfPersonToGiveBack("Alice");
        debtDTO.setDueDate(LocalDate.now().plusMonths(6));
        debtDTO.setInterestRate(5);

        // Act & Assert
        mockMvc.perform(post("/finances/debts/add-debt")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debtDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        // Verify that the service was called
        verify(debtService).createDebt(any(Debt.class));
    }

    @Test
    public void testAddDebt_InvalidDueDate() throws Exception {
        // Arrange
        int userId = 1;

        DebtDTO debtDTO = new DebtDTO();
        debtDTO.setAmount(1000.0f);
        debtDTO.setName("New Loan");
        debtDTO.setNameOfPersonToGiveBack("Alice");
        // Due date before current date
        debtDTO.setDueDate(LocalDate.now().minusDays(1));
        debtDTO.setInterestRate(5);

        // Act & Assert
        mockMvc.perform(post("/finances/debts/add-debt")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debtDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Debt can not be before the current date"));

        // Verify that the service was not called
        verify(debtService, never()).createDebt(any(Debt.class));
    }

    @Test
    public void testUpdateDebt_Success() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtRepository.existsById(debtId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/finances/debts/{id}", debtId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debt)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify that the service was called
        verify(debtService).updateDebt(any(Debt.class), eq(userId));
    }

    @Test
    public void testUpdateDebt_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtRepository.existsById(debtId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put("/finances/debts/{id}", debtId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debt)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Debt with id " + debtId + " not found."));

        // Verify that the service was not called
        verify(debtService, never()).updateDebt(any(Debt.class), eq(userId));
    }

    @Test
    public void testDeleteDebt_Success() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtRepository.existsById(debtId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/finances/debts/{id}", debtId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // Verify that the service was called
        verify(debtService).deleteDebt(debtId, userId);
    }

    @Test
    public void testDeleteDebt_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int debtId = 1;

        when(debtRepository.existsById(debtId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/finances/debts/{id}", debtId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Debt with id " + debtId + " not found"));

        // Verify that the service was not called
        verify(debtService, never()).deleteDebt(debtId, userId);
    }
}
