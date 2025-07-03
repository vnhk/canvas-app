package com.bervan.streamingapp.view;

import com.bervan.common.AbstractPageView;
import com.bervan.streamingapp.Canvas;
import com.bervan.streamingapp.CanvasService;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;
import lombok.Setter;


@JsModule("./styles.js")
@JsModule("./toolbar.js")
@JsModule("./canvasContainer.js")
@JsModule("./draggable.js")
@JsModule("./elements.js")
@JsModule("./layout.js")
@JsModule("./selection.js")
public class CanvasView extends AbstractPageView {
    public static final String ROUTE_NAME = AbstractCanvasPagesView.ROUTE_NAME + "/";
    private final CanvasService service;
    @Setter
    @Getter
    private Canvas canvasEntity;

    public CanvasView(CanvasService service, Canvas canvasEntity) {
        super();
        this.service = service;
        this.canvasEntity = canvasEntity;
        refresh();
    }

    public void refresh() {
        if (canvasEntity == null) {
            canvasEntity = new Canvas();
        }
        removeAll();
        Div container = new Div();
        container.addClassName("container");
        container.setWidth("98%");
        add(container);

        String body = (canvasEntity != null && canvasEntity.getContent() != null) ? canvasEntity.getContent() : null;

        // Execute initialization functions defined in JS files
        getElement().executeJs("injectStyles();");
        getElement().executeJs("initToolbar($0);", container);
        getElement().executeJs("initCanvasContainer($0);", container);
        getElement().executeJs("initSelection($0);", container);

        // After all init, load the layout if available
        if (body != null) {
            getElement().executeJs("const layoutData = JSON.parse($0); loadLayout(layoutData);", body);
        } else {
            getElement().executeJs("updateEditModeUI();");
        }

        // Add event listeners for buttons from toolbar after loading layout
        // Connect server calls
        getElement().executeJs(
                """
                        saveLayoutBtn.addEventListener('click', () => {
                          const layoutData = getCurrentLayout();
                          const jsonString = JSON.stringify(layoutData);
                          $0.$server.saveLayout(jsonString);
                        });
                        
                        toggleEditBtn.addEventListener('click', () => {
                          editMode = !editMode;
                          toggleEditBtn.textContent = 'Edit Mode: ' + (editMode ? 'ON' : 'OFF');
                          if (!editMode) {
                            hideElementToolbar();
                            selectedElement = null;
                          }
                          updateEditModeUI();
                        });
                        
                        addImageBtn.addEventListener('click', () => {
                          if (editMode) {
                            createImageElement("https://via.placeholder.com/150");
                            updateEditModeUI();
                          }
                        });
                        
                        addTableBtn.addEventListener('click', () => {
                          if (editMode) {
                            const defaultTable = [
                              ["R1C1", "R1C2", "R1C3"],
                              ["R2C1", "R2C2", "R2C3"],
                              ["R3C1", "R3C2", "R3C3"]
                            ];
                            createTableElement(defaultTable);
                            updateEditModeUI();
                          }
                        });
                        
                        addTextBtn.addEventListener('click', () => {
                          if (editMode) {
                            createTextElement("Edit me");
                            updateEditModeUI();
                          }
                        });
                        """,
                getElement()
        );
    }

    @ClientCallable
    public void saveLayout(String json) {
        if (canvasEntity != null) {
            canvasEntity.setContent(json);
            service.save(canvasEntity);
            showSuccessNotification("Layout saved successfully!");
        } else {
            showErrorNotification("No canvas to save!");
        }
    }
}