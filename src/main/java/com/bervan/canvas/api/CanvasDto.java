package com.bervan.canvas.api;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.canvas.Canvas;
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
public class CanvasDto implements BaseDTO<UUID> {
    private UUID id;
    private String name;
    private String category;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        @SuppressWarnings("unchecked")
        Class<? extends BaseModel<UUID>> t = (Class<? extends BaseModel<UUID>>)(Class<?>) Canvas.class;
        return t;
    }
}
