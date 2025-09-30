package org.example.controller;

import org.example.service.AuthService;

import java.util.regex.Pattern;

public class AuthController {
    private final AuthService authService;

    // Regex بسيط للتحقق من email
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String email, String password) {

        if (email == null || email.isBlank()) {
            System.out.println("Email cannot be empty.");
            return false;
        }
        if (password == null || password.isBlank()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("Invalid email format.");
            return false;
        }
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters.");
            return false;
        }

        return authService.login(email, password);
    }

    public boolean updateProfile(String newName, String newEmail, String newPassword){
        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                System.out.println("Password must be at least 6 characters.");
                return false;
            }
        }

        return  authService.updateProfile(newName, newEmail, newPassword);
    }

    public void logout() {
        authService.logout();
    }

    public void showProfile() {
        authService.showProfile();
    }
}
