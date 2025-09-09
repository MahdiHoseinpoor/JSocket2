package JSocket2.Core.Client;

/**
 * Holds configuration options for the client's automatic reconnection logic.
 * This includes retry delays, jitter, and thresholds for adjusting backoff strategy.
 */
public class ReconnectionOptions {
    private int minRetryDelay = 3000;
    private int maxRetryDelay = 15000;
    private int maxTryCount_for_changeRetryDelay = 5;
    private float coefficient_jitter = 0.5f;

    /**
     * Gets the minimum delay in milliseconds before the first reconnection attempt.
     * @return The minimum retry delay.
     */
    public int getMinRetryDelay() {
        return minRetryDelay;
    }

    /**
     * Sets the minimum delay in milliseconds before the first reconnection attempt.
     * @param minRetryDelay The minimum retry delay.
     */
    public void setMinRetryDelay(int minRetryDelay) {
        this.minRetryDelay = minRetryDelay;
    }

    /**
     * Gets the jitter coefficient, used to randomize retry delays to prevent thundering herd scenarios.
     * @return The jitter coefficient (between 0 and 1).
     */
    public float getCoefficient_jitter() {
        return coefficient_jitter;
    }

    /**
     * Gets the absolute maximum delay in milliseconds for reconnection attempts.
     * @return The maximum retry delay.
     */
    public int getMaxRetryDelay() {
        return maxRetryDelay;
    }

    /**
     * Gets the number of failed attempts before the retry delay is increased.
     * @return The maximum try count before delay adjustment.
     */
    public int getMaxTryCount_for_changeRetryDelay() {
        return maxTryCount_for_changeRetryDelay;
    }

    /**
     * Sets the jitter coefficient.
     * @param coefficient_jitter The coefficient, which must be less than or equal to 1.
     * @throws Exception if the coefficient is greater than 1.
     */
    public void setCoefficient_jitter(float coefficient_jitter) throws Exception {
        if(coefficient_jitter > 1) throw new Exception("coefficient_jitter must be less than 1");
        this.coefficient_jitter = coefficient_jitter;
    }

    /**
     * Sets the absolute maximum delay in milliseconds for reconnection attempts.
     * @param maxRetryDelay The maximum retry delay.
     */
    public void setMaxRetryDelay(int maxRetryDelay) {
        this.maxRetryDelay = maxRetryDelay;
    }

    /**
     * Sets the number of failed attempts before the retry delay is increased.
     * @param maxTryCount_for_changeRetryDelay The maximum try count.
     */
    public void setMaxTryCount_for_changeRetryDelay(int maxTryCount_for_changeRetryDelay) {
        this.maxTryCount_for_changeRetryDelay = maxTryCount_for_changeRetryDelay;
    }
}