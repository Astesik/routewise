package com.example.ioproject.services;

import com.example.ioproject.dto.TimelineBlockDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TimeLineService {

    @Autowired
    private GpsService gpsService;

    public List<TimelineBlockDto> getTimeline(String deviceId, String yyyyMMdd) {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        LocalDate day = LocalDate.parse(yyyyMMdd, DateTimeFormatter.BASIC_ISO_DATE);
        ZonedDateTime dayStart = day.atStartOfDay(zone);
        ZonedDateTime dayEnd = day.atTime(LocalTime.MAX).atZone(zone);

        long fromTs = dayStart.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
        long toTs = dayEnd.withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();

        Map<String, Object> response = gpsService.getHistory(deviceId, fromTs, toTs);
        List<Map<String, Object>> positions = (List<Map<String, Object>>) response.get("positionList");

        String vehicleName = getVehicleNameFromPositions(positions);
        String driverName = getDriverNameFromPositions(positions);
        String country = getCountryFromPositions(positions);

        return generateTimelineBlocks(positions, deviceId, vehicleName, country, driverName, fromTs, toTs);
    }

    private String getVehicleNameFromPositions(List<Map<String, Object>> positions) {
        if (positions != null && !positions.isEmpty()) {
            Object name = positions.get(0).get("deviceName");
            if (name != null) return name.toString();
        }
        return "";
    }
    private String getDriverNameFromPositions(List<Map<String, Object>> positions) {
        if (positions != null && !positions.isEmpty()) {
            List<Map<String, Object>> drivers = (List<Map<String, Object>>) positions.get(0).get("drivers");
            if (drivers != null && !drivers.isEmpty()) {
                Object dname = drivers.get(0).get("name");
                if (dname != null) return dname.toString();
            }
        }
        return "";
    }
    private String getCountryFromPositions(List<Map<String, Object>> positions) {
        if (positions != null && !positions.isEmpty()) {
            Map<String, Object> country = (Map<String, Object>) positions.get(0).get("country");
            if (country != null) {
                Object code = country.get("code");
                if (code != null) return code.toString();
            }
        }
        return "";
    }

    public List<TimelineBlockDto> generateTimelineBlocks(
            List<Map<String, Object>> positions,
            String deviceId,
            String vehicleName,
            String country,
            String driverName,
            long fromTs,
            long toTs
    ) {
        final long MIN_BLOCK_SECONDS = 60L;
        List<TimelineBlockDto> blocks = new ArrayList<>();
        if (positions == null || positions.isEmpty()) {
            blocks.add(new TimelineBlockDto("NO_DATA", toTs - fromTs, fromTs, toTs, deviceId, vehicleName, country, driverName));
            return blocks;
        }
        positions.sort(Comparator.comparingLong(TimeLineService::extractTimestampSafe));

        long blockStart = fromTs;
        String blockType = getStatusType(positions.get(0));

        List<TimelineBlockDto> tempBlocks = new ArrayList<>();

        for (int i = 1; i <= positions.size(); i++) {
            boolean isLast = (i == positions.size());
            long currentTs = isLast ? toTs : extractTimestampSafe(positions.get(i));
            String currentType = isLast ? null : getStatusType(positions.get(i));

            if (isLast || !currentType.equals(blockType)) {
                long duration = currentTs - blockStart;
                tempBlocks.add(new TimelineBlockDto(blockType, duration, blockStart, currentTs, deviceId, vehicleName, country, driverName));
                if (!isLast) {
                    blockStart = currentTs; // **Koniec bloku to początek nowego**
                    blockType = currentType;
                }
            }
            // jeśli typ taki sam, przechodzimy dalej
        }

        // Drugi etap – zlepianie mikrobloków
        List<TimelineBlockDto> finalBlocks = new ArrayList<>();
        for (int i = 0; i < tempBlocks.size(); i++) {
            TimelineBlockDto block = tempBlocks.get(i);

            // Sklej mikroblok z poprzednim dużym blokiem
            if (block.getDuration() < MIN_BLOCK_SECONDS) {
                if (!finalBlocks.isEmpty()) {
                    TimelineBlockDto last = finalBlocks.get(finalBlocks.size() - 1);
                    finalBlocks.set(finalBlocks.size() - 1, new TimelineBlockDto(
                            last.getType(),
                            last.getDuration() + block.getDuration(),
                            last.getStartTs(),
                            block.getEndTs(),
                            deviceId, vehicleName, country, driverName
                    ));
                } else {
                    // Pierwszy mikroblok dnia
                    finalBlocks.add(block);
                }
            } else {
                // Jeśli poprzedni blok był tego samego typu, zlep
                if (!finalBlocks.isEmpty() && finalBlocks.get(finalBlocks.size() - 1).getType().equals(block.getType())) {
                    TimelineBlockDto last = finalBlocks.get(finalBlocks.size() - 1);
                    finalBlocks.set(finalBlocks.size() - 1, new TimelineBlockDto(
                            last.getType(),
                            last.getDuration() + block.getDuration(),
                            last.getStartTs(),
                            block.getEndTs(),
                            deviceId, vehicleName, country, driverName
                    ));
                } else {
                    finalBlocks.add(block);
                }
            }
        }

        if (finalBlocks.isEmpty()) {
            finalBlocks.add(new TimelineBlockDto("NO_DATA", toTs - fromTs, fromTs, toTs, deviceId, vehicleName, country, driverName));
        }
        return finalBlocks;
    }

    public static long extractTimestampSafe(Map<String, Object> pos) {
        try {
            Map<String, Object> dt = (Map<String, Object>) pos.get("dateTime");
            if (dt != null) {
                int year = (int) dt.get("year");
                int month = (int) dt.get("month");
                int day = (int) dt.get("day");
                int hour = (int) dt.get("hour");
                int minute = (int) dt.get("minute");
                int seconds = (int) dt.get("seconds");
                LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute, seconds);
                return ldt.toEpochSecond(ZoneOffset.UTC);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public static String getStatusType(Map<String, Object> pos) {
        try {
            List<Map<String, Object>> drivers = (List<Map<String, Object>>) pos.get("drivers");
            if (drivers != null && !drivers.isEmpty()) {
                String st = (String) drivers.get(0).get("worktype");
                if (st != null) {
                    switch (st.toUpperCase()) {
                        case "DRIVING": return "DRIVE";
                        case "WORKING": return "WORK";
                        case "RESTING": return "REST";
                        case "SHORT_BREAK": return "REST";
                        default: return st.toUpperCase();
                    }
                }
            }
        } catch (Exception ignored) {}
        return "NO_DATA";
    }
}
