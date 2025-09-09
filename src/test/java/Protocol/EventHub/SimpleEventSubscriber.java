package Protocol.EventHub;

import JSocket2.Protocol.EventHub.EventSubscriberBase;
import JSocket2.Protocol.EventHub.OnEvent;

/**
 * A simple implementation of {@link EventSubscriberBase} for testing purposes.
 * It contains various methods annotated with {@code @OnEvent} to handle different test scenarios
 * and stores the results in public fields for easy assertion.
 */
public class SimpleEventSubscriber extends EventSubscriberBase {

    /** Stores the result of the onWithModelSumEvent method. */
    public int onWithModelSumEventResult = 0;
    /** Stores the result of the onWithoutModelSumEvent method. */
    public int onWithoutModelSumEventResult = 0;
    /** Stores the results of the four methods handling the "multipleEvent". */
    public int[] multipleEventResult = new int[4];
    /** Stores the result of the IgnoreEventNameCase method. */
    public int IgnoreEventNameCaseResult;

    /**
     * Handles the "withModelSumEvent" by summing the properties of the provided model.
     * @param model The data model containing the numbers to sum.
     */
    @OnEvent("withModelSumEvent")
    public void onWithModelSumEvent(SimpleSumEventModel model){
        onWithModelSumEventResult = model.a + model.b;
    }

    /**
     * Handles the "withoutModelSumEvent" by summing the two integer parameters.
     * @param a The first integer.
     * @param b The second integer.
     */
    @OnEvent("withoutModelSumEvent")
    public void onWithoutModelSumEvent(int a, int b){
        onWithoutModelSumEventResult = a + b;
    }

    /** First handler for "multipleEvent", performs addition. */
    @OnEvent("multipleEvent")
    public void multipleEvent1(int a, int b){
        multipleEventResult[0] = a + b;
    }

    /** Second handler for "multipleEvent", performs subtraction. */
    @OnEvent("multipleEvent")
    public void multipleEvent2(int a, int b){
        multipleEventResult[1] = a - b;
    }

    /** Third handler for "multipleEvent", performs multiplication. */
    @OnEvent("multipleEvent")
    public void multipleEvent3(int a, int b){
        multipleEventResult[2] = a * b;
    }

    /** Fourth handler for "multipleEvent", performs division. */
    @OnEvent("multipleEvent")
    public void multipleEvent4(int a, int b){
        multipleEventResult[3] = a / b;
    }

    /** Handles the "ignoreEventNameCase" event to test case-insensitivity. */
    @OnEvent("ignoreEventNameCase")
    public void IgnoreEventNameCase(int a){
        IgnoreEventNameCaseResult = a;
    }

    /** Handles an event with a mismatched parameter signature to test robustness. */
    @OnEvent("ignoreWrongSubscriberEvent")
    public void ignoreWrongSubscriberEvent(int a){
        // This method's signature does not match the published event,
        // it should be skipped without error.
    }
}