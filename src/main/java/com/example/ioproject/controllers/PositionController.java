package com.example.ioproject.controllers;

import com.example.ioproject.dto.DaysRequestDTO;
import com.example.ioproject.dto.ItalyStaySummaryDto;
import com.example.ioproject.dto.TimelineBlockDto;
import com.example.ioproject.models.Position;
import com.example.ioproject.dto.PositionDetailsProjection;
import com.example.ioproject.services.PositionService;
import com.example.ioproject.services.TimeLineService;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
public class PositionController {

    private final PositionService positionService;
    private final TimeLineService timeLineService;

    public PositionController(PositionService positionService, TimeLineService timeLineService) {
        this.positionService = positionService;
        this.timeLineService = timeLineService;
    }

    @GetMapping("/get")
    public List<PositionDetailsProjection> getAllPositions() {
        return positionService.getAllPositions();
    }

    @GetMapping("/trucks")
    public List<PositionDetailsProjection> getTrucks() {
        return positionService.getTrucks();
    }

    @GetMapping("/trailers")
    public List<PositionDetailsProjection> getTrailers() {
        return positionService.getTrailers();
    }

    @GetMapping("/group/{groupId}")
    public List<PositionDetailsProjection> getGroupVehicles(@PathVariable Long groupId) {
        return positionService.getGroupVehicles(groupId);
    }

    @GetMapping("/countries")
    public Map<String, Long> getVehicleCountByCountry() {
        return positionService.getVehicleCountByCountry();
    }

    @GetMapping("/low-fuel")
    public List<PositionDetailsProjection> getLowFuelVehicles() {
        return positionService.getLowFuelVehicles();
    }

    @GetMapping("/by-country")
    public Map<String, List<PositionDetailsProjection>> getPositionsGroupedByCountry() {
        return positionService.getPositionsGroupedByCountry();
    }

    @GetMapping("/countries/types")
    public Map<String, Map<String, Long>> getVehicleCountByCountryAndType() {
        return positionService.getVehicleCountByCountryAndType();
    }

    @GetMapping("/history/today/{deviceId}")
    public Map<String, Object> getTodayHistoryForDevice(@PathVariable String deviceId) {
        return positionService.getTodayHistoryForDevice(deviceId);
    }

    @PostMapping("/history/italy")
    public List<ItalyStaySummaryDto> getItalyStaySummary(@RequestBody DaysRequestDTO req) {
        return positionService.getItalyTrucksStaySummary(req.getDays());
    }
    @GetMapping("/driver-timeline/{deviceId}/{yyyyMMdd}")
    public List<TimelineBlockDto> getTimeline(
            @PathVariable String deviceId,
            @PathVariable String yyyyMMdd
    ) {
        // JEDNA LINIJKA: wszystko dzieje się w serwisie
        return timeLineService.getTimeline(deviceId, yyyyMMdd);
    }

}
