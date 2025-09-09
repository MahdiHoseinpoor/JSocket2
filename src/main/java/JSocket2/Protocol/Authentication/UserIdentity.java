package JSocket2.Protocol.Authentication;

/**
 * Represents the identity of an authenticated user.
 * This class is immutable.
 */
public class UserIdentity {
    private final String userId;
    private final String firstName;
    private final String lastName;

    /**
     * Constructs a {@code UserIdentity} with the specified user details.
     *
     * @param userId    The unique identifier for the user.
     * @param firstName The user's first name.
     * @param lastName  The user's last name.
     */
    public UserIdentity(String userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets the user's unique identifier.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's first name.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }
}