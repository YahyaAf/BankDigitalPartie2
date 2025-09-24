package org.example.service;

import org.example.model.Client;
import org.example.model.User;
import org.example.repository.ClientRepository;

import java.math.BigDecimal;

public class TellerService {
    private final ClientRepository clientRepository;
    private final AuthService authService;

    public TellerService(ClientRepository clientRepository, AuthService authService){
        this.clientRepository = clientRepository;
        this.authService = authService;
    }

    public boolean createClient(String firstName, String lastName, String cin, String phoneNumber, String address, String email, BigDecimal salary){
        User current = authService.getCurrentUser();
        if(current == null ){
            System.out.println("User not logged in");
            return false;
        }
        if(!(current.getRole() == User.Role.TELLER || current.getRole() == User.Role.ADMIN)){
            System.out.println("Permission denied. Only TELLER or ADMIN can create clients");
            return false;
        }
        if(firstName == null || lastName == null || cin == null){
            System.out.println("Missing required fields");
            return false;
        }

        Client client = new Client(firstName,lastName,cin,phoneNumber,address,email,current.getId(),salary);
        clientRepository.createClient(client);
        System.out.println("Client created with id "+client.getId());
        return true;
    }

}
