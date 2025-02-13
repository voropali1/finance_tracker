package cz.cvut.fel.pm2.FinanceMicroservice.controller;


import cz.cvut.fel.pm2.FinanceMicroservice.dto.GoalDTO;
import cz.cvut.fel.pm2.FinanceMicroservice.entity.Goal;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.GoalRepository;
import cz.cvut.fel.pm2.FinanceMicroservice.service.GoalService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/finances/goals")
public class GoalController {
    private final GoalService goalService;
    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }
    @Autowired
    private GoalRepository goalRepository;
    /**
     * Retrieves a goal by its ID.
     *
     * @param id     the ID of the goal to retrieve
     * @param userId the ID of the user associated with the goal
     * @return ResponseEntity containing the retrieved goal or an error message if not found
     */
    @GetMapping("/{id}")
    @Cacheable(value = "goals", key = "#id")
    public ResponseEntity<?> getGoalById(@PathVariable("id") int id, @RequestParam int userId) {
        try {
            Goal goal = goalService.getGoalById(id, userId);
            if (goal == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal with id " + id + " not found");
            }
            return ResponseEntity.ok().body(goal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }
    /**
     * Adds a new goal.
     *
     * @param goalDto the DTO containing goal information
     * @param userId  the ID of the user adding the goal
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PostMapping("/add-goal")
    public ResponseEntity<?> addGoal(@Valid @RequestBody GoalDTO goalDto, @RequestParam int userId) {
        Goal goal = new Goal();
        goal.setAmount(goalDto.getAmount());
        goal.setName(goalDto.getName());
        goal.setUserId(userId);
        goalService.createGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * Updates an existing goal.
     *
     * @param id          the ID of the goal to update
     * @param updatedGoal the updated goal object
     * @param userId      the ID of the user updating the goal
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PutMapping("/{id}")
    @CacheEvict(value = "goals", key = "#id")
    public ResponseEntity<?> updateGoal(@PathVariable("id") int id, @RequestBody Goal updatedGoal,  @RequestParam int userId) {
        if (!goalRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal with id " + id + " not found.");
        }
        updatedGoal.setId(id);
        goalService.updateGoal(updatedGoal, userId);
        return ResponseEntity.ok().build();
    }
    /**
     * Deletes a goal by its ID.
     *
     * @param id     the ID of the goal to delete
     * @param userId the ID of the user deleting the goal
     * @return ResponseEntity indicating success or failure of the operation
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "goals", key = "#id")
    public ResponseEntity<?> deleteGoal(@PathVariable("id") int id, @RequestParam int userId) {
        if (!goalRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal with id " + id + " not found");
        }
        goalService.deleteGoal(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all goals for a specific user.
     *
     * @param userId the ID of the user to retrieve goals for
     * @return ResponseEntity containing the list of goals
     */
    @GetMapping("/all")
    public ResponseEntity<List<Goal>> getAllGoals(@RequestParam int userId) {
        List<Goal> goals = goalService.getAllGoals(userId);
        return ResponseEntity.ok().body(goals);
    }
}