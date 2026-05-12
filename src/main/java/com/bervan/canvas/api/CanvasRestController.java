package com.bervan.canvas.api;

import com.bervan.canvas.Canvas;
import com.bervan.canvas.CanvasService;
import com.bervan.common.config.EntityConfigValidator;
import com.bervan.common.controller.BaseOwnedController;
import com.bervan.common.controller.ValidationErrorResponse;
import com.bervan.common.mapper.BervanDTOMapper;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/canvas")
@RolesAllowed("USER")
public class CanvasRestController extends BaseOwnedController<Canvas, UUID> {

    private final CanvasService canvasService;

    public CanvasRestController(CanvasService canvasService, BervanDTOMapper mapper, EntityConfigValidator validator) {
        super(canvasService, mapper, validator, "Canvas");
        this.canvasService = canvasService;
    }

    @GetMapping
    public ResponseEntity<Page<CanvasDto>> list(
            @RequestParam MultiValueMap<String, String> allParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return super.search(allParams, page, size, CanvasDto.class, Canvas.class);
    }

    @GetMapping("/categories")
    public ResponseEntity<java.util.Set<String>> getCategories() {
        return ResponseEntity.ok(canvasService.findAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanvasDetailDto> getCanvas(@PathVariable UUID id) {
        return super.getById(id, CanvasDetailDto.class);
    }

    @PostMapping
    public ResponseEntity<?> createCanvas(@RequestBody CreateCanvasRequest req) {
        return super.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCanvas(@PathVariable UUID id, @RequestBody UpdateCanvasRequest req) {
        req.setId(id);
        return super.update(req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCanvas(@PathVariable UUID id) {
        return super.delete(id);
    }
}
