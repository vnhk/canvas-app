package com.bervan.streamingapp.view;

import com.bervan.common.AbstractPageView;
import com.bervan.common.search.SearchRequest;
import com.bervan.streamingapp.Canvas;
import com.bervan.streamingapp.CanvasService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public abstract class AbstractCanvasPagesView extends AbstractPageView {
    public static final String ROUTE_NAME = "/canvas-app/all-canvas-pages";

    public AbstractCanvasPagesView(@Autowired CanvasService service) {
        // Create main horizontal layout filling the whole view
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.addClassName("canvas-main-layout");

        // Left panel: main categories placeholder
        Div leftPanel = new Div();
        leftPanel.setWidth("250px");
        Button mainCategory = new Button("Main Category");
        mainCategory.addClassName("canvas-notebook-button");
        leftPanel.add(mainCategory);
        leftPanel.addClassName("canvas-left-panel");

        // Load all notebooks with a SearchRequest and Pageable later based on category
        Set<Canvas> notebooks = service.load(new SearchRequest(), Pageable.ofSize(1000));

        // Middle panel: list of notebooks
        VerticalLayout middlePanel = new VerticalLayout();
        middlePanel.setWidth("250px");
        middlePanel.addClassName("canvas-middle-panel");

        // Main canvas view area
        CanvasView canvasView = new CanvasView(service, null); // initially null
        canvasView.setSizeFull();
        canvasView.addClassName("canvas-main-canvas");

        // Create a list of buttons for each notebook
        for (Canvas notebook : notebooks) {
            Button notebookButton = new Button(notebook.getName(), click -> {
                // On click: refresh the canvas view with selected notebook
                canvasView.setCanvasEntity(notebook);
                canvasView.refresh();
            });
            notebookButton.setWidthFull();
            notebookButton.addClassName("canvas-notebook-button");
            middlePanel.add(notebookButton);
        }

        // Add all panels to the main layout
        mainLayout.add(leftPanel, middlePanel, canvasView);
        mainLayout.setFlexGrow(1, canvasView); // allow canvasView to expand

        // Add main layout to the view
        add(mainLayout);
    }
}