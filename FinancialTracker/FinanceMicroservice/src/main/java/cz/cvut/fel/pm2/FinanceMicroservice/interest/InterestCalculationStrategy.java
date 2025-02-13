package cz.cvut.fel.pm2.FinanceMicroservice.interest;

public interface InterestCalculationStrategy {
    float calculateInterest(float amount, long daysBetween, int interestRate);
}
