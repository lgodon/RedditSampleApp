package com.leandrogodon.redditsampleapp;

/**
 * Maintains session data for the REST services
 */
public class Session {

    private static String accessToken;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        Session.accessToken = accessToken;
    }
}
