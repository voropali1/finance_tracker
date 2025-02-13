package cz.cvut.fel.pm2.FinanceMicroservice.service;

import cz.cvut.fel.pm2.FinanceMicroservice.entity.Debt;
import cz.cvut.fel.pm2.FinanceMicroservice.repository.DebtRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class DebtService {
    private final DebtRepository debtRepository;

    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }
    /**
     * Retrieves a debt by its ID and the user's ID.
     *
     * @param debtId the ID of the debt
     * @param userId the ID of the user
     * @return the debt with the specified ID for the specified user
     * @throws RuntimeException if the debt is not found
     */
    public Debt getDebtById(int debtId, int userId){
        Optional<Debt> optionalIncome = debtRepository.findByIdAndUserId(debtId, userId);
        return optionalIncome.orElseThrow(() -> new RuntimeException("Debt not found with id: " + debtId + " for user: " + userId));
    }
    /**
     * Retrieves all debts for a specified user.
     *
     * @param userId the ID of the user
     * @return a list of debts for the specified user
     */
    public List<Debt> getAllById(int userId){
        List<Debt> optionalIncome = debtRepository.findAllByUserId( userId);
        return optionalIncome;
    }
    /**
     * Creates a new debt.
     *
     * @param debt the debt to be created
     * @throws NullPointerException if the debt is null
     */
    @Transactional
    public void createDebt(Debt debt) {
        Objects.requireNonNull(debt);
        debtRepository.save(debt);
    }
    /**
     * Updates an existing debt.
     *
     * @param updatedDebt the updated debt information
     * @param userId      the ID of the user
     * @throws NullPointerException if the updated debt is null
     */
    @Transactional
    public void updateDebt(Debt updatedDebt,  int userId) {
        Objects.requireNonNull(updatedDebt);
        Debt existingDebt = getDebtById(updatedDebt.getId(), userId);
        existingDebt.setAmount(updatedDebt.getAmount());
        existingDebt.setName(updatedDebt.getName());
        existingDebt.setNameOfPersonToGiveBack(updatedDebt.getNameOfPersonToGiveBack());
        existingDebt.setDueDate(updatedDebt.getDueDate());
        debtRepository.save(existingDebt);
    }
    /**
     * Deletes a debt by its ID and the user's ID.
     *
     * @param debtId the ID of the debt to be deleted
     * @param userId the ID of the user
     * @throws RuntimeException if the debt is not found
     */
    @Transactional
    public void deleteDebt(int debtId, int userId) {
        getDebtById(debtId, userId);
        debtRepository.deleteById(debtId);
    }
}
