package org.example.repository;

import org.example.model.Credit;
import org.example.model.CreditSchedule;

import java.time.LocalDate;
import java.util.List;

public interface CreditScheduleRepository {
    void generateSchedule(Credit credit);
    List<CreditSchedule> findDueSchedules(LocalDate today);
    void update(CreditSchedule schedule);
}
