package com.johnymuffin.beta.discordauth;

import java.security.SecureRandom;

public class Utilities {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateCode(int length) {
        if (length <= 0) {
            return "";  // Return an empty string if length is not positive
        }

        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            builder.append(CHARACTERS.charAt(index));
        }

        return builder.toString();
    }
}
