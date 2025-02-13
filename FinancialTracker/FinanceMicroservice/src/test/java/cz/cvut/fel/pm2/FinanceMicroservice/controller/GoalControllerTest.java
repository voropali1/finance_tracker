package cz.cvut.fel.pm2.FinanceMicroservice.controller;

import cz.cvut.fel.pm2.FinanceMicroservice.dto.GoalDTO;
import cz.cvut.fel.pm2.FinanceMicroservice.entity.Goal;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.GoalRepository;
import cz.cvut.fel.pm2.FinanceMicroservice.service.GoalService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
public class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private GoalRepository goalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Goal goal;

    @BeforeEach
    public void setup() {
        goal = new Goal();
        goal.setId(1);
        goal.setUserId(1);
        goal.setName("Vacation Fund");
        goal.setAmount(5000.0f);
    }

    @Test
    public void testGetGoalById_Success() throws Exception {
        // Arrange
        int userId = 1;
        int goalId = goal.getId();

        when(goalService.getGoalById(goalId, userId)).thenReturn(goal);

        // Act & Assert
        mockMvc.perform(get("/finances/goals/{id}", goalId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goal.getId()))
                .andExpect(jsonPath("$.userId").value(goal.getUserId()))
                .andExpect(jsonPath("$.name").value(goal.getName()))
                .andExpect(jsonPath("$.amount").value(goal.getAmount()));
    }

    @Test
    public void testGetGoalById_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int goalId = 1;

        when(goalService.getGoalById(goalId, userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/finances/goals/{id}", goalId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Goal with id " + goalId + " not found"));
    }

    @Test
    public void testAddGoal_Success() throws Exception {
        // Arrange
        int userId = 1;

        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setAmount(5000.0f);
        goalDTO.setName("New Goal");
        // Set additional fields if any

        // Act & Assert
        mockMvc.perform(post("/finances/goals/add-goal")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        // Verify that the service was called
        verify(goalService).createGoal(any(Goal.class));
    }

    @Test
    public void testAddGoal_InvalidData() throws Exception {
        // Arrange
        int userId = 1;

        GoalDTO goalDTO = new GoalDTO();
        // Missing required fields like name
        goalDTO.setAmount(5000.0f);

        // Act & Assert
        mockMvc.perform(post("/finances/goals/add-goal")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalDTO)))
                .andExpect(status().isBadRequest());

        // Verify that the service was not called
        verify(goalService, never()).createGoal(any(Goal.class));
    }

    @Test
    public void testUpdateGoal_Success() throws Exception {
        // Arrange
        int userId = 1;
        int goalId = goal.getId();

        when(goalRepository.existsById(goalId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/finances/goals/{id}", goalId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goal)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        // Verify that the service was called
        verify(goalService).updateGoal(any(Goal.class), eq(userId));
    }

    @Test
    public void testUpdateGoal_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int goalId = goal.getId();

        when(goalService.getGoalById(goalId, userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/finances/goals/{id}", goalId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goal)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Goal with id " + goalId + " not found."));

        // Verify that the service was not called
        verify(goalService, never()).updateGoal(any(Goal.class), eq(userId));
    }

    @Test
    public void testDeleteGoal_Success() throws Exception {
        // Arrange
        int userId = 1;
        int goalId = goal.getId();

        when(goalRepository.existsById(goalId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/finances/goals/{id}", goalId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // Verify that the service was called
        verify(goalService).deleteGoal(goalId, userId);
    }

    @Test
    public void testDeleteGoal_NotFound() throws Exception {
        // Arrange
        int userId = 1;
        int goalId = goal.getId();

        when(goalService.getGoalById(goalId, userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(delete("/finances/goals/{id}", goalId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Goal with id " + goalId + " not found"));

        // Verify that the service was not called
        verify(goalService, never()).deleteGoal(goalId, userId);
    }

    @Test
    public void testGetAllGoals_Success() throws Exception {
        // Arrange
        int userId = 1;

        Goal goal2 = new Goal();
        goal2.setId(2);
        goal2.setUserId(userId);
        goal2.setName("Retirement Fund");
        goal2.setAmount(10000.0f);

        List<Goal> goals = Arrays.asList(goal, goal2);

        when(goalService.getAllGoals(userId)).thenReturn(goals);

        // Act & Assert
        mockMvc.perform(get("/finances/goals/all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(goals.size()))
                .andExpect(jsonPath("$[0].id").value(goal.getId()))
                .andExpect(jsonPath("$[0].name").value(goal.getName()))
                .andExpect(jsonPath("$[1].id").value(goal2.getId()))
                .andExpect(jsonPath("$[1].name").value(goal2.getName()));
    }

    @Test
    public void testGetAllGoals_NoGoals() throws Exception {
        // Arrange
        int userId = 1;

        when(goalService.getAllGoals(userId)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/finances/goals/all")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
