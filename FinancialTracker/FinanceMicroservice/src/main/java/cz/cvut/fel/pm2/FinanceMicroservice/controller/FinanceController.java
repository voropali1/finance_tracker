package cz.cvut.fel.pm2.FinanceMicroservice.controller;


import cz.cvut.fel.pm2.FinanceMicroservice.entity.Finance;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.FinanceRepository;
import cz.cvut.fel.pm2.FinanceMicroservice.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing finance-related operations.
 */
@RestController
@RequestMapping("/finances")
public class FinanceController {
    private final FinanceService financeService;
    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }
    @Autowired
    private FinanceRepository financeRepository;
    /**
     * Retrieves finance information by its ID.
     *
     * @param id the ID of the finance information to retrieve
     * @return ResponseEntity containing the retrieved finance information or an error message if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFinanceById(@PathVariable("id") int id) {
        try {
            Finance finance = financeService.getFinanceById(id);
            if (finance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Finance with id " + id + " not found");
            }
            return ResponseEntity.ok().body(finance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }
}
