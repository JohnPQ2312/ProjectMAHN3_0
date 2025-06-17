package FXMLControllers.auxclasses;

import PersistenceClasses.Users;

//Maneja una unica sesión en todo el programa
public class UserSession {

    private static Users currentUser;

    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public static Users getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }
}