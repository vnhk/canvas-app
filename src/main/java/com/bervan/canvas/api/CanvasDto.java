package com.bervan.canvas.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanvasDto {
    private UUID id;
    private String name;
    private String category;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
}
