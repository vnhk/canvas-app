// selection.js

// Keep track of currently selected element
window.selectedElement = null;

window.showElementToolbar = function(el) {
    const elementToolbar = document.getElementById('element-toolbar');
    elementToolbar.innerHTML = '';
    elementToolbar.style.display = 'block';

    const type = el.dataset.type;

    // Common remove button
    const removeBtn = document.createElement('button');
    removeBtn.textContent = 'Remove';
    removeBtn.addEventListener('click', () => window.removeElement(el));
    elementToolbar.appendChild(removeBtn);

    // If the element is an image
    if (type === 'image') {
        const changeSrcBtn = document.createElement('button');
        changeSrcBtn.textContent = 'Change Source';
        changeSrcBtn.addEventListener('click', () => window.changeImageSource(el));
        elementToolbar.appendChild(changeSrcBtn);
    }

    // If the element is a table
    if (type === 'table') {
        const addRowBtn = document.createElement('button');
        addRowBtn.textContent = 'Add Row';
        addRowBtn.addEventListener('click', () => window.addTableRow(el));
        elementToolbar.appendChild(addRowBtn);

        const addColBtn = document.createElement('button');
        addColBtn.textContent = 'Add Col';
        addColBtn.addEventListener('click', () => window.addTableColumn(el));
        elementToolbar.appendChild(addColBtn);

        const remRowBtn = document.createElement('button');
        remRowBtn.textContent = 'Remove Row';
        remRowBtn.addEventListener('click', () => window.removeTableRow(el));
        elementToolbar.appendChild(remRowBtn);

        const remColBtn = document.createElement('button');
        remColBtn.textContent = 'Remove Col';
        remColBtn.addEventListener('click', () => window.removeTableColumn(el));
        elementToolbar.appendChild(remColBtn);
    }

    // If the element is text
    if (type === 'text') {
        const boldBtn = document.createElement('button');
        boldBtn.textContent = 'B';
        boldBtn.addEventListener('click', () => window.formatText(el, 'bold'));
        elementToolbar.appendChild(boldBtn);

        const italicBtn = document.createElement('button');
        italicBtn.textContent = 'I';
        italicBtn.addEventListener('click', () => window.formatText(el, 'italic'));
        elementToolbar.appendChild(italicBtn);

        const underlineBtn = document.createElement('button');
        underlineBtn.textContent = 'U';
        underlineBtn.addEventListener('click', () => window.formatText(el, 'underline'));
        elementToolbar.appendChild(underlineBtn);

        const fontSizeBtn = document.createElement('button');
        fontSizeBtn.textContent = 'Font Size';
        fontSizeBtn.addEventListener('click', () => {
            const size = prompt("Enter font size (1-7):", "3");
            if (size) window.formatText(el, 'fontSize', size);
        });
        elementToolbar.appendChild(fontSizeBtn);
    }

    // Position the toolbar above the element
    const rect = el.getBoundingClientRect();
    elementToolbar.style.left = (rect.left + window.scrollX) + 'px';
    elementToolbar.style.top = (rect.top + window.scrollY - elementToolbar.offsetHeight - 5) + 'px';
};

window.hideElementToolbar = function() {
    const elementToolbar = document.getElementById('element-toolbar');
    if (elementToolbar) {
        elementToolbar.style.display = 'none';
        elementToolbar.innerHTML = '';
    }
};

// Initialize selection handling
window.initSelection = function(container) {
    // Create a container for the element toolbar
    const elementToolbar = document.createElement('div');
    elementToolbar.id = 'element-toolbar';
    container.appendChild(elementToolbar);

    // Right-click (contextmenu) event on the canvas to show toolbar
    window.canvas.addEventListener('contextmenu', (e) => {
        if (!window.editMode) return;
        e.preventDefault();
        const el = e.target.closest('.draggable');
        if (el && window.canvas.contains(el)) {
            window.selectedElement = el;
            window.showElementToolbar(el);
        } else {
            window.hideElementToolbar();
            window.selectedElement = null;
        }
    });

    // Reposition toolbar on scroll
    window.addEventListener('scroll', () => {
        if (window.selectedElement && elementToolbar.style.display === 'block') {
            window.showElementToolbar(window.selectedElement);
        }
    });
};