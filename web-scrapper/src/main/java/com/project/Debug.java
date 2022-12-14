package com.project;

public interface Debug {
    /**
     * true to debug and show all console output.
     */
    boolean DEBUG = true;
    boolean SHOW_BROWSER = false;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    default void debug(String input) {
        if(DEBUG) System.out.println(input);
    }

    default void info(String input) {
        System.out.println(input);
    }
}

/**
 *
 * Product
 * - ID
 * - Model
 * - Description
 *
 * Instance
 * - ID
 * - Product ID
 * - Images
 * - Storage
 * - Color
 *
 * Providers
 * - Price
 * - Url
 * - Provider
 */