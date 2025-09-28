package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.Transaction;
import org.example.repository.TransactionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionRepositoryImpl implements TransactionRepository {

    private final Connection connection;

    public TransactionRepositoryImpl(){
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void create(Transaction transaction){
        String sql = "INSERT INTO transactions (id, amount, type, status, timestamp, sender_account_id, receiver_account_id, external_receiver_account)" +
                " VALUES (?, ?, ?::transaction_type_enum, ?::transaction_status_enum, ?, ?, ?, ?)";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1, transaction.getId());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getType().name());
            stmt.setString(4, transaction.getStatus().name());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            stmt.setObject(6, transaction.getSenderAccountId());
            stmt.setObject(7, transaction.getReceiverAccountId());
            stmt.setString(8, transaction.getExternalReceiverAccount());
            stmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error while creating transaction: " + e.getMessage(), e);
        }
    }

    public Optional<Transaction> findById(UUID id){
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return Optional.of(mapFromResultSet(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error while finding transaction: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Transaction> findAll(){
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while (rs.next()){
                transactions.add(mapFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error while finding transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public void updateStatus(UUID id, Transaction.TransactionStatus newStatus){
        String sql = "UPDATE transactions SET status = ?::transaction_status_enum WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1,newStatus.name());
            stmt.setObject(2,id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No transaction found with ID: " + id);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error while updating transaction: " + e.getMessage(), e);
        }
    }

    private Transaction mapFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
                (UUID) rs.getObject("id"),
                rs.getBigDecimal("amount"),
                Transaction.TransactionType.valueOf(rs.getString("type")),
                Transaction.TransactionStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                (UUID) rs.getObject("sender_account_id"),
                (UUID) rs.getObject("receiver_account_id"),
                rs.getString("external_receiver_account")
        );
    }

}
