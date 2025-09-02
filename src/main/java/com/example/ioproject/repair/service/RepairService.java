// src/main/java/com/example/ioproject/repair/service/RepairService.java
package com.example.ioproject.repair.service;

import com.example.ioproject.repair.dto.RepairDto;
import com.example.ioproject.repair.dto.RepairItemDto;
import com.example.ioproject.repair.dto.WeekRepairsDto;
import com.example.ioproject.repair.exception.RepairNotFoundException;
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

    @Autowired private RepairRepository repairRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private PlaceRepository placeRepository;

    public List<RepairDto> getAllRepairs() {
        return repairRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RepairDto> getRepairsByVehicleId(Long vehicleId) {
        return repairRepository.findByVehicleId(vehicleId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public RepairDto getRepair(Long id) {
        return repairRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RepairNotFoundException("Repair not found: " + id));
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

        Place place = (dto.getPlaceId() != null) ? placeRepository.findById(dto.getPlaceId()).orElse(null) : null;
        repair.setPlace(place);

        repair.setCreatedBy(dto.getCreatedBy());

        // NOWE: items jako lista (zabezpiecz null)
        List<RepairItemDto> items = dto.getItems() != null ? dto.getItems() : List.of();
        repair.setItems(new ArrayList<>(items));

        Repair saved = repairRepository.save(repair);
        return toDto(saved);
    }

    public RepairDto replaceItems(Long repairId, List<RepairItemDto> items) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RepairNotFoundException("Repair not found: " + repairId));
        repair.setItems(items != null ? new ArrayList<>(items) : new ArrayList<>());
        return toDto(repairRepository.save(repair));
    }

    public void deleteRepair(Long id) {
        if (!repairRepository.existsById(id)) {
            throw new RepairNotFoundException("Repair not found: " + id);
        }
        repairRepository.deleteById(id);
    }

    public List<RepairDto> getRepairsByStatus(String status) {
        return repairRepository.findByStatus(status).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<WeekRepairsDto> getRepairsGroupedByWeek() {
        List<RepairDto> allRepairs = getAllRepairs();
        if (allRepairs.isEmpty()) return List.of();

        LocalDate minDate = allRepairs.stream().map(RepairDto::getPlannedDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = allRepairs.stream().map(RepairDto::getPlannedDate).max(LocalDate::compareTo).orElse(LocalDate.now());

        LocalDate start = minDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end   = maxDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Map<LocalDate, List<RepairDto>> weekMap = new HashMap<>();
        for (RepairDto r : allRepairs) {
            LocalDate weekStart = r.getPlannedDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            weekMap.computeIfAbsent(weekStart, k -> new ArrayList<>()).add(r);
        }

        List<WeekRepairsDto> result = new ArrayList<>();
        LocalDate week = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        while (!week.isBefore(start)) {
            LocalDate weekEnd = week.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            List<RepairDto> rep = weekMap.getOrDefault(week, new ArrayList<>());
            rep.sort(Comparator.comparing(RepairDto::getPlannedDate).thenComparing(RepairDto::getPlannedTime));
            result.add(new WeekRepairsDto(week, weekEnd, rep));
            week = week.minusWeeks(1);
        }
        return result;
    }

    private RepairDto toDto(Repair repair) {
        RepairDto dto = new RepairDto();
        dto.setId(repair.getId());

        if (repair.getVehicle() != null) {
            dto.setVehicleId(repair.getVehicle().getId());
            dto.setLicensePlates(repair.getVehicle().getLicensePlate());
        }

        dto.setDescription(repair.getDescription());
        dto.setPlannedDate(repair.getPlannedDate());
        dto.setPlannedTime(repair.getPlannedTime());
        dto.setStatus(repair.getStatus());
        dto.setCreatedBy(repair.getCreatedBy());

        if (repair.getPlace() != null) {
            dto.setPlaceId(repair.getPlace().getId());
            dto.setPlaceName(repair.getPlace().getName());
        }

        // NOWE: przeniesienie listy z encji do DTO
        List<RepairItemDto> items = repair.getItems() != null ? repair.getItems() : List.of();
        dto.setItems(new ArrayList<>(items));

        return dto;
    }


}
