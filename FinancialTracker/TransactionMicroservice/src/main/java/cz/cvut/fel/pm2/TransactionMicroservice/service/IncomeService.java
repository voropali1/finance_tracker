package cz.cvut.fel.pm2.TransactionMicroservice.service;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.Income;
import cz.cvut.fel.pm2.TransactionMicroservice.entity.IncomeCategory;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.IncomeCategoryRepository;
import cz.cvut.fel.pm2.TransactionMicroservice.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class IncomeService  {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    public IncomeService(IncomeRepository incomeRepository, IncomeCategoryRepository incomeCategoryRepository) {
        this.incomeRepository = incomeRepository;
        this.incomeCategoryRepository = incomeCategoryRepository;
    }


    public void addIncomeCategory(String categoryName) {
        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setCategoryName(categoryName);
        incomeCategoryRepository.save(incomeCategory);
    }


    public Income getIncomeById(int incomeId, int userId) {
        Optional<Income> optionalIncome = incomeRepository.findByIdAndUserId(incomeId, userId);
        return optionalIncome.orElseThrow(() -> new RuntimeException("Income not found with id: " + incomeId + " for user: " + userId));
    }


    @Transactional
    public void updateIncome(Income updatedIncome, int userId) {
        Objects.requireNonNull(updatedIncome);
        Income existingIncome = getIncomeById(updatedIncome.getId(), userId);
        existingIncome.setAmount(updatedIncome.getAmount());
        existingIncome.setName(updatedIncome.getName());
        existingIncome.setTransactionDate(updatedIncome.getTransactionDate());
        existingIncome.setIncomeCategory(updatedIncome.getIncomeCategory());
        incomeRepository.save(existingIncome);
    }


    @Transactional
    public void createIncome(Income income){
        Objects.requireNonNull(income);
        incomeRepository.save(income);
    }

    @Transactional
    public void deleteIncome(int incomeId, int userId) {
        getIncomeById(incomeId, userId);
        incomeRepository.deleteById(incomeId);
    }

    @Transactional(readOnly = true)
    public List<Income> getIncomesByIncomeCategoryAndUserId(IncomeCategory incomeCategory, int userId) {
        return incomeRepository.findByIncomeCategoryAndUserId(incomeCategory, userId);
    }


    @Transactional(readOnly = true)
    public List<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<IncomeCategory> getAllIncomeCategories() {
        return incomeCategoryRepository.findAll();
    }


    public List<Income> getIncomesByIncomeCategory(IncomeCategory incomeCategory) {
        return incomeRepository.findByIncomeCategory(incomeCategory);
    }

    public List<Income> getAllExpensesDescendingOrder(int userId) {
        return incomeRepository.findAllByOrderByTransactionDateDesc(userId);
    }

    public List<Income> getAllExpensesAscendingOrder(int userId) {
        return incomeRepository.findAllByOrderByTransactionDateAsc(userId);
    }

    public List<Income> filterIncomesByAmountRange(int userId, float fromAmount, float toAmount) {
        return incomeRepository.findByAmountBetweenOrderByAmountAsc(userId, fromAmount, toAmount);
    }

    public List<Income> filterIncomesByAmountStartingFrom(int userId, float fromAmount) {
        return incomeRepository.findByAmountGreaterThanEqualOrderByAmountAsc(userId, fromAmount);
    }
}

