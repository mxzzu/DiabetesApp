package com.diabetesapp.config;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    /**
     * Hashes a plain text passwords
     * @param plainPassword Clear password to hash
     * @return Returns the hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Checks if two passwords match
     * @param plainPassword Clear Password entered by the user
     * @param hashedPassword Hashed password stored in the database
     * @return Returns true if the two passwords are equal, otherwise returns false
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
