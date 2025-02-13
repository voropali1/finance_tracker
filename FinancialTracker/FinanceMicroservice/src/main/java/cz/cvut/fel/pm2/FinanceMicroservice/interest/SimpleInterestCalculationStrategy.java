package cz.cvut.fel.pm2.FinanceMicroservice.interest;

/**
 * Implementation of simple interest calculation strategy.
 */
public class SimpleInterestCalculationStrategy implements InterestCalculationStrategy {
    /**
     * Calculates simple interest based on the given parameters.
     *
     * @param amount       the principal amount
     * @param daysBetween  the number of days between start and due dates
     * @param interestRate the interest rate per annum
     * @return the calculated simple interest
     */
    @Override
    public float calculateInterest(float amount, long daysBetween, int interestRate) {
        float rate = interestRate / 100.0f;
        float timeInYears = daysBetween / 365.0f;
        return amount * rate * timeInYears;
    }
}