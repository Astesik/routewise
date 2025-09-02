package com.example.ioproject.position.service;

import com.example.ioproject.position.dto.PositionDetailsProjection;
import com.example.ioproject.vehicle.repository.VehicleRepository;
import com.example.ioproject.vehicle.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VignetteCheckerService {

    private final PositionService positionService;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VignetteCheckerService(PositionService positionService, VehicleRepository vehicleRepository) {
        this.positionService = positionService;
        this.vehicleRepository = vehicleRepository;
    }

    // Co 5 minut!
    public void checkUkVignettes() {
        List<PositionDetailsProjection> ukTrucks = positionService.getAllPositions().stream()
                .filter(pos -> "GB".equalsIgnoreCase(pos.getCountryCode()))
                .filter(pos -> "truck".equalsIgnoreCase(pos.getType()))
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();

        System.out.println("[WINIETA] Sprawdzanie winiet");
        for (PositionDetailsProjection pos : ukTrucks) {
            String deviceId = pos.getDeviceId();
            Optional<Vehicle> vehicleOpt = vehicleRepository.findByDevice_SerialNumber(deviceId);
            if (vehicleOpt.isPresent()) {
                Vehicle vehicle = vehicleOpt.get();
                LocalDate vignetteUntil = vehicle.getUkVignetteValidUntil();
                if (vignetteUntil == null) {
                    System.out.println("🚨 [WINIETA] BRAK winiety UK dla pojazdu: " + vehicle.getLicensePlate());
                } else if (vignetteUntil.isBefore(today) || vignetteUntil.isEqual(today)) {
                    System.out.println("❌ [WINIETA] Winieta UK WYGASŁA dla: " + vehicle.getLicensePlate() + " (do: " + vignetteUntil + ")");
                } else if (vignetteUntil.minusDays(7).isBefore(today)) {
                    System.out.println("⚠️ [WINIETA] Winieta UK kończy się wkrótce (" + vignetteUntil + ") dla: " + vehicle.getLicensePlate());
                } else {
                    System.out.println("✅ [WINIETA] OK: " + vehicle.getLicensePlate() + " winieta ważna do " + vignetteUntil);
                }
            } else {
                System.out.println("❓ [WINIETA] Nie znaleziono pojazdu w bazie dla deviceId: " + deviceId);
            }
        }
    }
}
