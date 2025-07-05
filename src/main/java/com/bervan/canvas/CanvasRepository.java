package com.bervan.canvas;

import com.bervan.history.model.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CanvasRepository extends BaseRepository<Canvas, UUID> {

    List<Canvas> findByDeletedFalseAndOwnersId(UUID loggedUserId);

    List<Canvas> findByNameAndDeletedFalseAndOwnersId(String name, UUID loggedUserId);

    @Query("select distinct c.category from Canvas c where c.deleted != true")
    Set<String> findAllCategories();

}
