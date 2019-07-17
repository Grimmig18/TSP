package app;

/**
 * IdSetter
 */
public class IdSetter {

    private static int currentId = 0;

    public static int getNewId() {
        currentId++;
        return currentId - 1;
    }

    public static void resetIdCounter() {
        currentId = 0;
    }
}