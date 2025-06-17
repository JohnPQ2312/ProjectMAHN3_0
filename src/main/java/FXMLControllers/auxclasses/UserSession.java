package FXMLControllers.auxclasses;

import PersistenceClasses.Users;

/**
 * Manages the current user session for the application.
 * Singleton-style utility for storing and clearing session user data.
 */
public class UserSession {

    private static Users currentUser;

    /**
     * Sets the current authenticated user for the session.
     * @param user The logged-in user object
     */
    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    /**
     * Gets the current user for the session.
     * @return The Users object if logged in, otherwise null
     */
    public static Users getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the current user session.
     */
    public static void clearSession() {
        currentUser = null;
    }
}