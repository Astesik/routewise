// WeekRepairsDto.java
package com.example.ioproject.dto;

import java.time.LocalDate;
import java.util.List;

public class WeekRepairsDto {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<RepairDto> repairs;

    // Konstruktor, gettery, settery
    public WeekRepairsDto(LocalDate weekStart, LocalDate weekEnd, List<RepairDto> repairs) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.repairs = repairs;
    }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }
    public List<RepairDto> getRepairs() { return repairs; }
    public void setRepairs(List<RepairDto> repairs) { this.repairs = repairs; }
}
