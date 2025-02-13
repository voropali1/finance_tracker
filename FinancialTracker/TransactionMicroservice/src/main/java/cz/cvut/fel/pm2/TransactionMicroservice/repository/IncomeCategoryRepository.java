package cz.cvut.fel.pm2.TransactionMicroservice.repository;


import cz.cvut.fel.pm2.TransactionMicroservice.entity.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Integer> {
    boolean existsByCategoryName(String categoryName);
}

