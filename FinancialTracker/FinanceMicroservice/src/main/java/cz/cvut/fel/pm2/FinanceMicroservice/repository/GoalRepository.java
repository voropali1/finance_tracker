package cz.cvut.fel.pm2.FinanceMicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cz.cvut.fel.pm2.FinanceMicroservice.entity.Goal;

import java.util.List;
import java.util.Optional;
@Repository
public interface GoalRepository extends JpaRepository<Goal, Integer> {

    List<Goal> findAllByUserId(int userId);

    @Query("SELECT e FROM Goal e WHERE e.id = :goalId AND e.userId = :userId")
    Optional<Goal> findByIdAndUserId(@Param("goalId") int goalId, @Param("userId") int userId);
}
