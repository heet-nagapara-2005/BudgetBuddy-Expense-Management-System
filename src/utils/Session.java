package utils;

public class Session {
    private static int currentUser = -1; // -1 indicates no user is logged in
    public static int getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(int userId) {
        currentUser = userId;
    }
    public static void clearSession() {
        currentUser = -1; // or any other default value indicating no user is logged in
    }
}
