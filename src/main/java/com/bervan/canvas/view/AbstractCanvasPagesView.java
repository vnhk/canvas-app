package com.bervan.canvas.view;

import com.bervan.canvas.Canvas;
import com.bervan.canvas.CanvasService;
import com.bervan.common.AbstractBervanEntityView;
import com.bervan.common.EmptyLayout;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.model.Operator;
import com.bervan.common.search.model.SearchOperation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public abstract class AbstractCanvasPagesView extends AbstractBervanEntityView<UUID, Canvas> {
    public static final String ROUTE_NAME = "/canvas-app/all-canvas-pages";

    public AbstractCanvasPagesView(@Autowired CanvasService service) {
        super(new EmptyLayout(), service, Canvas.class);
        // Create main horizontal layout filling the whole view
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.addClassName("canvas-main-layout");

        // Main canvas view area
        CanvasComponent canvasComponent = new CanvasComponent(service, null); // initially null
        canvasComponent.setSizeFull();
        canvasComponent.addClassName("canvas-main-canvas");

        // Middle panel: list of notebooks
        VerticalLayout middlePanel = new VerticalLayout();
        middlePanel.setWidth("250px");
        middlePanel.addClassName("canvas-middle-panel");

        // Left panel: main categories placeholder
        Div leftPanel = new Div();
        leftPanel.setWidth("250px");
        Button noCategory = new Button("No category", (event) -> {
            canvasCategoryOnClickLogic(null, middlePanel, canvasComponent);
        });
        noCategory.addClassName("canvas-notebook-button");
        leftPanel.add(noCategory);
        leftPanel.addClassName("canvas-left-panel");

        Set<String> allCategories = service.findAllCategories();
        for (String category : allCategories) {
            if (category == null || category.isBlank()) {
                continue;
            }
            Button categoryButton = new Button(category, (event) -> {
                canvasCategoryOnClickLogic(category, middlePanel, canvasComponent);
            });
            categoryButton.addClassName("canvas-notebook-button");
            leftPanel.add(categoryButton);
        }

        // Add all panels to the main layout
        mainLayout.add(leftPanel, middlePanel, canvasComponent);
        mainLayout.setFlexGrow(1, canvasComponent); // allow canvasView to expand

        // Add main layout to the view
        add(mainLayout);
    }

    private void canvasCategoryOnClickLogic(String category, VerticalLayout middlePanel, CanvasComponent canvasComponent) {
        middlePanel.removeAll();
        canvasComponent.removeAll();
        // Load all notebooks with a SearchRequest and Pageable based on category
        Set<Canvas> notebooks;
        if (category == null) {
            notebooks = noCategorySearch();
        } else {
            notebooks = categorySearch(category);
        }
        // Create a list of buttons for each notebook
        addButton.setWidthFull();
        middlePanel.add(addButton);
        for (Canvas notebook : notebooks) {
            Button notebookButton = new Button(notebook.getName(), click -> {
                // On click: refresh the canvas view with selected notebook
                canvasComponent.setCanvasEntity(notebook);
                canvasComponent.refresh();
            });
            notebookButton.setWidthFull();
            notebookButton.addClassName("canvas-notebook-button");
            middlePanel.add(notebookButton);
        }
    }

    private Set<Canvas> noCategorySearch() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.addCriterion("G1", Operator.OR_OPERATOR, Canvas.class, "category", SearchOperation.EQUALS_OPERATION, "");
        searchRequest.addCriterion("G1", Canvas.class, "category", SearchOperation.IS_NULL_OPERATION, null);
        return service.load(searchRequest, Pageable.ofSize(100));
    }

    private Set<Canvas> categorySearch(String category) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.addCriterion("G1", Canvas.class, "category", SearchOperation.EQUALS_OPERATION, category);
        return service.load(searchRequest, Pageable.ofSize(100));
    }
}