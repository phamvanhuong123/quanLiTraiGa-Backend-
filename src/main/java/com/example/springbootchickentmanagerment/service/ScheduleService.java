package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.entity.Schedule;
import com.example.springbootchickentmanagerment.enums.ScheduleStatus;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getSchedulesByFlockId(Long flockId) {
        return scheduleRepository.findByFlockId(flockId);
    }

    public List<Schedule> getTodaySchedules() {
        return scheduleRepository.findPendingSchedulesUpToDate(LocalDate.now());
    }

    public void completeSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Lịch trình không tồn tại"));
        
        schedule.setStatus(ScheduleStatus.DONE);
        scheduleRepository.save(schedule);
    }

    public void skipSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Lịch trình không tồn tại"));
        
        schedule.setStatus(ScheduleStatus.SKIPPED);
        scheduleRepository.save(schedule);
    }

    public List<Schedule> getUpcomingSchedules(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        return scheduleRepository.findSchedulesBetweenDates(LocalDate.now(), endDate);
    }
}