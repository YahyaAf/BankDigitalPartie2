package org.example.repository.implementations;

import org.example.model.Account;
import org.example.repository.AccountRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountRepositoryImpl implements AccountRepository {
    private final Connection connection;

    public AccountRepositoryImpl(Connection connection){
        this.connection = connection;
    }

    public void create(Account account){
        String sql = "INSERT INTO account (id,account_number,balance,type,client_id,is_active) VALUES (?,?,?,?,?,?)";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,account.getId());
            stmt.setString(2,account.getAccountNumber());
            stmt.setBigDecimal(3,account.getBalance());
            stmt.setString(4,account.getType().name());
            stmt.setObject(5,account.getClientId());
            stmt.setBoolean(6,account.isActive());
            stmt.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException("Error while creating account: " + e.getMessage(), e);
        }
    }

    public Optional<Account> findById(UUID id){
        String sql = "SELECT * FROM account WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToAccount(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error while finding account: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Account> findByAccountNumber(String accountNumber){
        String sql = "SELECT * FROM account WHERE account_number = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapToAccount(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error while finding account: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Account> findAll(){
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                accounts.add(mapToAccount(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while finding accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    public void updateBalance(Account account){
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setBigDecimal(1,account.getBalance());
            stmt.setObject(2,account.getId());
            int rows = stmt.executeUpdate();
            if(rows == 0){
                throw new RuntimeException("No account found with id " + account.getId());
            }
        }catch(SQLException e){
            throw new RuntimeException("Error while updating account: " + e.getMessage(), e);
        }
    }

    public void deactivateAccount(UUID id){
        String sql = "UPDATE accounts SET isActive = FALSE WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,id);
            int rows = stmt.executeUpdate();
            if(rows == 0){
                throw new RuntimeException("No account found with id " + id);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while deactivating account: " + e.getMessage(), e);
        }
    }

    private Account mapToAccount(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String accountNumber = rs.getString("account_number");
        BigDecimal balance = rs.getBigDecimal("balance");
        Account.AccountType type = Account.AccountType.valueOf(rs.getString("type"));
        UUID clientId = (UUID) rs.getObject("client_id");
        boolean isActive = rs.getBoolean("is_active");

        return new Account(id, accountNumber, balance, type, clientId, isActive);
    }
}
