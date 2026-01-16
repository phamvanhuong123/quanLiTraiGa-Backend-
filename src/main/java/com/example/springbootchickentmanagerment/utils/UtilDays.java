package com.example.springbootchickentmanagerment.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UtilDays {
    public static Long getDaysBetween(LocalDate day1, LocalDate  day2) {
        return Math.abs(ChronoUnit.DAYS.between(day1, day2));
    }
}
