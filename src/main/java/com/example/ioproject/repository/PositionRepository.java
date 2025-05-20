package com.example.ioproject.repository;

import com.example.ioproject.models.Position;
import com.example.ioproject.dto.PositionDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByDeviceId(String deviceId);

    @Query(value = """
        SELECT 
            vp.id as id,
            vp.device_id as deviceId,
            d.device_name as deviceName,
            vp.country_code as countryCode,
            vp.latitude as latitude,
            vp.longitude as longitude,
            vp.ignition_state as ignitionState,
            vp.speed as speed,
            vp.heading as heading,
            d.type as type,
            vp.fuel_level_perc as fuelLevelPerc,
            vp.driver_slot_0 as driverSlot0,
            dr0.first_name as driver0FirstName,
            dr0.last_name as driver0LastName,
            vp.driver_slot_1 as driverSlot1,
            dr1.first_name as driver1FirstName,
            dr1.last_name as driver1LastName,
            vp.updated_at as updatedAt
        FROM vehicle_positions vp
        LEFT JOIN devices d ON d.serial_number = vp.device_id
        LEFT JOIN drivers dr0 ON dr0.tachoid = CAST(vp.driver_slot_0 AS BIGINT)
        LEFT JOIN drivers dr1 ON dr1.tachoid = CAST(vp.driver_slot_1 AS BIGINT)
        """, nativeQuery = true)
    List<PositionDetailsProjection> getPositionDetails();
}
