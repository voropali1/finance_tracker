package cz.cvut.fel.pm2.FinanceMicroservice.repository;

import cz.cvut.fel.pm2.FinanceMicroservice.entity.Finance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceRepository extends JpaRepository<Finance, Integer> {


}
