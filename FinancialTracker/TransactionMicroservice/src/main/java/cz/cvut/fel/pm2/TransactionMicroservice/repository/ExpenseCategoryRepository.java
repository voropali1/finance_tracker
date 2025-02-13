package cz.cvut.fel.pm2.TransactionMicroservice.repository;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Integer> {
}

