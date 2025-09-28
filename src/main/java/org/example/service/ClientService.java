package org.example.service;

import org.example.model.Client;
import org.example.model.User;
import org.example.repository.ClientRepository;

import java.math.BigDecimal;
import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository;
    private final AuthService authService;

    public ClientService(ClientRepository clientRepository, AuthService authService){
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

    public void showAllClients(){
        List<Client> clients = clientRepository.findAll();
        if(clients.isEmpty()){
            System.out.println("No clients found");
        }else{
            System.out.println("======List Of Clients=======");
            for(Client client : clients){
                System.out.println("ID: " + client.getId());
                System.out.println("Name: " + client.getFirstName() + " " + client.getLastName());
                System.out.println("CIN: " + client.getCin());
                System.out.println("Phone: " + client.getPhoneNumber());
                System.out.println("Email: " + client.getEmail());
                System.out.println("Salary: " + client.getSalary());
                System.out.println("Created By: " + client.getCreatedBy());
                System.out.println("------------------------");
            }
        }
    }

}
