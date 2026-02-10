window.makeDraggable = function(el) {
    let offsetX = 0;
    let offsetY = 0;
    let isDragging = false;

    el.addEventListener('mousedown', (e) => {
        if (!window.editMode) return;
        if (e.target.closest('#element-toolbar')) return;
        if (e.target.closest('.resize-handle')) return;
        if (e.target.matches('input, select, textarea')) return;

        // Don't drag if user clicks on contenteditable content (text or table cells)
        const editable = e.target.closest('[contenteditable="true"]');
        if (editable) return;

        isDragging = true;
        const canvasRect = window.canvas.getBoundingClientRect();
        offsetX = e.clientX - el.offsetLeft;
        offsetY = e.clientY - el.offsetTop;
        el.style.zIndex = 10;
    });

    document.addEventListener('mousemove', (e) => {
        if (!window.editMode || !isDragging) return;
        const x = e.clientX - offsetX;
        const y = e.clientY - offsetY;
        el.style.left = Math.max(0, x) + 'px';
        el.style.top = Math.max(0, y) + 'px';
    });

    document.addEventListener('mouseup', () => {
        if (!window.editMode) return;
        if (isDragging) {
            isDragging = false;
            el.style.zIndex = 1;
            if (window.scheduleAutoSave) window.scheduleAutoSave();
        }
    });
};

window.makeResizable = function(el) {
    const handle = document.createElement('div');
    handle.className = 'resize-handle';
    el.appendChild(handle);

    let isResizing = false;
    let startX, startY, startWidth, startHeight;

    handle.addEventListener('mousedown', (e) => {
        if (!window.editMode) return;
        e.stopPropagation();
        e.preventDefault();
        isResizing = true;
        startX = e.clientX;
        startY = e.clientY;
        startWidth = el.offsetWidth;
        startHeight = el.offsetHeight;
        el.style.zIndex = 10;
    });

    document.addEventListener('mousemove', (e) => {
        if (!isResizing) return;
        const newWidth = Math.max(60, startWidth + (e.clientX - startX));
        const newHeight = Math.max(30, startHeight + (e.clientY - startY));
        el.style.width = newWidth + 'px';
        el.style.height = newHeight + 'px';
    });

    document.addEventListener('mouseup', () => {
        if (isResizing) {
            isResizing = false;
            el.style.zIndex = 1;
            if (window.scheduleAutoSave) window.scheduleAutoSave();
        }
    });
};

window.createDraggableElement = function() {
    const el = document.createElement('div');
    el.classList.add('draggable');
    el.style.top = '50px';
    el.style.left = '50px';
    window.makeDraggable(el);
    window.makeResizable(el);
    return el;
};
