package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public boolean login(String email, String password){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            if(user.get().getPassword().equals(password)){
                currentUser = user.get();
                System.out.println("Login successful Welcome "+currentUser.getName());
                return true;
            }else{
                System.out.println("Wrong password");
            }
        }else{
            System.out.println("Wrong email");
        }
        return false;
    }

    public void logout(){
        if(currentUser != null){
            System.out.println("User "+currentUser.getName()+" logged out");
            currentUser = null;
        }else{
            System.out.println("No user logged in");
        }
    }

    public void showProfile(){
        if(currentUser != null){
            System.out.println("=== User Profile ===");
            System.out.println("ID: " + currentUser.getId());
            System.out.println("Name: " + currentUser.getName());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("Role: " + currentUser.getRole());
        }else{
            System.out.println("No user logged in");
        }
    }

    public boolean createUser(String name, String email, String password, User.Role role){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            System.out.println("Email already in use");
            return false;
        }
        User newUser = new User(name, email, password, role);
        userRepository.save(newUser);
        System.out.println("User registered successfully with email: " + email);
        return true;
    }

    public boolean updateProfile(String newName, String newEmail, String newPassword){
        if(currentUser == null){
            System.out.println("No user logged in");
            return false;
        }
        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(currentUser.getEmail())) {
            boolean emailExists = userRepository.findAll().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(newEmail));
            if (emailExists) {
                System.out.println("Email already in use by another account.");
                return false;
            }
            currentUser.setEmail(newEmail);
        }
        if(newName != null && !newName.isEmpty()){
            currentUser.setName(newName);
        }
        if(newPassword != null && !newPassword.isEmpty()){
            currentUser.setPassword(newPassword);
        }
        userRepository.update(currentUser);
        System.out.println("Profile updated successfully.");
        return true;
    }

    public User getCurrentUser(){
        return currentUser;
    }
}
