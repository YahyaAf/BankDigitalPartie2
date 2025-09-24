package org.example.repository;

import org.example.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    void createClient(Client client);
    Optional<Client> findById(UUID id);
    Optional<Client> findByCin(String cin);
    List<Client> findAll();
    void updateClient(Client client);
    void deleteClient(UUID id);
}
