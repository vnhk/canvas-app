// Create an image element
window.createImageElement = function (src, x = 50, y = 50, w, h) {
    const el = window.createDraggableElement();
    el.dataset.type = "image";
    const img = document.createElement('img');
    img.src = src;
    el.appendChild(img);
    el.style.left = x + 'px';
    el.style.top = y + 'px';
    if (w) el.style.width = w + 'px';
    if (h) el.style.height = h + 'px';
    window.canvas.appendChild(el);
    return el;
};

// Create a table element
window.createTableElement = function (tableData, x = 50, y = 50, w, h) {
    const el = window.createDraggableElement();
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
    if (w) el.style.width = w + 'px';
    if (h) el.style.height = h + 'px';
    window.canvas.appendChild(el);
    return el;
};

// Create a text element
window.createTextElement = function (textValue, x = 50, y = 50, w, h) {
    const el = window.createDraggableElement();
    el.dataset.type = "text";
    const textDiv = document.createElement('div');
    textDiv.contentEditable = "true";
    textDiv.innerHTML = textValue;
    el.appendChild(textDiv);
    el.style.left = x + 'px';
    el.style.top = y + 'px';
    if (w) el.style.width = w + 'px';
    if (h) el.style.height = h + 'px';
    window.canvas.appendChild(el);
    return el;
};

// Create a divider element
window.createDividerElement = function (x = 50, y = 50, w = 300) {
    const el = document.createElement('div');
    el.classList.add('draggable');
    el.dataset.type = "divider";
    el.style.top = y + 'px';
    el.style.left = x + 'px';
    el.style.width = w + 'px';
    const hr = document.createElement('hr');
    el.appendChild(hr);
    window.makeDraggable(el);
    window.makeResizable(el);
    window.canvas.appendChild(el);
    return el;
};

// Change image source
window.changeImageSource = function (el) {
    const newSrc = prompt("Enter new image URL:", el.querySelector('img').src);
    if (newSrc) {
        el.querySelector('img').src = newSrc;
    }
};

// Remove an element
window.removeElement = function (el) {
    if (confirm("Remove this element?")) {
        el.remove();
        window.hideElementToolbar();
        window.selectedElement = null;
    }
};

// Table actions
window.addTableRow = function (el) {
    const table = el.querySelector('table');
    const colCount = table.rows[0] ? table.rows[0].cells.length : 3;
    const tr = document.createElement('tr');
    for (let i = 0; i < colCount; i++) {
        const td = document.createElement('td');
        td.textContent = "";
        tr.appendChild(td);
    }
    table.querySelector('tbody').appendChild(tr);
};

window.addTableColumn = function (el) {
    const table = el.querySelector('table');
    for (let i = 0; i < table.rows.length; i++) {
        const td = document.createElement('td');
        td.textContent = "";
        table.rows[i].appendChild(td);
    }
};

window.removeTableRow = function (el) {
    const table = el.querySelector('table');
    const rowIndex = prompt("Enter row index to remove (0-based):");
    if (rowIndex !== null) {
        const i = parseInt(rowIndex);
        if (!isNaN(i) && i >= 0 && i < table.rows.length) {
            table.deleteRow(i);
        }
    }
};

window.removeTableColumn = function (el) {
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
};

// Text formatting actions
window.formatText = function (el, cmd, value = null) {
    const textDiv = el.querySelector('div[contenteditable]');
    if (!textDiv) return;
    textDiv.focus();
    document.execCommand(cmd, false, value);
};

// Apply heading format
window.applyHeading = function (el, level) {
    const textDiv = el.querySelector('div[contenteditable]');
    if (!textDiv) return;
    textDiv.focus();
    document.execCommand('formatBlock', false, '<h' + level + '>');
};
