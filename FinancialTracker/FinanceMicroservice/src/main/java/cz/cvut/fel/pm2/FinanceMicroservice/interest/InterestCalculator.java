package cz.cvut.fel.pm2.FinanceMicroservice.interest;
/**
 * A class responsible for calculating interest based on a specific strategy.
 */
public class InterestCalculator {
    private InterestCalculationStrategy strategy;
    /**
     * Sets the interest calculation strategy.
     *
     * @param strategy the interest calculation strategy to be set
     */
    public void setStrategy(InterestCalculationStrategy strategy) {
        this.strategy = strategy;
    }
    /**
     * Calculates interest using the current strategy.
     *
     * @param amount       the principal amount
     * @param daysBetween  the number of days between start and due dates
     * @param interestRate the interest rate per annum
     * @return the calculated interest amount
     */
    public float calculateInterest(float amount, long daysBetween, int interestRate) {
        return strategy.calculateInterest(amount, daysBetween, interestRate);
    }
}