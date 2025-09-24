package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.Client;
import org.example.repository.ClientRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientRepositoryImpl implements ClientRepository {
    private final Connection connection;

    public ClientRepositoryImpl(){
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void createClient(Client client){
        String sql = "INSERT INTO clients (id,first_name,last_name,cin,phone_number,address,email,created_by,salary) VALUES (?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,client.getId());
            stmt.setString(2,client.getFirstName());
            stmt.setString(3,client.getLastName());
            stmt.setString(4,client.getCin());
            stmt.setString(5,client.getPhoneNumber());
            stmt.setString(6,client.getAddress());
            stmt.setString(7,client.getEmail());
            stmt.setObject(8,client.getCreatedBy());
            stmt.setBigDecimal(9,client.getSalary());
            stmt.executeUpdate();
        }catch (SQLIntegrityConstraintViolationException e){
            throw new IllegalArgumentException("Client already exists with CIN or Email",e);
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating client", e);
        }
    }

    public Optional<Client> findById(UUID id){
        String sql = "SELECT * FROM clients WHERE id=?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToClient(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while finding client by id", e);
        }
        return Optional.empty();
    }

    public Optional<Client> findByCin(String cin){
        String sql = "SELECT * FROM clients WHERE cin=?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1,cin);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToClient(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while finding client by cin", e);
        }
        return Optional.empty();
    }

    public List<Client> findAll(){
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try(Statement stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                clients.add(mapToClient(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while finding all clients", e);
        }
        return clients;
    }

    public void updateClient(Client client){
        String sql = "UPDATE clients SET "
                + "first_name=?,last_name=?,"
                + "cin=?,phone_number=?,address=?,email=?,"
                + "created_by=?,salary=? WHERE id=?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1,client.getFirstName());
            stmt.setString(2,client.getLastName());
            stmt.setString(3,client.getCin());
            stmt.setString(4,client.getPhoneNumber());
            stmt.setString(5,client.getAddress());
            stmt.setString(6,client.getEmail());
            stmt.setObject(7,client.getCreatedBy());
            stmt.setBigDecimal(8,client.getSalary());
            stmt.setObject(9,client.getId());
            int rows = stmt.executeUpdate();
            if(rows == 0){
                throw new IllegalArgumentException("Client with id " + client.getId() + " does not exist");
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while updating client by id", e);
        }
    }

    public void deleteClient(UUID id){
        String sql = "DELETE FROM clients WHERE id=?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,id);
            int rows = stmt.executeUpdate();
            if(rows == 0){
                throw new IllegalArgumentException("Client with id " + id + " does not exist");
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while deleting client by id", e);
        }
    }

    public Client mapToClient(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String cin = rs.getString("cin");
        String phone = rs.getString("phone_number");
        String address = rs.getString("address");
        String email = rs.getString("email");
        UUID createdBy = (UUID) rs.getObject("created_by");
        BigDecimal salary = rs.getBigDecimal("salary");

        return new Client(firstName, lastName, cin, phone, address, email, createdBy, salary);
    }

}
