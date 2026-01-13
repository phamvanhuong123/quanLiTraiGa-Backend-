package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.Schedule.CreateScheduleDTO;
import com.example.springbootchickentmanagerment.dto.Schedule.ScheduleStats;
import com.example.springbootchickentmanagerment.entity.Flock;
import com.example.springbootchickentmanagerment.entity.Schedule;
import com.example.springbootchickentmanagerment.enums.ScheduleStatus;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.FlockRepository;
import com.example.springbootchickentmanagerment.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final FlockRepository flockRepository;

    @Transactional
    public Schedule createSchedule(CreateScheduleDTO dto) {
        Flock flock = flockRepository.findById(dto.getFlockId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy đàn gà"));

        // Kiểm tra ngày schedule không được trước ngày nhập đàn
        if (dto.getScheduledDate().isBefore(flock.getImportDate())) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    "Ngày lịch trình không được trước ngày nhập đàn");
        }

        Schedule schedule = Schedule.builder()
                .flock(flock)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .scheduledDate(dto.getScheduledDate())
                .status(ScheduleStatus.PENDING)
                .build();

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule updateSchedule(Long id, CreateScheduleDTO dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch trình"));

        Flock flock = flockRepository.findById(dto.getFlockId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy đàn gà"));

        // Kiểm tra ngày schedule không được trước ngày nhập đàn
        if (dto.getScheduledDate().isBefore(flock.getImportDate())) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    "Ngày lịch trình không được trước ngày nhập đàn");
        }

        // Cập nhật thông tin
        schedule.setFlock(flock);
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setScheduledDate(dto.getScheduledDate());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch trình"));

        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByFlockId(Long flockId) {
        return scheduleRepository.findByFlockId(flockId);
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
    public List<Schedule> getTodaySchedules() {
        return scheduleRepository.findPendingSchedulesUpToDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Schedule> getUpcomingSchedules(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        return scheduleRepository.findSchedulesBetweenDates(LocalDate.now(), endDate);
    }

    @Transactional(readOnly = true)
    public List<Schedule> getOverdueSchedules() {
        LocalDate today = LocalDate.now();
        return scheduleRepository.findPendingSchedulesUpToDate(today);
    }

    @Transactional(readOnly = true)
    public ScheduleStats getScheduleStats() {
        LocalDate today = LocalDate.now();

        long totalSchedules = scheduleRepository.count();
        long pendingCount = scheduleRepository.countByStatus(ScheduleStatus.PENDING);
        long doneCount = scheduleRepository.countByStatus(ScheduleStatus.DONE);
        long skippedCount = scheduleRepository.countByStatus(ScheduleStatus.SKIPPED);
        long overdueCount = scheduleRepository.countOverdueSchedules(today);

        return ScheduleStats.builder()
                .totalSchedules(totalSchedules)
                .pendingSchedules(pendingCount)
                .doneSchedules(doneCount)
                .skippedSchedules(skippedCount)
                .overdueSchedules(overdueCount)
                .build();
    }
}