package com.example.ioproject.repository;

import com.example.ioproject.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findBySerialNumber(String serialNumber);

    @Query("SELECT d FROM Device d JOIN Position p ON d.serialNumber = p.deviceId " +
            "WHERE d.type = 'truck' AND p.countryCode = :country " +
            "AND p.updatedAt = (SELECT MAX(p2.updatedAt) FROM Position p2 WHERE p2.deviceId = p.deviceId)")
    List<Device> findTrucksInCountry(@Param("country") String country);
}
