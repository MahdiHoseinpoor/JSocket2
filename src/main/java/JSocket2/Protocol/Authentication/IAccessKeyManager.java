package JSocket2.Protocol.Authentication;

/**
 * Defines the contract for a component that manages access keys and the state of the authentication process.
 */
public interface IAccessKeyManager {
    /**
     * Retrieves the authentication model containing the access keys.
     *
     * @return An {@link AuthModel} with the current access keys.
     */
    AuthModel getKeys();

    /**
     * Sets the current state of the authentication process.
     *
     * @param state The {@link AuthProcessState} to set.
     */
    void setAuthProcessState(AuthProcessState state);

    /**
     * Gets the current state of the authentication process.
     *
     * @return The current {@link AuthProcessState}.
     */
    AuthProcessState getAuthProcessState();
}