package cz.cvut.fel.pm2.FinanceMicroservice.service;

import cz.cvut.fel.pm2.FinanceMicroservice.entity.Finance;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.FinanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FinanceService {
    private final FinanceRepository financeRepository;
    /**
     * Constructs a new FinanceService with the specified FinanceRepository.
     *
     * @param financeRepository the repository used to manage finance records
     */
    public FinanceService(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;
    }
    /**
     * Retrieves a finance record by its ID.
     *
     * @param financeId the ID of the finance record
     * @return the finance record with the specified ID
     * @throws RuntimeException if the finance record is not found
     */
    public Finance getFinanceById(int financeId){
        return financeRepository.findById(financeId)
                .orElseThrow(() -> new RuntimeException("Finance not found with id: " + financeId));
    }
}