package com.bervan.streamingapp.view;

import com.bervan.common.AbstractTableView;
import com.bervan.core.model.BervanLogger;
import com.bervan.streamingapp.Canvas;
import com.bervan.streamingapp.CanvasService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public abstract class AbstractCanvasPagesView extends AbstractTableView<UUID, Canvas> {
    public static final String ROUTE_NAME = "/canvas-app/all-canvas-pages";
    private final CanvasService service;

    public AbstractCanvasPagesView(@Autowired CanvasService service, BervanLogger log) {
        super(new CanvasAppPageLayout(false, null), service, log, Canvas.class);
        this.service = service;
        renderCommonComponents();
    }

    @Override
    protected Grid<Canvas> getGrid() {
        Grid<Canvas> grid = new Grid<>(Canvas.class, false);
        buildGridAutomatically(grid);

        return grid;
    }

    @Override
    protected void preColumnAutoCreation(Grid<Canvas> grid) {
        grid.addComponentColumn(entity -> {
                    Icon linkIcon = new Icon(VaadinIcon.LINK);
                    linkIcon.getStyle().set("cursor", "pointer");
                    return new Anchor(ROUTE_NAME + "/" + entity.getName(), new HorizontalLayout(linkIcon));
                }).setKey("link")
                .setWidth("10px")
                .setResizable(false);
    }
}