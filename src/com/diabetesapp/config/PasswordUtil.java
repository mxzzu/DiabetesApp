package com.diabetesapp.config;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Hash della password
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verifica della password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
