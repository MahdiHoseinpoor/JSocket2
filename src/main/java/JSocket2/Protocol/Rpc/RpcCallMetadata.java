package JSocket2.Protocol.Rpc;

/**
 * A data model that holds the metadata for an RPC call,
 * specifying the target controller and action.
 */
public class RpcCallMetadata {
    private String controller;
    private String action;

    /**
     * Constructs an {@code RpcCallMetadata}.
     *
     * @param controller The name of the target controller.
     * @param action     The name of the target action.
     */
    public RpcCallMetadata(String controller, String action) {
        this.controller = controller;
        this.action = action;
    }

    /**
     * Gets the controller name.
     *
     * @return The controller name.
     */
    public String getController() {
        return controller;
    }

    /**
     * Sets the controller name.
     *
     * @param controller The controller name.
     */
    public void setController(String controller) {
        this.controller = controller;
    }

    /**
     * Gets the action name.
     *
     * @return The action name.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action name.
     *
     * @param action The action name.
     */
    public void setAction(String action) {
        this.action = action;
    }
}