package org.example.controller;

import org.example.model.User;
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

    public boolean createUser(String name, String email, String password, String roleInput){
        if (name == null || name.isBlank()) {
            System.out.println("Name cannot be empty.");
            return false;
        }

        if (email == null || email.isBlank()) {
            System.out.println("Email cannot be empty.");
            return false;
        }

        if (password == null || password.isBlank()) {
            System.out.println("Password cannot be empty.");
            return false;
        }

        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters.");
            return false;
        }

        User.Role role;
        try{
            role =  User.Role.valueOf(roleInput.toUpperCase());
        }catch(IllegalArgumentException e){
            System.out.println("Invalid role. Available: ADMIN, TELLER...");
            return false;
        }

        return authService.createUser(name, email, password, role);

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
