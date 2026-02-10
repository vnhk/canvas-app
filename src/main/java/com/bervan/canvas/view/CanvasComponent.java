package com.bervan.canvas.view;

import com.bervan.canvas.Canvas;
import com.bervan.canvas.CanvasService;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.view.AbstractBervanEntityView;
import com.bervan.common.view.EmptyLayout;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;

import java.util.UUID;


@JsModule("./styles.js")
@JsModule("./toolbar.js")
@JsModule("./canvasContainer.js")
@JsModule("./draggable.js")
@JsModule("./elements.js")
@JsModule("./layout.js")
@JsModule("./selection.js")
public class CanvasComponent extends AbstractBervanEntityView<UUID, Canvas> {
    private final CanvasService service;
    private Canvas canvasEntity;

    public CanvasComponent(CanvasService service, Canvas canvasEntity, BervanViewConfig bervanViewConfig) {
        super(new EmptyLayout(), service, bervanViewConfig, Canvas.class);
        this.service = service;
        setCanvasEntity(canvasEntity);
        refresh();
    }

    public void setCanvasEntity(Canvas canvasEntity) {
        this.canvasEntity = canvasEntity;
        this.item = canvasEntity;
    }

    public void refresh() {
        if (canvasEntity == null) {
            canvasEntity = new Canvas();
        }
        removeAll();
        Div container = new Div();
        container.addClassName("container");
        container.setWidth("100%");
        add(container);

        String name = canvasEntity.getName() != null ? canvasEntity.getName() : "";
        String category = canvasEntity.getCategory() != null ? canvasEntity.getCategory() : "";
        String body = (canvasEntity.getContent() != null) ? canvasEntity.getContent() : null;

        // Initialize JS modules
        getElement().executeJs("injectStyles();");
        getElement().executeJs("initToolbar($0);", container);
        getElement().executeJs("initCanvasContainer($0);", container);
        getElement().executeJs("initSelection($0);", container);

        // Set auto-save element reference and entity flag
        boolean hasEntity = canvasEntity.getId() != null;
        getElement().executeJs("window._canvasAutoSaveElement = $0; window._canvasHasEntity = $1;",
                getElement(), hasEntity);

        // Build editable header
        getElement().executeJs(
                """
                        const headerRow = document.createElement('div');
                        headerRow.className = 'canvas-header-row';

                        const nameField = document.createElement('input');
                        nameField.type = 'text';
                        nameField.className = 'canvas-header-field';
                        nameField.value = $1;
                        nameField.placeholder = 'Canvas name...';
                        nameField.addEventListener('change', () => {
                            $0.$server.updateName(nameField.value);
                        });

                        const catField = document.createElement('input');
                        catField.type = 'text';
                        catField.className = 'canvas-header-field category-field';
                        catField.value = $2;
                        catField.placeholder = 'Category...';
                        catField.addEventListener('change', () => {
                            $0.$server.updateCategory(catField.value);
                        });

                        headerRow.appendChild(nameField);
                        headerRow.appendChild(catField);

                        const container = $3;
                        container.insertBefore(headerRow, container.firstChild);
                        """,
                getElement(), name, category, container
        );

        // Load layout if available
        if (body != null) {
            getElement().executeJs("const layoutData = JSON.parse($0); loadLayout(layoutData);", body);
        } else {
            getElement().executeJs("updateEditModeUI();");
        }

        // Wire up all toolbar buttons
        getElement().executeJs(
                """
                        // Save
                        saveLayoutBtn.addEventListener('click', () => {
                          const layoutData = getCurrentLayout();
                          const jsonString = JSON.stringify(layoutData);
                          $0.$server.saveLayout(jsonString);
                        });

                        // Toggle edit mode
                        toggleEditBtn.addEventListener('click', () => {
                          editMode = !editMode;
                          toggleEditBtn.textContent = 'Edit: ' + (editMode ? 'ON' : 'OFF');
                          if (!editMode) {
                            deselectAll();
                          }
                          updateEditModeUI();
                        });

                        // Add elements
                        addImageBtn.addEventListener('click', () => {
                          if (editMode) {
                            createImageElement("https://via.placeholder.com/150");
                            updateEditModeUI();
                          }
                        });

                        addTableBtn.addEventListener('click', () => {
                          if (editMode) {
                            createTableElement([
                              ["", "", ""],
                              ["", "", ""],
                              ["", "", ""]
                            ]);
                            updateEditModeUI();
                          }
                        });

                        addTextBtn.addEventListener('click', () => {
                          if (editMode) {
                            createTextElement("Edit me...");
                            updateEditModeUI();
                          }
                        });

                        addDividerBtn.addEventListener('click', () => {
                          if (editMode) {
                            createDividerElement();
                          }
                        });

                        // Delete selected
                        deleteSelectedBtn.addEventListener('click', () => {
                          if (editMode && selectedElement) {
                            removeElement(selectedElement);
                          }
                        });

                        // Text formatting from toolbar (applies to selected text element)
                        function applyToSelected(cmd, val) {
                          if (!editMode || !selectedElement) return;
                          if (selectedElement.dataset.type === 'text') {
                            formatText(selectedElement, cmd, val);
                          }
                        }

                        boldBtn.addEventListener('click', () => applyToSelected('bold'));
                        italicBtn.addEventListener('click', () => applyToSelected('italic'));
                        underlineBtn.addEventListener('click', () => applyToSelected('underline'));
                        strikeBtn.addEventListener('click', () => applyToSelected('strikethrough'));

                        h1Btn.addEventListener('click', () => {
                          if (editMode && selectedElement && selectedElement.dataset.type === 'text') {
                            applyHeading(selectedElement, 1);
                          }
                        });
                        h2Btn.addEventListener('click', () => {
                          if (editMode && selectedElement && selectedElement.dataset.type === 'text') {
                            applyHeading(selectedElement, 2);
                          }
                        });
                        h3Btn.addEventListener('click', () => {
                          if (editMode && selectedElement && selectedElement.dataset.type === 'text') {
                            applyHeading(selectedElement, 3);
                          }
                        });

                        fontSizeSelect.addEventListener('change', () => {
                          applyToSelected('fontSize', fontSizeSelect.value);
                        });

                        textColorInput.addEventListener('input', () => {
                          applyToSelected('foreColor', textColorInput.value);
                        });

                        bgColorInput.addEventListener('input', () => {
                          applyToSelected('hiliteColor', bgColorInput.value);
                        });

                        ulBtn.addEventListener('click', () => applyToSelected('insertUnorderedList'));
                        olBtn.addEventListener('click', () => applyToSelected('insertOrderedList'));
                        """,
                getElement()
        );
    }

    @ClientCallable
    public void saveLayout(String json) {
        if (canvasEntity != null && canvasEntity.getId() != null) {
            canvasEntity.setContent(json);
            service.save(canvasEntity);
        }
    }

    @ClientCallable
    public void updateName(String name) {
        if (canvasEntity != null && canvasEntity.getId() != null) {
            canvasEntity.setName(name);
            service.save(canvasEntity);
        }
    }

    @ClientCallable
    public void updateCategory(String category) {
        if (canvasEntity != null && canvasEntity.getId() != null) {
            canvasEntity.setCategory(category);
            service.save(canvasEntity);
        }
    }
}
