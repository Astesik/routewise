package com.example.ioproject.repair.repository;

import com.example.ioproject.repair.model.RepairComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepairCommentRepository extends JpaRepository<RepairComment, Long> {
    List<RepairComment> findByRepairIdOrderByCreatedAtAsc(Long repairId);
}
