package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.entity.Schedule;
import com.example.springbootchickentmanagerment.enums.ScheduleStatus;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.ScheduleRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByFlockId(Long flockId) {
        List<Schedule> schedules = scheduleRepository.findByFlockId(flockId);

        // Eager load flock để tránh LazyInitializationException
        for (Schedule schedule : schedules) {
            if (schedule.getFlock() != null) {
                // Force load để đảm bảo dữ liệu được fetch trước khi controller trả về
                entityManager.refresh(schedule.getFlock());
            }
        }

        return schedules;
    }

    @Transactional(readOnly = true)
    public List<Schedule> getTodaySchedules() {
        return scheduleRepository.findPendingSchedulesUpToDate(LocalDate.now());
    }

    @Transactional
    public void completeSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Lịch trình không tồn tại"));

        schedule.setStatus(ScheduleStatus.DONE);
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void skipSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Lịch trình không tồn tại"));

        schedule.setStatus(ScheduleStatus.SKIPPED);
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getUpcomingSchedules(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        return scheduleRepository.findSchedulesBetweenDates(LocalDate.now(), endDate);
    }
}