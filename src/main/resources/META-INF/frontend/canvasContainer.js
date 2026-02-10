window.initCanvasContainer = function(container) {
    const canvas = document.createElement('div');
    canvas.id = 'canvas';
    container.appendChild(canvas);
    window.canvas = canvas;

    // Auto-save indicator - placed OUTSIDE canvas to avoid MutationObserver loop
    const indicator = document.createElement('div');
    indicator.id = 'canvas-autosave-indicator';
    indicator.textContent = '';
    container.style.position = 'relative';
    container.appendChild(indicator);

    // Auto-save with debounce
    let autoSaveTimer = null;
    window._canvasAutoSaveElement = null; // set by CanvasComponent
    window._canvasHasEntity = false; // set by CanvasComponent
    window._ignoreObserver = false; // set by loadLayout

    function scheduleAutoSave() {
        if (!window._canvasAutoSaveElement) return;
        if (!window._canvasHasEntity) return;
        if (window._ignoreObserver) return;
        if (autoSaveTimer) clearTimeout(autoSaveTimer);
        indicator.textContent = 'Unsaved changes...';
        indicator.style.opacity = '0.8';
        autoSaveTimer = setTimeout(() => {
            if (!window._canvasHasEntity) return;
            const layoutData = window.getCurrentLayout();
            const jsonString = JSON.stringify(layoutData);
            window._canvasAutoSaveElement.$server.saveLayout(jsonString);
            indicator.textContent = 'Saved';
            setTimeout(() => {
                indicator.style.opacity = '0.4';
                setTimeout(() => { indicator.textContent = ''; }, 1500);
            }, 800);
        }, 2000);
    }

    window.scheduleAutoSave = scheduleAutoSave;

    // Only auto-save on user input (typing in contenteditable)
    // Don't use MutationObserver — it fires on programmatic DOM changes
    // (classList, contentEditable attribute, etc.) causing cascading issues
    canvas.addEventListener('input', () => {
        if (window.editMode && window._canvasHasEntity && !window._ignoreObserver) {
            scheduleAutoSave();
        }
    });
};
