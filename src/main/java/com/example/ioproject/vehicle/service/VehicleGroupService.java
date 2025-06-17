package com.example.ioproject.vehicle.service;

import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.vehicle.model.VehicleGroup;
import com.example.ioproject.vehicle.repository.VehicleGroupRepository;
import com.example.ioproject.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VehicleGroupService {

    private final VehicleGroupRepository groupRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleGroupService(VehicleGroupRepository groupRepository, VehicleRepository vehicleRepository) {
        this.groupRepository = groupRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleGroup createGroup(String name, List<Long> vehicleIds) {
        VehicleGroup group = new VehicleGroup();
        group.setName(name);

        if (vehicleIds != null && !vehicleIds.isEmpty()) {
            Set<Vehicle> vehicles = new HashSet<>(vehicleRepository.findAllById(vehicleIds));
            group.setVehicles(vehicles);
        }

        return groupRepository.save(group);
    }

    public List<VehicleGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public Optional<VehicleGroup> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional
    public VehicleGroup updateGroup(Long groupId, String name, List<Long> vehicleIds) {
        VehicleGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupa nie istnieje"));

        group.setName(name);

        if (vehicleIds != null) {
            Set<Vehicle> vehicles = new HashSet<>(vehicleRepository.findAllById(vehicleIds));
            group.setVehicles(vehicles);
        }

        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
    }
}
