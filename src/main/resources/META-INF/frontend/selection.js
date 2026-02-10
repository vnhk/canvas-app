// selection.js

window.selectedElement = null;

window.showElementToolbar = function(el) {
    const elementToolbar = document.getElementById('element-toolbar');
    if (!elementToolbar) return;
    elementToolbar.innerHTML = '';
    elementToolbar.style.display = 'flex';

    const type = el.dataset.type;

    // Common remove button
    const removeBtn = document.createElement('button');
    removeBtn.textContent = 'Remove';
    removeBtn.addEventListener('click', () => window.removeElement(el));
    elementToolbar.appendChild(removeBtn);

    if (type === 'image') {
        const changeSrcBtn = document.createElement('button');
        changeSrcBtn.textContent = 'Change Source';
        changeSrcBtn.addEventListener('click', () => window.changeImageSource(el));
        elementToolbar.appendChild(changeSrcBtn);
    }

    if (type === 'table') {
        const addRowBtn = document.createElement('button');
        addRowBtn.textContent = '+ Row';
        addRowBtn.addEventListener('click', () => window.addTableRow(el));
        elementToolbar.appendChild(addRowBtn);

        const addColBtn = document.createElement('button');
        addColBtn.textContent = '+ Col';
        addColBtn.addEventListener('click', () => window.addTableColumn(el));
        elementToolbar.appendChild(addColBtn);

        const remRowBtn = document.createElement('button');
        remRowBtn.textContent = '- Row';
        remRowBtn.addEventListener('click', () => window.removeTableRow(el));
        elementToolbar.appendChild(remRowBtn);

        const remColBtn = document.createElement('button');
        remColBtn.textContent = '- Col';
        remColBtn.addEventListener('click', () => window.removeTableColumn(el));
        elementToolbar.appendChild(remColBtn);
    }

    if (type === 'text') {
        const boldBtn = document.createElement('button');
        boldBtn.innerHTML = '<b>B</b>';
        boldBtn.addEventListener('click', () => window.formatText(el, 'bold'));
        elementToolbar.appendChild(boldBtn);

        const italicBtn = document.createElement('button');
        italicBtn.innerHTML = '<i>I</i>';
        italicBtn.addEventListener('click', () => window.formatText(el, 'italic'));
        elementToolbar.appendChild(italicBtn);

        const underlineBtn = document.createElement('button');
        underlineBtn.innerHTML = '<u>U</u>';
        underlineBtn.addEventListener('click', () => window.formatText(el, 'underline'));
        elementToolbar.appendChild(underlineBtn);

        const strikeBtn = document.createElement('button');
        strikeBtn.innerHTML = '<s>S</s>';
        strikeBtn.addEventListener('click', () => window.formatText(el, 'strikethrough'));
        elementToolbar.appendChild(strikeBtn);
    }

    // Position the toolbar above the element within the canvas
    const canvasRect = window.canvas.getBoundingClientRect();
    const elRect = el.getBoundingClientRect();
    elementToolbar.style.left = (elRect.left - canvasRect.left + window.canvas.scrollLeft) + 'px';
    elementToolbar.style.top = Math.max(0, (elRect.top - canvasRect.top + window.canvas.scrollTop - elementToolbar.offsetHeight - 5)) + 'px';
};

window.hideElementToolbar = function() {
    const elementToolbar = document.getElementById('element-toolbar');
    if (elementToolbar) {
        elementToolbar.style.display = 'none';
        elementToolbar.innerHTML = '';
    }
};

window.selectElement = function(el) {
    // Deselect previous
    if (window.selectedElement && window.selectedElement !== el) {
        window.selectedElement.classList.remove('selected');
    }
    window.selectedElement = el;
    el.classList.add('selected');
    window.showElementToolbar(el);
};

window.deselectAll = function() {
    if (window.selectedElement) {
        window.selectedElement.classList.remove('selected');
        window.selectedElement = null;
    }
    window.hideElementToolbar();
};

window.initSelection = function(container) {
    const elementToolbar = document.createElement('div');
    elementToolbar.id = 'element-toolbar';
    container.appendChild(elementToolbar);

    // Left-click to select
    window.canvas.addEventListener('mousedown', (e) => {
        if (!window.editMode) return;
        if (e.target.closest('#element-toolbar')) return;

        const el = e.target.closest('.draggable');
        if (el && window.canvas.contains(el)) {
            // If clicking on editable content, do NOTHING during mousedown
            // so the browser can focus the contenteditable element normally
            const editable = e.target.closest('[contenteditable="true"]');
            if (editable) {
                // Just mark as selected, no DOM rebuilding
                window.selectedElement = el;
                return;
            }
            window.selectElement(el);
        } else {
            window.deselectAll();
        }
    });

    // Right-click context menu
    window.canvas.addEventListener('contextmenu', (e) => {
        if (!window.editMode) return;
        e.preventDefault();
        const el = e.target.closest('.draggable');
        if (el && window.canvas.contains(el)) {
            window.selectElement(el);
        } else {
            window.deselectAll();
        }
    });

    // Keyboard: Delete / Backspace to remove selected element
    document.addEventListener('keydown', (e) => {
        if (!window.editMode) return;
        if (!window.selectedElement) return;

        // Don't intercept if user is typing in a contenteditable or input
        const active = document.activeElement;
        if (active && (active.isContentEditable ||
            active.tagName === 'INPUT' || active.tagName === 'TEXTAREA' || active.tagName === 'SELECT')) {
            return;
        }

        if (e.key === 'Delete' || e.key === 'Backspace') {
            e.preventDefault();
            window.removeElement(window.selectedElement);
        }
    });

    // Reposition toolbar on scroll
    window.canvas.addEventListener('scroll', () => {
        if (window.selectedElement && elementToolbar.style.display !== 'none') {
            window.showElementToolbar(window.selectedElement);
        }
    });
};
