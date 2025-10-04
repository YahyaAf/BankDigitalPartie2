package org.example.controller;

import org.example.service.ClientService;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    private static final String CIN_REGEX = "^[A-Za-z0-9]{6,10}$";
    private static final Pattern CIN_PATTERN = Pattern.compile(CIN_REGEX);

    private static final String PHONE_REGEX = "^[0-9]{9,10}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public boolean createClient(String firstName, String lastName, String cin,
                                String phoneNumber, String address, String email, BigDecimal salary){
        if(firstName == null || firstName.isBlank()){
            System.out.println("First name is required");
            return false;
        }
        if(lastName == null || lastName.isBlank()){
            System.out.println("Last name is required");
            return false;
        }
        if(cin == null || cin.isBlank()){
            System.out.println("Cin is required");
            return false;
        }
        if (phoneNumber != null && !phoneNumber.isBlank()
                && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            System.out.println("Invalid phone number format.");
            return false;
        }
        if(address == null || address.isBlank()){
            System.out.println("Address is required");
            return false;
        }
        if (email != null && !email.isBlank()
                && !EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("Invalid email format.");
            return false;
        }
        if (salary != null && salary.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Salary cannot be negative.");
            return false;
        }

        return clientService.createClient(firstName, lastName, cin, phoneNumber, address, email, salary);
    }

    public void showAllClients(){
        clientService.showAllClients();
    }

}
