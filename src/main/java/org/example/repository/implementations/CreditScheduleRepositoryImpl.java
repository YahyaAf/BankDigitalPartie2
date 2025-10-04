package org.example.repository.implementations;

import org.example.config.DatabaseConnection;
import org.example.model.Credit;
import org.example.model.CreditSchedule;
import org.example.repository.CreditScheduleRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreditScheduleRepositoryImpl implements CreditScheduleRepository {

    private final Connection connection;

    public CreditScheduleRepositoryImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void generateSchedule(Credit credit) {
        try {
            String sql = "INSERT INTO credit_schedule (id, credit_id, due_date, amount_due, status, penalty) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            BigDecimal totalAmount = credit.getAmount().add(credit.getInterestAmount());
            int months = credit.getDurationMonths();
            BigDecimal monthlyPayment = totalAmount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

            LocalDate dueDate = credit.getStartDate().plusMonths(1);

            for (int i = 0; i < months; i++) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    CreditSchedule schedule = new CreditSchedule(
                            credit.getId(),
                            dueDate,
                            monthlyPayment,
                            CreditSchedule.PaymentStatus.UNPAID,
                            BigDecimal.ZERO
                    );

                    stmt.setObject(1, schedule.getId());
                    stmt.setObject(2, schedule.getCreditId());
                    stmt.setDate(3, Date.valueOf(schedule.getDueDate()));
                    stmt.setBigDecimal(4, schedule.getAmountDue());
                    stmt.setObject(5, schedule.getStatus().name(),java.sql.Types.OTHER);
                    stmt.setBigDecimal(6, schedule.getPenalty());

                    stmt.executeUpdate();
                }
                dueDate = dueDate.plusMonths(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating credit schedule", e);
        }
    }

    @Override
    public List<CreditSchedule> findDueSchedules(LocalDate today) {
        List<CreditSchedule> dueSchedules = new ArrayList<>();
        String sql = "SELECT cs.id, cs.credit_id, cs.due_date, cs.amount_due, cs.status, cs.penalty, c.account_id " +
                "FROM credit_schedule cs " +
                "JOIN credits c ON cs.credit_id = c.id " +
                "WHERE cs.due_date <= ? AND cs.status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));
            stmt.setString(2, CreditSchedule.PaymentStatus.UNPAID.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CreditSchedule schedule = new CreditSchedule(
                            (UUID) rs.getObject("credit_id"),
                            rs.getDate("due_date").toLocalDate(),
                            rs.getBigDecimal("amount_due"),
                            CreditSchedule.PaymentStatus.valueOf(rs.getString("status")),
                            rs.getBigDecimal("penalty")
                    );
                    schedule.setId((UUID) rs.getObject("id"));
                    schedule.setAccountId((UUID) rs.getObject("account_id"));
                    dueSchedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching due credit schedules", e);
        }

        return dueSchedules;
    }

    @Override
    public void update(CreditSchedule schedule) {
        String sql = "UPDATE credit_schedule SET due_date = ?, amount_due = ?, status = ?, penalty = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(schedule.getDueDate()));
            stmt.setBigDecimal(2, schedule.getAmountDue());
            stmt.setString(3, schedule.getStatus().name());
            stmt.setBigDecimal(4, schedule.getPenalty());
            stmt.setObject(5, schedule.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating credit schedule", e);
        }
    }



}
