package cz.cvut.fel.pm2.FinanceMicroservice.controller;

import cz.cvut.fel.pm2.FinanceMicroservice.dto.DebtDTO;
import cz.cvut.fel.pm2.FinanceMicroservice.entity.Debt;
import cz.cvut.fel.pm2.FinanceMicroservice.interest.CompoundInterestCalculationStrategy;
import cz.cvut.fel.pm2.FinanceMicroservice.interest.InterestCalculator;
import cz.cvut.fel.pm2.FinanceMicroservice.interest.SimpleInterestCalculationStrategy;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.DebtRepository;
import cz.cvut.fel.pm2.FinanceMicroservice.service.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
/**
 * Controller class for managing debts in the finance module.
 */
@RestController
@RequestMapping("/finances/debts")
public class DebtController {
    private final DebtService debtService;
    @Autowired
    private DebtRepository debtRepository;
    private final InterestCalculator interestCalculator;
    @Autowired
    public DebtController(DebtService debtService, InterestCalculator interestCalculator) {
        this.debtService = debtService;
        this.interestCalculator = interestCalculator;
    }
    /**
     * Retrieves a debt by its ID.
     *
     * @param id     the ID of the debt to retrieve
     * @param userId the ID of the user associated with the debt
     * @return ResponseEntity containing the retrieved debt or an error message if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDebtById(@PathVariable("id") int id, @RequestParam int userId) {
        try {
            Debt debt = debtService.getDebtById(id, userId);
            if (debt == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debt with id " + id + " not found");
            }
            return ResponseEntity.ok().body(debt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }
    /**
     * Retrieves all debts associated with a specific user.
     *
     * @param userId the ID of the user to retrieve debts for
     * @return ResponseEntity containing the list of retrieved debts or an error message if none found
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllDebtById( @RequestParam int userId) {
        try {
            List<Debt> debts = debtService.getAllById(userId);
            if (debts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debts was not fount");
            }
            return ResponseEntity.ok().body(debts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }
    /**
     * Retrieves the interest rate for a specific debt.
     *
     * @param id     the ID of the debt to retrieve the interest rate for
     * @param userId the ID of the user associated with the debt
     * @return ResponseEntity containing the calculated interest rate or an error message if not found
     */
    @GetMapping("/{id}/interest-rate")
    public ResponseEntity<?> getInterestRate(@PathVariable("id") int id, @RequestParam int userId) {
        try {
            Debt debt = debtService.getDebtById(id, userId);
            if (debt == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debt with id " + id + " not found");
            }
            LocalDate from = debt.getFromDate();
            LocalDate due = debt.getDueDate();
            int interestRate = debt.getInterestRate();
            long daysBetween = ChronoUnit.DAYS.between(from, due);
            float interest = interestCalculator.calculateInterest(debt.getAmount(), daysBetween, interestRate);
            return ResponseEntity.ok().body(interest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }
    /**
     * Sets the interest calculation strategy for future calculations.
     *
     * @param userId          the ID of the user setting the strategy
     * @param numberOfStrategy the number representing the chosen strategy (1 for Compound, 2 for Simple)
     */
    @PostMapping("/{id}/set-calculation-strategy")
    public void setInterestCalculationStrategy(@RequestParam int userId, @RequestParam int numberOfStrategy) {
        switch (numberOfStrategy){
            case 1:
                interestCalculator.setStrategy(new CompoundInterestCalculationStrategy());
                break;
            case 2:
                interestCalculator.setStrategy(new SimpleInterestCalculationStrategy());
                break;
        }
    }
    /**
     * Adds a new debt.
     *
     * @param debtDto the DTO containing debt information
     * @param userId  the ID of the user adding the debt
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PostMapping("/add-debt")
    public ResponseEntity<?> addDebt(@RequestBody DebtDTO debtDto,  @RequestParam int userId) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(2000, 1, 1);
        if (debtDto.getDueDate().isBefore(currentDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debt can not be before the current date");
        }
        Debt debt = new Debt();
        debt.setFromDate(currentDate);
        debt.setUserId(userId);
        debt.setAmount(debtDto.getAmount());
        debt.setName(debtDto.getName());
        debt.setNameOfPersonToGiveBack(debtDto.getNameOfPersonToGiveBack());
        debt.setDueDate(debtDto.getDueDate());
        debt.setInterestRate(debtDto.getInterestRate());
        debtService.createDebt(debt);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * Updates an existing debt.
     *
     * @param id          the ID of the debt to update
     * @param updatedDebt the updated debt object
     * @param userId      the ID of the user updating the debt
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDebt(@PathVariable("id") int id, @RequestBody Debt updatedDebt,  @RequestParam int userId) {
        if (!debtRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debt with id " + id + " not found.");
        }
        updatedDebt.setId(id);
        debtService.updateDebt(updatedDebt, userId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDebt(@PathVariable("id") int id, @RequestParam int userId) {
        if (!debtRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debt with id " + id + " not found");
        }
        debtService.deleteDebt(id, userId);
        return ResponseEntity.noContent().build();
    }
}
