package com.bervan.streamingapp;

import com.bervan.common.search.SearchService;
import com.bervan.common.service.AuthService;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CanvasService extends BaseService<UUID, Canvas> {
    private final CanvasRepository repository;
    private final HistoryCanvasRepository historyRepository;
    private final BervanLogger logger;

    public CanvasService(CanvasRepository repository,
                         HistoryCanvasRepository historyRepository,
                         SearchService searchService,
                         BervanLogger logger) {
        super(repository, searchService);
        this.repository = repository;
        this.historyRepository = historyRepository;
        this.logger = logger;
    }

    @Override
    public void save(List<Canvas> data) {
        repository.saveAll(data);
    }

    public Canvas save(Canvas canvas) {
        return repository.save(canvas);
    }

    @Override
    @PostFilter("(T(com.bervan.common.service.AuthService).hasAccess(filterObject.owners))")
    public Set<Canvas> load() {
        return new HashSet<>(repository.findByDeletedFalseAndOwnersId(AuthService.getLoggedUserId()));
    }

    @PostFilter("(T(com.bervan.common.service.AuthService).hasAccess(filterObject.owners))")
    public List<Canvas> loadByName(String canvasName) {
        return repository.findByNameAndDeletedFalseAndOwnersId(canvasName, AuthService.getLoggedUserId());
    }

    @Override
    public void delete(Canvas item) {
        item.setDeleted(true);
        save(item);
    }

    @PostFilter("(T(com.bervan.common.service.AuthService).hasAccess(filterObject.owners))")
    public List<HistoryCanvas> loadHistory() {
        return historyRepository.findAll();
    }

}
