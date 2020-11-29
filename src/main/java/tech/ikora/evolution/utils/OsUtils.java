package tech.ikora.evolution.utils;

public class OsUtils {
    private static final String OS  = System.getProperty("os.name");

    public static boolean isWindows() {
        return OS.startsWith("Windows");
    }
}
