window.loadLayout = function(layoutData) {
    window._ignoreObserver = true;
    window.canvas.innerHTML = '';

    layoutData.forEach(item => {
        const { type, x, y, width, height, content } = item;
        const w = width || null;
        const h = height || null;
        if (type === 'image') {
            window.createImageElement(content, x, y, w, h);
        } else if (type === 'table') {
            window.createTableElement(content, x, y, w, h);
        } else if (type === 'text') {
            window.createTextElement(content, x, y, w, h);
        } else if (type === 'divider') {
            window.createDividerElement(x, y, w || 300);
        }
    });
    window.updateEditModeUI();
    // Allow observer after layout is fully loaded
    setTimeout(() => { window._ignoreObserver = false; }, 500);
};

window.getCurrentLayout = function() {
    const elements = window.canvas.querySelectorAll('.draggable');
    const layoutData = [];
    elements.forEach(el => {
        const type = el.dataset.type;
        if (!type) return;
        const x = parseInt(el.style.left, 10) || 0;
        const y = parseInt(el.style.top, 10) || 0;
        const width = el.style.width ? parseInt(el.style.width, 10) : null;
        const height = el.style.height ? parseInt(el.style.height, 10) : null;
        let content = null;
        if (type === 'image') {
            const img = el.querySelector('img');
            content = img ? img.src : '';
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
            const div = el.querySelector('div[contenteditable]');
            content = div ? div.innerHTML : '';
        } else if (type === 'divider') {
            content = null;
        }
        const item = { type, x, y, content };
        if (width) item.width = width;
        if (height) item.height = height;
        layoutData.push(item);
    });
    return layoutData;
};

window.updateEditModeUI = function() {
    if (!window.canvas) return;
    var editable = window.editMode ? "true" : "false";

    // Only target the actual contenteditable text divs, NOT resize handles
    window.canvas.querySelectorAll('.draggable[data-type="text"] > div:not(.resize-handle)').forEach(div => {
        div.contentEditable = editable;
    });

    window.canvas.querySelectorAll('.draggable[data-type="table"] td').forEach(td => {
        td.contentEditable = editable;
    });

    // Show/hide resize handles
    window.canvas.querySelectorAll('.resize-handle').forEach(h => {
        h.style.display = window.editMode ? '' : 'none';
    });
};
