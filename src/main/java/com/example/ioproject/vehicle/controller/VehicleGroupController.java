package com.example.ioproject.vehicle.controller;

import com.example.ioproject.vehicle.dto.VehicleGroupCreateDTO;
import com.example.ioproject.vehicle.repository.VehicleRepository;
import com.example.ioproject.vehicle.model.VehicleGroup;
import com.example.ioproject.vehicle.model.Vehicle;
import com.example.ioproject.vehicle.service.VehicleGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-groups")
public class VehicleGroupController {

    private final VehicleGroupService groupService;
    private final VehicleRepository vehicleRepository;

    public VehicleGroupController(VehicleGroupService groupService, VehicleRepository vehicleRepository) {
        this.groupService = groupService;
        this.vehicleRepository = vehicleRepository;
    }

    // Utwórz nową grupę
    @PostMapping
    public VehicleGroup createGroup(@RequestBody VehicleGroupCreateDTO dto) {
        return groupService.createGroup(dto.getName(), dto.getVehicleIds());
    }

    // Pobierz wszystkie grupy
    @GetMapping
    public List<VehicleGroup> getAllGroups() {
        return groupService.getAllGroups();
    }

    // Pobierz grupę po ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleGroup> getGroup(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Edytuj grupę
    @PutMapping("/{id}")
    public VehicleGroup updateGroup(@PathVariable Long id, @RequestBody VehicleGroupCreateDTO dto) {
        return groupService.updateGroup(id, dto.getName(), dto.getVehicleIds());
    }

    // Usuń grupę
    @DeleteMapping("/{id}")
    public void deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
    }

    // Pobierz auta z danej grupy
    @GetMapping("/{id}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehiclesInGroup(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(group -> ResponseEntity.ok().body(group.getVehicles().stream().toList()))
                .orElse(ResponseEntity.notFound().build());
    }
}
