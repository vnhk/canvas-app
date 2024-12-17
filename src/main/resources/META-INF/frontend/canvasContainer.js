window.initCanvasContainer = function(container) {
    const canvas = document.createElement('div');
    canvas.id = 'canvas';
    container.appendChild(canvas);
    window.canvas = canvas;
};