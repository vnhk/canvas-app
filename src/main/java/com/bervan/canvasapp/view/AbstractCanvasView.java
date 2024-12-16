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
        String jsCode =
                getToolbarJs()
                        + getCanvasContainerJs()
                        + getStylesJs()
                        + getElementToolbarJs()           // New toolbar for selected element
                        + getMakeDraggableJs()
                        + getCreateDraggableElementJs()
                        + getCreateImageElementJs()
                        + getCreateTableElementJs()
                        + getCreateTextElementJs()
                        + getLoadLayoutJs()
                        + getGetCurrentLayoutJs()
                        + getElementActionsJs()           // JS for element-specific actions
                        + getButtonsEventListenersJs()
                        + getUpdateEditModeUiJs()
                        + getElementSelectionJs()         // JS for selecting elements and showing toolbar
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

    private String getToolbarJs() {
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

               // Toggle Edit Mode Button
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
        return """
               const canvas = document.createElement('div');
               canvas.id = 'canvas';

               document.querySelector('.container').appendChild(toolbar);
               document.querySelector('.container').appendChild(canvas);
               """;
    }

    private String getStylesJs() {
        return """
               const style = document.createElement('style');
               style.textContent = `
                 .container {
                   padding: 10px;
                   background-color: black;
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
                   background: black;
                   padding: 5px;
                   cursor: move;
                 }
                 
                 .draggable[data-type="image"] img {
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
                 
                 .draggable[data-type="text"] div[contenteditable="true"] {
                   width: 200px;
                   height: 100px;
                   border: none;
                   outline: none;
                 }

                 #element-toolbar {
                   position: absolute;
                   background: #333;
                   color: #fff;
                   padding: 5px;
                   display: none;
                   z-index: 999;
                 }
                 
                 #element-toolbar button {
                   background: #555;
                   color: #fff;
                   margin: 0 2px;
                   border: none;
                   padding: 3px 5px;
                   font-size: 12px;
                   cursor: pointer;
                 }
                 #element-toolbar button:hover {
                   background: #777;
                 }
               `;
               document.head.appendChild(style);
               """;
    }

    private String getElementToolbarJs() {
        // A toolbar that appears near the selected element
        // We'll dynamically fill it with options depending on the element type
        return """
               const elementToolbar = document.createElement('div');
               elementToolbar.id = 'element-toolbar';
               document.querySelector('.container').appendChild(elementToolbar);
               """;
    }

    private String getMakeDraggableJs() {
        return """
               function makeDraggable(el) {
                 let offsetX = 0;
                 let offsetY = 0;
                 let isDown = false;

                 el.addEventListener('mousedown', (e) => {
                   if (!editMode) return;
                   // If clicked inside toolbar, don't drag
                   if (e.target.closest('#element-toolbar')) return;
                   isDown = true;
                   offsetX = e.clientX - el.offsetLeft;
                   offsetY = e.clientY - el.offsetTop;
                   el.style.zIndex = 10;
                 });

                 document.addEventListener('mousemove', (e) => {
                   if (!editMode || !isDown) return;
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
        return """
               function createImageElement(src, x = 50, y = 50) {
                 if (!editMode) return;
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
        return """
               function createTableElement(tableData, x = 50, y = 50) {
                 if (!editMode) return;
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
        // Use a contenteditable div for text to allow formatting
        return """
               function createTextElement(textValue, x = 50, y = 50) {
                 if (!editMode) return;
                 const el = createDraggableElement();
                 el.dataset.type = "text";
                 const textDiv = document.createElement('div');
                 textDiv.contentEditable = "true";
                 textDiv.innerHTML = textValue;
                 el.appendChild(textDiv);
                 el.style.left = x + 'px';
                 el.style.top = y + 'px';
                 canvas.appendChild(el);
               }
               """;
    }

    private String getLoadLayoutJs() {
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
                 updateEditModeUI();
               }
               """;
    }

    private String getGetCurrentLayoutJs() {
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
                     content = el.querySelector('div').innerHTML;
                   }

                   layoutData.push({ type, x: Number(x), y: Number(y), content });
                 });

                 return layoutData;
               }
               """;
    }

    private String getElementActionsJs() {
        // Actions for image, table, and text
        return """
               function changeImageSource(el) {
                 const newSrc = prompt("Enter new image URL:", el.querySelector('img').src);
                 if (newSrc) {
                   el.querySelector('img').src = newSrc;
                 }
               }

               function removeElement(el) {
                 if (confirm("Remove this element?")) {
                   el.remove();
                   hideElementToolbar();
                 }
               }

               // Table actions
               function addTableRow(el) {
                 const table = el.querySelector('table');
                 const rowCount = table.rows.length;
                 const colCount = table.rows[0].cells.length;
                 const tr = document.createElement('tr');
                 for (let i = 0; i < colCount; i++) {
                   const td = document.createElement('td');
                   td.textContent = "New Cell";
                   tr.appendChild(td);
                 }
                 table.appendChild(tr);
               }

               function addTableColumn(el) {
                 const table = el.querySelector('table');
                 for (let i = 0; i < table.rows.length; i++) {
                   const td = document.createElement('td');
                   td.textContent = "New Cell";
                   table.rows[i].appendChild(td);
                 }
               }

               function removeTableRow(el) {
                 const table = el.querySelector('table');
                 const rowIndex = prompt("Enter row index to remove (0-based):");
                 if (rowIndex !== null) {
                   const i = parseInt(rowIndex);
                   if (!isNaN(i) && i >= 0 && i < table.rows.length) {
                     table.deleteRow(i);
                   }
                 }
               }

               function removeTableColumn(el) {
                 const table = el.querySelector('table');
                 const colIndex = prompt("Enter column index to remove (0-based):");
                 if (colIndex !== null) {
                   const i = parseInt(colIndex);
                   if (!isNaN(i)) {
                     for (let r = 0; r < table.rows.length; r++) {
                       if (i >= 0 && i < table.rows[r].cells.length) {
                         table.rows[r].deleteCell(i);
                       }
                     }
                   }
                 }
               }

               // Text formatting actions
               function formatText(el, cmd, value = null) {
                 // Temporarily focus the element
                 const textDiv = el.querySelector('div[contenteditable="true"]');
                 textDiv.focus();
                 document.execCommand(cmd, false, value);
               }

               """;
    }

    private String getButtonsEventListenersJs() {
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
                   createTextElement("Edit me");
                   updateEditModeUI();
                 }
               });

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
               """;
    }

    private String getUpdateEditModeUiJs() {
        return """
               function updateEditModeUI() {
                 // Update text elements (readOnly in view mode is not needed now since we use contentEditable)
                 // We'll just remove contentEditable in view mode:
                 const textElements = canvas.querySelectorAll('.draggable[data-type="text"] div');
                 textElements.forEach(div => {
                   div.contentEditable = editMode;
                 });

                 // Update tables (contentEditable cells in edit mode)
                 const tableElements = canvas.querySelectorAll('.draggable[data-type="table"] table');
                 tableElements.forEach(table => {
                   const tds = table.querySelectorAll('td');
                   tds.forEach(td => {
                     td.contentEditable = editMode;
                   });
                 });
               }
               """;
    }

    private String getElementSelectionJs() {
        return """
               let selectedElement = null;

               canvas.addEventListener('click', (e) => {
                 if (!editMode) return; // Only select in edit mode
                 const el = e.target.closest('.draggable');
                 if (el && canvas.contains(el)) {
                   // Select this element
                   selectedElement = el;
                   showElementToolbar(el);
                 } else {
                   // Clicked on canvas but not on element
                   hideElementToolbar();
                   selectedElement = null;
                 }
               });

               function showElementToolbar(el) {
                 elementToolbar.innerHTML = '';
                 elementToolbar.style.display = 'block';

                 const type = el.dataset.type;

                 // Common remove button
                 const removeBtn = document.createElement('button');
                 removeBtn.textContent = 'Remove';
                 removeBtn.addEventListener('click', () => removeElement(el));
                 elementToolbar.appendChild(removeBtn);

                 if (type === 'image') {
                   const changeSrcBtn = document.createElement('button');
                   changeSrcBtn.textContent = 'Change Source';
                   changeSrcBtn.addEventListener('click', () => changeImageSource(el));
                   elementToolbar.appendChild(changeSrcBtn);
                 }

                 if (type === 'table') {
                   const addRowBtn = document.createElement('button');
                   addRowBtn.textContent = 'Add Row';
                   addRowBtn.addEventListener('click', () => addTableRow(el));
                   elementToolbar.appendChild(addRowBtn);

                   const addColBtn = document.createElement('button');
                   addColBtn.textContent = 'Add Col';
                   addColBtn.addEventListener('click', () => addTableColumn(el));
                   elementToolbar.appendChild(addColBtn);

                   const remRowBtn = document.createElement('button');
                   remRowBtn.textContent = 'Remove Row';
                   remRowBtn.addEventListener('click', () => removeTableRow(el));
                   elementToolbar.appendChild(remRowBtn);

                   const remColBtn = document.createElement('button');
                   remColBtn.textContent = 'Remove Col';
                   remColBtn.addEventListener('click', () => removeTableColumn(el));
                   elementToolbar.appendChild(remColBtn);
                 }

                 if (type === 'text') {
                   const boldBtn = document.createElement('button');
                   boldBtn.textContent = 'B';
                   boldBtn.addEventListener('click', () => formatText(el, 'bold'));
                   elementToolbar.appendChild(boldBtn);

                   const italicBtn = document.createElement('button');
                   italicBtn.textContent = 'I';
                   italicBtn.addEventListener('click', () => formatText(el, 'italic'));
                   elementToolbar.appendChild(italicBtn);

                   const underlineBtn = document.createElement('button');
                   underlineBtn.textContent = 'U';
                   underlineBtn.addEventListener('click', () => formatText(el, 'underline'));
                   elementToolbar.appendChild(underlineBtn);

                   const fontSizeBtn = document.createElement('button');
                   fontSizeBtn.textContent = 'Font Size';
                   fontSizeBtn.addEventListener('click', () => {
                     const size = prompt("Enter font size (1-7):", "3");
                     if (size) formatText(el, 'fontSize', size);
                   });
                   elementToolbar.appendChild(fontSizeBtn);
                 }

                 // Position toolbar
                 const rect = el.getBoundingClientRect();
                 const containerRect = document.querySelector('.container').getBoundingClientRect();
                 elementToolbar.style.left = (rect.left - containerRect.left) + 'px';
                 elementToolbar.style.top = (rect.top - containerRect.top - elementToolbar.offsetHeight - 5) + 'px';
               }

               function hideElementToolbar() {
                 elementToolbar.style.display = 'none';
                 elementToolbar.innerHTML = '';
               }

               window.addEventListener('scroll', () => {
                 if (selectedElement && elementToolbar.style.display === 'block') {
                   showElementToolbar(selectedElement);
                 }
               });
               """;
    }

    private String getInitLayoutJs() {
        return """
               if ($1) {
                 try {
                   const layoutData = JSON.parse($1);
                   loadLayout(layoutData);
                 } catch (e) {
                   console.error("Invalid initial layout JSON", e);
                 }
               } else {
                 updateEditModeUI();
               }
               """;
    }
}