package com.example.ioproject.repair.controller;

import com.example.ioproject.repair.dto.RepairCommentDto;
import com.example.ioproject.repair.dto.RepairDto;
import com.example.ioproject.repair.dto.WeekRepairsDto;
import com.example.ioproject.repair.service.RepairCommentService;
import com.example.ioproject.repair.service.RepairService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repairs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RepairController {

    private final RepairService repairService;
    private final RepairCommentService repairCommentService;

    public RepairController(RepairService repairService,
                            RepairCommentService repairCommentService) {
        this.repairService = repairService;
        this.repairCommentService = repairCommentService;
    }

    // ---------- Repairs ----------

    @GetMapping("/get")
    public List<RepairDto> getAllRepairs() {
        return repairService.getAllRepairs();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<RepairDto> getRepairsByVehicle(@PathVariable Long vehicleId) {
        return repairService.getRepairsByVehicleId(vehicleId);
    }

    @GetMapping("/status/{status}")
    public List<RepairDto> getRepairsByStatus(@PathVariable String status) {
        return repairService.getRepairsByStatus(status);
    }

    @GetMapping("/{id}")
    public RepairDto getRepair(@PathVariable Long id) {
        return repairService.getRepair(id);
    }

    @PostMapping
    public RepairDto createRepair(@Valid @RequestBody RepairDto dto) {
        return repairService.saveRepair(dto);
    }

    @PutMapping("/{id}")
    public RepairDto updateRepair(@PathVariable Long id, @Valid @RequestBody RepairDto dto) {
        dto.setId(id);
        return repairService.saveRepair(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteRepair(@PathVariable Long id) {
        repairService.deleteRepair(id);
    }

    @GetMapping("/grouped-by-week")
    public List<WeekRepairsDto> getRepairsGroupedByWeek() {
        return repairService.getRepairsGroupedByWeek();
    }

    // ---------- Comments ----------

    @GetMapping("/{id}/comments")
    public List<RepairCommentDto> listComments(@PathVariable Long id) {
        return repairCommentService.list(id);
    }

    /**
     * Dodanie komentarza — kontroler nie posiada logiki biznesowej:
     * - walidacja pola 'text' odbywa się adnotacją @NotBlank na DTO,
     * - rozpoznanie bieżącego użytkownika i zapis autora są w serwisie.
     */
    @PostMapping("/{id}/comments")
    public RepairCommentDto addComment(@PathVariable Long id,
                                       @Valid @RequestBody RepairCommentDto dto) {
        return repairCommentService.addFromCurrentUser(id, dto);
    }
}
