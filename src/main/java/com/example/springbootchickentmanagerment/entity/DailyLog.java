package com.example.springbootchickentmanagerment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_logs")
public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_id", nullable = false)
    private Flock flock;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "mortality")
    private Integer mortality;

    @Column(name = "cull")
    private Integer cull;

    @Column(name = "feed_amount")
    private Double feedAmount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyLogDetail> details;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
