package com.bervan.streamingapp;

import com.bervan.history.model.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface CanvasRepository extends BaseRepository<Canvas, UUID> {

    List<Canvas> findByDeletedFalseAndOwnersId(UUID loggedUserId);

    List<Canvas> findByNameAndDeletedFalseAndOwnersId(String name, UUID loggedUserId);
}
