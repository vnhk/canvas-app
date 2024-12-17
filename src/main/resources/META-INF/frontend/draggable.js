window.makeDraggable = function(el) {
    let offsetX = 0;
    let offsetY = 0;
    let isDown = false;

    el.addEventListener('mousedown', (e) => {
        if (!window.editMode) return;
        if (e.target.closest('#element-toolbar')) return;
        isDown = true;
        offsetX = e.clientX - el.offsetLeft;
        offsetY = e.clientY - el.offsetTop;
        el.style.zIndex = 10;
    });

    document.addEventListener('mousemove', (e) => {
        if (!window.editMode || !isDown) return;
        const x = e.clientX - offsetX;
        const y = e.clientY - offsetY;
        el.style.left = x + 'px';
        el.style.top = y + 'px';
    });

    document.addEventListener('mouseup', () => {
        if (!window.editMode) return;
        isDown = false;
        el.style.zIndex = 1;
    });
};

window.createDraggableElement = function() {
    const el = document.createElement('div');
    el.classList.add('draggable');
    el.style.top = '50px';
    el.style.left = '50px';
    window.makeDraggable(el);
    return el;
};