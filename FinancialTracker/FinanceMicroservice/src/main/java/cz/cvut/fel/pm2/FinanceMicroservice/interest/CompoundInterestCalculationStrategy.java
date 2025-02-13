package cz.cvut.fel.pm2.FinanceMicroservice.interest;
/**
 * Implementation of compound interest calculation strategy.
 */
public class CompoundInterestCalculationStrategy implements InterestCalculationStrategy {
    /**
     * Calculates compound interest based on the given parameters.
     *
     * @param amount       the principal amount
     * @param daysBetween  the number of days between start and due dates
     * @param interestRate the interest rate per annum
     * @return the calculated compound interest
     */
    @Override
    public float calculateInterest(float amount, long daysBetween, int interestRate) {
        float rate = interestRate / 100.0f;
        float timeInYears = daysBetween / 365.0f;
        return amount * (float) Math.pow(1 + rate, timeInYears) - amount;
    }
}