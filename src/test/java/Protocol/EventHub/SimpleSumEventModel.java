package Protocol.EventHub;

/**
 * A simple Plain Old Java Object (POJO) used as a data model for event payloads in tests.
 */
public class SimpleSumEventModel {
    int a;
    int b;

    /**
     * Constructs a SimpleSumEventModel.
     * @param a The first integer value.
     * @param b The second integer value.
     */
    public SimpleSumEventModel(int a, int b){
        this.a = a;
        this.b = b;
    }

    /**
     * Gets the value of 'a'.
     * @return The integer 'a'.
     */
    public int getA() {
        return a;
    }

    /**
     * Gets the value of 'b'.
     * @return The integer 'b'.
     */
    public int getB() {
        return b;
    }
}