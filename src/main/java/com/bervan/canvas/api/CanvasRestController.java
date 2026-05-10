package com.bervan.canvas.api;

import com.bervan.canvas.Canvas;
import com.bervan.canvas.CanvasRepository;
import com.bervan.canvas.CanvasService;
import com.bervan.common.service.AuthService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/canvas")
@RolesAllowed("USER")
public class CanvasRestController {

    private final CanvasService canvasService;
    private final CanvasRepository canvasRepository;

    public CanvasRestController(CanvasService canvasService, CanvasRepository canvasRepository) {
        this.canvasService = canvasService;
        this.canvasRepository = canvasRepository;
    }

    @GetMapping
    public ResponseEntity<List<CanvasDto>> listCanvases() {
        UUID userId = AuthService.getLoggedUserId();
        List<CanvasDto> dtos = canvasRepository.findByDeletedFalseAndOwnersId(userId).stream()
                .map(c -> new CanvasDto(c.getId(), c.getName(), c.getCategory(),
                        c.getCreationDate(), c.getModificationDate()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getCategories() {
        return ResponseEntity.ok(canvasService.findAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanvasDetailDto> getCanvas(@PathVariable UUID id) {
        UUID userId = AuthService.getLoggedUserId();
        return canvasRepository.findByDeletedFalseAndOwnersId(userId).stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(c -> ResponseEntity.ok(new CanvasDetailDto(
                        c.getId(), c.getName(), c.getCategory(), c.getContent(),
                        c.getCreationDate(), c.getModificationDate())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CanvasDto> createCanvas(@RequestBody CreateCanvasRequest req) {
        Canvas canvas = new Canvas();
        canvas.setId(UUID.randomUUID());
        canvas.setName(req.getName());
        canvas.setCategory(req.getCategory());
        canvas.setContent("");
        canvas.setCreationDate(LocalDateTime.now());
        canvas.setModificationDate(LocalDateTime.now());
        Canvas saved = canvasService.save(canvas);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CanvasDto(saved.getId(), saved.getName(), saved.getCategory(),
                        saved.getCreationDate(), saved.getModificationDate()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CanvasDetailDto> updateCanvas(@PathVariable UUID id,
                                                        @RequestBody UpdateCanvasRequest req) {
        UUID userId = AuthService.getLoggedUserId();
        Canvas canvas = canvasRepository.findByDeletedFalseAndOwnersId(userId).stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (canvas == null) return ResponseEntity.notFound().build();

        if (req.getName() != null) canvas.setName(req.getName());
        if (req.getCategory() != null) canvas.setCategory(req.getCategory());
        if (req.getContent() != null) canvas.setContent(req.getContent());
        canvas.setModificationDate(LocalDateTime.now());

        Canvas saved = canvasService.save(canvas);
        return ResponseEntity.ok(new CanvasDetailDto(
                saved.getId(), saved.getName(), saved.getCategory(), saved.getContent(),
                saved.getCreationDate(), saved.getModificationDate()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCanvas(@PathVariable UUID id) {
        UUID userId = AuthService.getLoggedUserId();
        Canvas canvas = canvasRepository.findByDeletedFalseAndOwnersId(userId).stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (canvas == null) return ResponseEntity.notFound().build();
        canvasService.delete(canvas);
        return ResponseEntity.noContent().build();
    }
}
