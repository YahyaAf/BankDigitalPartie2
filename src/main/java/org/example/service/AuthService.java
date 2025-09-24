package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;

import java.util.Optional;

public class AuthService {
    private UserRepository userRepository;
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

    public User getCurrentUser(){
        return currentUser;
    }
}
