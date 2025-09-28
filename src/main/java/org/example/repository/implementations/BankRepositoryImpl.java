package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.Bank;
import org.example.repository.BankRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class BankRepositoryImpl implements BankRepository {
    private final Connection connection;

    public BankRepositoryImpl(){
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void create(Bank bank){
        String sql = "INSERT INTO bank (id, name, total_balance) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,bank.getId());
            stmt.setString(2,bank.getName());
            stmt.setBigDecimal(3,bank.getTotalBalance());
            stmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error while inserting bank: " + e.getMessage(), e);
        }

    }

    public Optional<Bank> findById(UUID id){
        String sql = "SELECT * FROM bank WHERE id=?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapFromResultSet(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while fetching bank: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Bank> findFirst() {
        String sql = "SELECT * FROM bank LIMIT 1";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching bank: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void updateTotalBalance(UUID id,java.math.BigDecimal newBalance){
        String sql = "UPDATE bank SET total_balance=? WHERE id=?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setBigDecimal(1,newBalance);
            stmt.setObject(2,id);
            int rows = stmt.executeUpdate();
            if(rows == 0){
                throw new RuntimeException("No bank found with ID: " + id);
            }
        }catch(SQLException e){
            throw new RuntimeException("Error while updating bank: " + e.getMessage(), e);
        }
    }

    private Bank mapFromResultSet(ResultSet rs) throws SQLException {
        return new Bank(
                (UUID) rs.getObject("id"),
                rs.getString("name"),
                rs.getBigDecimal("total_balance")
        );
    }


}
