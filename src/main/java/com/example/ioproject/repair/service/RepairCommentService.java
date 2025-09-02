package com.example.ioproject.repair.service;

import com.example.ioproject.auth.model.User;
import com.example.ioproject.auth.repository.UserRepository;
import com.example.ioproject.repair.dto.RepairCommentDto;
import com.example.ioproject.repair.model.Repair;
import com.example.ioproject.repair.model.RepairComment;
import com.example.ioproject.repair.repository.RepairCommentRepository;
import com.example.ioproject.repair.repository.RepairRepository;
import com.example.ioproject.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RepairCommentService {

    private final RepairCommentRepository commentRepo;
    private final RepairRepository repairRepo;
    private final UserRepository userRepo;

    public RepairCommentService(RepairCommentRepository commentRepo,
                                RepairRepository repairRepo,
                                UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.repairRepo = repairRepo;
        this.userRepo = userRepo;
    }

    public List<RepairCommentDto> list(Long repairId) {
        Objects.requireNonNull(repairId, "repairId is required");
        return commentRepo.findByRepairIdOrderByCreatedAtAsc(repairId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Dodaje komentarz na podstawie aktualnie zalogowanego użytkownika.
     * Brak logiki w kontrolerze — całość tutaj.
     */
    @Transactional
    public RepairCommentDto addFromCurrentUser(Long repairId, RepairCommentDto dto) {
        Objects.requireNonNull(repairId, "repairId is required");
        if (dto == null || dto.getText() == null || dto.getText().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pole 'text' jest wymagane");
        }

        Long currentUserId = getCurrentUserIdOrThrow();
        User author = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Użytkownik nie znaleziony"));

        Repair repair = repairRepo.findById(repairId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Naprawa nie istnieje"));

        RepairComment c = new RepairComment();
        c.setRepair(repair);
        c.setAuthor(author);
        c.setText(dto.getText().trim());
        if (c.getCreatedAt() == null) {
            c.setCreatedAt(Instant.now());
        }

        RepairComment saved = commentRepo.save(c);
        return toDto(saved);
    }

    // ===== helpers =====

    private Long getCurrentUserIdOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji");
        }
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetailsImpl udi) {
            return udi.getId();
        }

        // fallback po username (np. gdy masz inny typ principal)
        String username = auth.getName();
        if (username != null && !username.isBlank()) {
            Optional<User> u = userRepo.findByUsername(username);
            if (u.isPresent()) return u.get().getId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nie można ustalić użytkownika");
    }

    private RepairCommentDto toDto(RepairComment c) {
        RepairCommentDto dto = new RepairCommentDto();
        dto.setId(c.getId());
        dto.setRepairId(c.getRepair().getId());

        User u = c.getAuthor();
        if (u != null) {
            dto.setAuthorUserId(u.getId());
            dto.setAuthorUsername(u.getUsername());
            String full = ((u.getFirstName() == null ? "" : u.getFirstName().trim()) + " " +
                    (u.getLastName()  == null ? "" : u.getLastName().trim())).trim();
            dto.setAuthorFullName(full.isBlank() ? null : full);
        }

        dto.setText(c.getText());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
