// services/RepairService.java
package com.example.ioproject.repair.service;

import com.example.ioproject.repair.dto.RepairDto;
import com.example.ioproject.repair.dto.WeekRepairsDto;
import com.example.ioproject.repair.model.Repair;
import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.place.model.Place;
import com.example.ioproject.repair.repository.RepairRepository;
import com.example.ioproject.vehicle.repository.VehicleRepository;
import com.example.ioproject.place.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairService {

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PlaceRepository placeRepository;

    public List<RepairDto> getAllRepairs() {
        return repairRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RepairDto> getRepairsByVehicleId(Long vehicleId) {
        return repairRepository.findByVehicleId(vehicleId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public RepairDto getRepair(Long id) {
        return repairRepository.findById(id).map(this::toDto).orElse(null);
    }

    public RepairDto saveRepair(RepairDto dto) {
        Repair repair = new Repair();
        if (dto.getId() != null) repair.setId(dto.getId());

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId()).orElse(null);
        repair.setVehicle(vehicle);

        repair.setDescription(dto.getDescription());
        repair.setPlannedDate(dto.getPlannedDate());
        repair.setPlannedTime(dto.getPlannedTime());
        repair.setStatus(dto.getStatus());

        Place place = placeRepository.findById(dto.getPlaceId()).orElse(null);
        repair.setPlace(place);

        Repair saved = repairRepository.save(repair);
        return toDto(saved);
    }

    public void deleteRepair(Long id) {
        repairRepository.deleteById(id);
    }

    private RepairDto toDto(Repair repair) {
        RepairDto dto = new RepairDto();
        dto.setId(repair.getId());
        dto.setVehicleId(repair.getVehicle().getId());
        dto.setDescription(repair.getDescription());
        dto.setPlannedDate(repair.getPlannedDate());
        dto.setPlannedTime(repair.getPlannedTime());
        dto.setStatus(repair.getStatus());

        // DODAJ TO:
        if (repair.getVehicle() != null) {
            dto.setLicensePlates(repair.getVehicle().getLicensePlate());
        }

        if (repair.getPlace() != null) {
            dto.setPlaceId(repair.getPlace().getId());
            dto.setPlaceName(repair.getPlace().getName());
        }
        return dto;
    }

    public List<RepairDto> getRepairsByStatus(String status) {
        return repairRepository.findByStatus(status).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<WeekRepairsDto> getRepairsGroupedByWeek() {
        // Załaduj wszystkie naprawy
        List<RepairDto> allRepairs = getAllRepairs(); // zakładam że masz taką metodę

        if (allRepairs.isEmpty()) return List.of();

        // Znajdź najstarszą i najnowszą datę
        LocalDate minDate = allRepairs.stream().map(RepairDto::getPlannedDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = allRepairs.stream().map(RepairDto::getPlannedDate).max(LocalDate::compareTo).orElse(LocalDate.now());

        // Ustal pierwszy poniedziałek (od minDate) i ostatnią niedzielę (od maxDate)
        LocalDate start = minDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = maxDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Mapowanie: tydzień (start) => lista napraw
        Map<LocalDate, List<RepairDto>> weekMap = new HashMap<>();

        for (RepairDto repair : allRepairs) {
            LocalDate date = repair.getPlannedDate();
            LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            weekMap.computeIfAbsent(weekStart, k -> new ArrayList<>()).add(repair);
        }

        // Stwórz listę tygodni od końca do początku
        List<WeekRepairsDto> result = new ArrayList<>();
        LocalDate week = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        while (!week.isBefore(start)) {
            LocalDate weekEnd = week.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            List<RepairDto> repairs = weekMap.getOrDefault(week, new ArrayList<>());
            // Sortuj naprawy w tygodniu po dacie rosnąco
            repairs.sort(Comparator.comparing(RepairDto::getPlannedDate).thenComparing(RepairDto::getPlannedTime));
            result.add(new WeekRepairsDto(week, weekEnd, repairs));
            week = week.minusWeeks(1);
        }
        return result;
    }
}
