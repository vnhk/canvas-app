package com.bervan.canvasapp.view;

import com.bervan.canvasapp.Canvas;
import com.bervan.canvasapp.CanvasService;
import com.bervan.common.AbstractPageView;
import com.bervan.core.model.BervanLogger;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;

import java.util.List;
import java.util.UUID;

public abstract class AbstractCanvasView extends AbstractPageView implements HasUrlParameter<String> {
    public static final String ROUTE_NAME = AbstractCanvasPagesView.ROUTE_NAME + "/";
    private final CanvasService service;
    private final BervanLogger logger;
    private Canvas canvasEntity;

    public AbstractCanvasView(CanvasService service, BervanLogger logger) {
        super();
        this.service = service;
        this.logger = logger;
    }

    @Override
    public void setParameter(BeforeEvent event, String s) {
        String canvasName = event.getRouteParameters().get("___url_parameter").orElse(UUID.randomUUID().toString());
        init(canvasName);
    }

    private void init(String name) {
        List<Canvas> optionalEntity = service.loadByName(name);

        if (!optionalEntity.isEmpty()) {
            canvasEntity = optionalEntity.get(0);
            add(new CanvasAppPageLayout(true, canvasEntity.getName()));
        } else {
            showErrorNotification("Canvas does not exist!");
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        Div container = new Div();
        container.addClassName("container");
        container.setWidth("98%");
        add(container);

        String body = (canvasEntity != null && canvasEntity.getContent() != null) ? canvasEntity.getContent() : null;

        // Execute the JS in the browser, assembling all parts
        String jsCode = getToolbarJs()
                + getCanvasContainerJs()
                + getStylesJs()
                + getMakeDraggableJs()
                + getCreateDraggableElementJs()
                + getCreateImageElementJs()
                + getCreateTableElementJs()
                + getCreateTextElementJs()
                + getLoadLayoutJs()
                + getGetCurrentLayoutJs()
                + getButtonsEventListenersJs()
                + getUpdateEditModeUiJs() // new function to update UI when toggling modes
                + getInitLayoutJs();

        getElement().executeJs(jsCode, getElement(), body);
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

    // ********* Below are the private methods returning JS code as strings *********

    private String getToolbarJs() {
        // Creates the toolbar and buttons (including the toggle edit mode button)
        return """
               let editMode = true; // Global flag indicating if we are in edit mode

               const toolbar = document.createElement('div');
               toolbar.id = 'toolbar';

               const addImageBtn = document.createElement('button');
               addImageBtn.id = 'addImage';
               addImageBtn.textContent = 'Add Image';

               const addTableBtn = document.createElement('button');
               addTableBtn.id = 'addTable';
               addTableBtn.textContent = 'Add Table';

               const addTextBtn = document.createElement('button');
               addTextBtn.id = 'addText';
               addTextBtn.textContent = 'Add Text';

               const saveLayoutBtn = document.createElement('button');
               saveLayoutBtn.id = 'saveLayout';
               saveLayoutBtn.textContent = 'Save';

               // New toggle button for edit mode
               const toggleEditBtn = document.createElement('button');
               toggleEditBtn.id = 'toggleEditMode';
               toggleEditBtn.textContent = 'Edit Mode: ON';

               toolbar.appendChild(addImageBtn);
               toolbar.appendChild(addTableBtn);
               toolbar.appendChild(addTextBtn);
               toolbar.appendChild(saveLayoutBtn);
               toolbar.appendChild(toggleEditBtn);
               """;
    }

    private String getCanvasContainerJs() {
        // Creates the canvas container
        return """
               const canvas = document.createElement('div');
               canvas.id = 'canvas';

               document.querySelector('.container').appendChild(toolbar);
               document.querySelector('.container').appendChild(canvas);
               """;
    }

    private String getStylesJs() {
        // Adds styles to the document head
        return """
               const style = document.createElement('style');
               style.textContent = `
                 .container {
                   padding: 10px;
                 }
                 #toolbar {
                   background: #eee;
                   padding: 10px;
                   display: flex;
                   gap: 10px;
                   margin-bottom: 10px;
                 }
                 button {
                   cursor: pointer;
                 }
                 #canvas {
                   position: relative;
                   width: 100%;
                   height: 80vh;
                   overflow: hidden;
                   border: 1px solid #ccc;
                 }
                 .draggable {
                   position: absolute;
                   border: 1px solid #aaa;
                   background: #fff;
                   padding: 5px;
                   cursor: move;
                 }
                 .draggable img {
                   max-width: 200px;
                   height: auto;
                   display: block;
                 }
                 .draggable table {
                   border-collapse: collapse;
                 }
                 .draggable table, .draggable td, .draggable th {
                   border: 1px solid #555;
                   padding: 5px;
                 }
                 .draggable textarea {
                   width: 200px;
                   height: 100px;
                 }
               `;
               document.head.appendChild(style);
               """;
    }

    private String getMakeDraggableJs() {
        // Function to make elements draggable (only when editMode is true)
        return """
               function makeDraggable(el) {
                 let offsetX = 0;
                 let offsetY = 0;
                 let isDown = false;

                 el.addEventListener('mousedown', (e) => {
                   if (!editMode) return; // If not in edit mode, do nothing
                   isDown = true;
                   offsetX = e.clientX - el.offsetLeft;
                   offsetY = e.clientY - el.offsetTop;
                   el.style.zIndex = 10;
                 });

                 document.addEventListener('mousemove', (e) => {
                   if (!editMode || !isDown) return; // If not in edit mode, no dragging
                   const x = e.clientX - offsetX;
                   const y = e.clientY - offsetY;
                   el.style.left = x + 'px';
                   el.style.top = y + 'px';
                 });

                 document.addEventListener('mouseup', () => {
                   if (!editMode) return;
                   isDown = false;
                   el.style.zIndex = 1;
                 });
               }
               """;
    }

    private String getCreateDraggableElementJs() {
        // Function to create a draggable element
        return """
               function createDraggableElement() {
                 const el = document.createElement('div');
                 el.classList.add('draggable');
                 el.style.top = '50px';
                 el.style.left = '50px';
                 makeDraggable(el);
                 return el;
               }
               """;
    }

    private String getCreateImageElementJs() {
        // Function to create an image element
        return """
               function createImageElement(src, x = 50, y = 50) {
                 if (!editMode) return; // Only create if in edit mode
                 const el = createDraggableElement();
                 el.dataset.type = "image";
                 const img = document.createElement('img');
                 img.src = src;
                 el.appendChild(img);
                 el.style.left = x + 'px';
                 el.style.top = y + 'px';
                 canvas.appendChild(el);
               }
               """;
    }

    private String getCreateTableElementJs() {
        // Function to create a table element
        // By default, cells will be contentEditable in edit mode only (handled by updateEditModeUI)
        return """
               function createTableElement(tableData, x = 50, y = 50) {
                 if (!editMode) return; // Only create if in edit mode
                 const el = createDraggableElement();
                 el.dataset.type = "table";
                 const table = document.createElement('table');
                 const tbody = document.createElement('tbody');
                 tableData.forEach(rowData => {
                   const tr = document.createElement('tr');
                   rowData.forEach(cellData => {
                     const td = document.createElement('td');
                     td.textContent = cellData;
                     tr.appendChild(td);
                   });
                   tbody.appendChild(tr);
                 });
                 table.appendChild(tbody);
                 el.appendChild(table);
                 el.style.left = x + 'px';
                 el.style.top = y + 'px';
                 canvas.appendChild(el);
               }
               """;
    }

    private String getCreateTextElementJs() {
        // Function to create a text (textarea) element
        // Text area editability is handled by updateEditModeUI
        return """
               function createTextElement(textValue, x = 50, y = 50) {
                 if (!editMode) return; // Only create if in edit mode
                 const el = createDraggableElement();
                 el.dataset.type = "text";
                 const textarea = document.createElement('textarea');
                 textarea.value = textValue;
                 el.appendChild(textarea);
                 el.style.left = x + 'px';
                 el.style.top = y + 'px';
                 canvas.appendChild(el);
               }
               """;
    }

    private String getLoadLayoutJs() {
        // Function to load the layout from JSON data
        return """
               function loadLayout(layoutData) {
                 canvas.innerHTML = '';
                 layoutData.forEach(item => {
                   const { type, x, y, content } = item;
                   if (type === 'image') {
                     createImageElement(content, x, y);
                   } else if (type === 'table') {
                     createTableElement(content, x, y);
                   } else if (type === 'text') {
                     createTextElement(content, x, y);
                   }
                 });
                 updateEditModeUI(); // Update UI based on current edit mode after loading
               }
               """;
    }

    private String getGetCurrentLayoutJs() {
        // Function to get current layout as JSON data
        return """
               function getCurrentLayout() {
                 const elements = canvas.querySelectorAll('.draggable');
                 const layoutData = [];

                 elements.forEach(el => {
                   const type = el.dataset.type;
                   const x = el.style.left.replace('px', '');
                   const y = el.style.top.replace('px', '');

                   let content = null;
                   if (type === 'image') {
                     content = el.querySelector('img').src;
                   } else if (type === 'table') {
                     const rows = el.querySelectorAll('tr');
                     const tableData = [];
                     rows.forEach(r => {
                       const rowCells = [];
                       r.querySelectorAll('td').forEach(td => {
                         rowCells.push(td.textContent);
                       });
                       tableData.push(rowCells);
                     });
                     content = tableData;
                   } else if (type === 'text') {
                     content = el.querySelector('textarea').value;
                   }

                   layoutData.push({ type, x: Number(x), y: Number(y), content });
                 });

                 return layoutData;
               }
               """;
    }

    private String getButtonsEventListenersJs() {
        // Event listeners for the buttons
        return """
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
                   createTextElement("...");
                   updateEditModeUI();
                 }
               });

               saveLayoutBtn.addEventListener('click', () => {
                 // Saving can happen regardless of edit mode
                 const layoutData = getCurrentLayout();
                 const jsonString = JSON.stringify(layoutData);
                 $0.$server.saveLayout(jsonString);
               });

               // Toggle edit mode event listener
               toggleEditBtn.addEventListener('click', () => {
                 editMode = !editMode;
                 toggleEditBtn.textContent = 'Edit Mode: ' + (editMode ? 'ON' : 'OFF');
                 updateEditModeUI();
               });
               """;
    }

    private String getUpdateEditModeUiJs() {
        // This function updates the UI elements based on the current edit mode
        return """
               function updateEditModeUI() {
                 // Update text areas
                 const allTextareas = canvas.querySelectorAll('.draggable[data-type="text"] textarea');
                 allTextareas.forEach(ta => {
                   ta.readOnly = !editMode; // When view mode, text is not editable
                 });

                 // Update tables
                 const allTables = canvas.querySelectorAll('.draggable[data-type="table"] table');
                 allTables.forEach(t => {
                   const tds = t.querySelectorAll('td');
                   tds.forEach(td => {
                     td.contentEditable = editMode; // Editable only in edit mode
                   });
                 });
               }
               """;
    }

    private String getInitLayoutJs() {
        // Initialize layout if there is JSON data
        return """
               if ($1) {
                 try {
                   const layoutData = JSON.parse($1);
                   loadLayout(layoutData);
                 } catch (e) {
                   console.error("Invalid initial layout JSON", e);
                 }
               } else {
                 updateEditModeUI(); // Ensure UI is correct even if empty layout
               }
               """;
    }
}