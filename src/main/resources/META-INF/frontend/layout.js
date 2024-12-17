window.loadLayout = function(layoutData) {
    window.canvas.innerHTML = '';
    layoutData.forEach(item => {
        const { type, x, y, content } = item;
        if (type === 'image') {
            window.createImageElement(content, x, y);
        } else if (type === 'table') {
            window.createTableElement(content, x, y);
        } else if (type === 'text') {
            window.createTextElement(content, x, y);
        }
    });
    window.updateEditModeUI();
};

window.getCurrentLayout = function() {
    const elements = window.canvas.querySelectorAll('.draggable');
    const layoutData = [];
    elements.forEach(el => {
        const type = el.dataset.type;
        const x = parseInt(el.style.left, 10);
        const y = parseInt(el.style.top, 10);
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
        layoutData.push({ type, x, y, content });
    });
    return layoutData;
};

window.updateEditModeUI = function() {
    const textElements = window.canvas.querySelectorAll('.draggable[data-type="text"] div');
    textElements.forEach(div => {
        div.contentEditable = window.editMode;
    });

    const tableElements = window.canvas.querySelectorAll('.draggable[data-type="table"] table');
    tableElements.forEach(table => {
        const tds = table.querySelectorAll('td');
        tds.forEach(td => {
            td.contentEditable = window.editMode;
        });
    });
};