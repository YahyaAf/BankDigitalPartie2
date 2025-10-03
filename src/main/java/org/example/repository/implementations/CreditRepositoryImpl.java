package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.Credit;
import org.example.repository.CreditRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreditRepositoryImpl implements CreditRepository {
    private final Connection connection;

    public CreditRepositoryImpl(){
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void save(Credit credit){
        String sql = "INSERT INTO credits (id, amount, interest_rate, start_date, end_date, duration_months, " +
                "status, account_id, credit_type, income_proof, interest_amount, validation_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1, credit.getId());
            stmt.setBigDecimal(2, credit.getAmount());
            stmt.setDouble(3, credit.getInterestRate());
            stmt.setDate(4, Date.valueOf(credit.getStartDate()));
            stmt.setDate(5, Date.valueOf(credit.getEndDate()));
            stmt.setInt(6, credit.getDurationMonths());
            stmt.setObject(7, credit.getStatus().name(),java.sql.Types.OTHER);
            stmt.setObject(8, credit.getAccountId());
            stmt.setObject(9, credit.getType().name(),java.sql.Types.OTHER);
            stmt.setString(10, credit.getIncomeProof());
            stmt.setBigDecimal(11, credit.getInterestAmount());
            stmt.setObject(12, credit.getValidationStatus().name(),java.sql.Types.OTHER);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error saving credit", e);
        }
    }

    public Optional<Credit> findById(UUID id){
        String sql = "SELECT * FROM credits WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(mapRowToCredit(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting credit", e);
        }
        return Optional.empty();
    }

    public List<Credit> findAll(){
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM credits";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while (rs.next()){
                credits.add(mapRowToCredit(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting credits", e);
        }
        return credits;
    }

    public List<Credit> findByAccountId(UUID accountId){
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM credits WHERE account_id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setObject(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                credits.add(mapRowToCredit(rs));
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting credits", e);
        }
        return credits;
    }

    public void updateStatus(UUID id, Credit.CreditStatus status){
        String sql = "UPDATE credits SET status = ? WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1, status.name());
            stmt.setObject(2, id);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error updating credit status", e);
        }
    }

    public void updateValidationStatus(UUID id, Credit.ValidationStatus validationStatus){
        String sql = "UPDATE credits SET validation_status = ? WHERE id = ?";
        try(PreparedStatement stmt = this.connection.prepareStatement(sql)){
            stmt.setString(1, validationStatus.name());
            stmt.setObject(2, id);
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Error updating credit validation status", e);
        }
    }

    private Credit mapRowToCredit(ResultSet rs) throws SQLException {
        return new Credit(
                (UUID) rs.getObject("id"),
                rs.getBigDecimal("amount"),
                rs.getDouble("interest_rate"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getInt("duration_months"),
                Credit.CreditStatus.valueOf(rs.getString("status")),
                Credit.CreditType.valueOf(rs.getString("credit_type")),
                (UUID) rs.getObject("account_id"),
                rs.getString("income_proof"),
                rs.getBigDecimal("interest_amount"),
                Credit.ValidationStatus.valueOf(rs.getString("validation_status"))
        );
    }

}
