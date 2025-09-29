package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.FeeRule;
import org.example.model.Transaction;
import org.example.repository.FeeRuleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FeeRuleRepositoryImpl implements FeeRuleRepository {
    private final Connection connection;

    public FeeRuleRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(FeeRule feeRule) {
        String sql = "INSERT INTO fees_rules (operation_type, mode, value, currency, is_active, created_at, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, feeRule.getOperationType().name());
            stmt.setString(2, feeRule.getMode().name());
            stmt.setBigDecimal(3, feeRule.getValue());
            stmt.setString(4, feeRule.getCurrency());
            stmt.setBoolean(5, feeRule.isActive());
            stmt.setTimestamp(6, Timestamp.valueOf(feeRule.getCreatedAt()));
            stmt.setObject(7, feeRule.getCreatedBy());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                feeRule.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while inserting fee rule: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FeeRule> findById(Long id) {
        String sql = "SELECT * FROM fees_rules WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching fee rule: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<FeeRule> findAll() {
        String sql = "SELECT * FROM fees_rules";
        List<FeeRule> rules = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rules.add(mapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching all fee rules: " + e.getMessage(), e);
        }
        return rules;
    }

    @Override
    public List<FeeRule> findActiveRules() {
        String sql = "SELECT * FROM fees_rules WHERE is_active = TRUE";
        List<FeeRule> rules = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rules.add(mapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching active fee rules: " + e.getMessage(), e);
        }
        return rules;
    }

    @Override
    public Optional<FeeRule> findActiveByOperationType(Transaction.TransactionType operationType) {
        String sql = "SELECT * FROM fees_rules WHERE operation_type = ? AND is_active = TRUE LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, operationType.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching fee rule by operation type: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void updateStatus(Long id, boolean isActive) {
        String sql = "UPDATE fees_rules SET is_active = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating fee rule status: " + e.getMessage(), e);
        }
    }

    private FeeRule mapFromResultSet(ResultSet rs) throws SQLException {
        return new FeeRule(
                rs.getLong("id"),
                Transaction.TransactionType.valueOf(rs.getString("operation_type")),
                FeeRule.FeeMode.valueOf(rs.getString("mode")),
                rs.getBigDecimal("value"),
                rs.getString("currency"),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                (UUID) rs.getObject("created_by")
        );
    }
}
