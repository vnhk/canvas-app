window.injectStyles = function() {
    const style = document.createElement('style');
    style.textContent = `
    .container {
      padding: 10px;
      background-color: var(--canvas-main-canvas-background, #1a1a2e);
      color: var(--canvas-main-canvas-text-color, #e0e0e0);
    }

    #toolbar {
      background: var(--canvas-toolbar-bg, rgba(255,255,255,0.08));
      padding: 6px 10px;
      display: flex;
      flex-wrap: wrap;
      gap: 2px;
      margin-bottom: 8px;
      border-radius: 8px;
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.12));
      align-items: center;
    }

    #toolbar .toolbar-separator {
      width: 1px;
      height: 24px;
      background: var(--canvas-toolbar-border, rgba(255,255,255,0.15));
      margin: 0 6px;
    }

    #toolbar .toolbar-group {
      display: flex;
      gap: 2px;
      align-items: center;
    }

    #toolbar button {
      cursor: pointer;
      background: var(--canvas-toolbar-btn-bg, rgba(255,255,255,0.06));
      color: var(--canvas-main-canvas-text-color, #e0e0e0);
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.1));
      border-radius: 4px;
      padding: 4px 8px;
      font-size: 13px;
      transition: background 0.15s;
      line-height: 1.4;
    }

    #toolbar button:hover {
      background: var(--canvas-toolbar-btn-hover, rgba(255,255,255,0.15));
    }

    #toolbar button.active {
      background: var(--canvas-toolbar-btn-active, rgba(100,149,237,0.3));
      border-color: var(--canvas-toolbar-btn-active-border, rgba(100,149,237,0.6));
    }

    #toolbar select {
      background: var(--canvas-toolbar-btn-bg, rgba(255,255,255,0.06));
      color: var(--canvas-main-canvas-text-color, #e0e0e0);
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.1));
      border-radius: 4px;
      padding: 4px 6px;
      font-size: 13px;
      cursor: pointer;
    }

    #toolbar input[type="color"] {
      width: 28px;
      height: 28px;
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.1));
      border-radius: 4px;
      padding: 2px;
      cursor: pointer;
      background: transparent;
    }

    #canvas {
      position: relative;
      width: 100%;
      height: calc(100vh - 160px);
      overflow: auto;
      color: var(--canvas-main-canvas-text-color, #e0e0e0);
      background-color: var(--canvas-main-canvas-background, #1a1a2e);
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.1));
      border-radius: 6px;
    }

    .draggable {
      position: absolute;
      border: 1px solid var(--canvas-draggable-border, rgba(255,255,255,0.15));
      background: var(--canvas-draggable-bg, rgba(255,255,255,0.04));
      padding: 8px;
      cursor: move;
      border-radius: 4px;
      min-width: 60px;
      min-height: 30px;
      transition: box-shadow 0.15s;
    }

    .draggable.selected {
      border-color: var(--canvas-draggable-selected, cornflowerblue);
      box-shadow: 0 0 0 2px var(--canvas-draggable-selected, cornflowerblue);
    }

    .draggable[data-type="image"] img {
      max-width: 100%;
      height: auto;
      display: block;
    }

    .draggable table {
      border-collapse: collapse;
      width: 100%;
    }

    .draggable table td, .draggable table th {
      border: 1px solid var(--canvas-draggable-border, rgba(255,255,255,0.2));
      padding: 6px 8px;
      min-width: 40px;
    }

    .draggable[data-type="text"] div[contenteditable] {
      width: 100%;
      height: 100%;
      border: none;
      outline: none;
      min-height: 20px;
      cursor: text;
    }

    .draggable[data-type="table"] td[contenteditable] {
      cursor: text;
    }

    .draggable[data-type="divider"] {
      padding: 4px 0;
      cursor: move;
      background: transparent;
      border: none;
    }

    .draggable[data-type="divider"].selected {
      border: 1px dashed var(--canvas-draggable-selected, cornflowerblue);
      box-shadow: none;
    }

    .draggable[data-type="divider"] hr {
      border: none;
      border-top: 2px solid var(--canvas-draggable-border, rgba(255,255,255,0.3));
      margin: 0;
    }

    .resize-handle {
      position: absolute;
      width: 12px;
      height: 12px;
      bottom: 0;
      right: 0;
      cursor: nwse-resize;
      background: var(--canvas-draggable-selected, cornflowerblue);
      opacity: 0;
      border-radius: 2px;
      transition: opacity 0.15s;
    }

    .draggable:hover .resize-handle,
    .draggable.selected .resize-handle {
      opacity: 0.6;
    }

    .resize-handle:hover {
      opacity: 1 !important;
    }

    #element-toolbar {
      position: absolute;
      background: var(--canvas-element-toolbar-bg, rgba(30,30,50,0.95));
      color: var(--canvas-main-canvas-text-color, #fff);
      padding: 4px 6px;
      display: none;
      z-index: 999;
      border-radius: 6px;
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.15));
      box-shadow: 0 4px 12px rgba(0,0,0,0.3);
      gap: 3px;
      flex-wrap: wrap;
    }

    #element-toolbar button {
      background: var(--canvas-toolbar-btn-bg, rgba(255,255,255,0.1));
      color: var(--canvas-main-canvas-text-color, #fff);
      margin: 0 1px;
      border: 1px solid var(--canvas-toolbar-border, rgba(255,255,255,0.1));
      padding: 3px 7px;
      font-size: 12px;
      cursor: pointer;
      border-radius: 3px;
      transition: background 0.15s;
    }

    #element-toolbar button:hover {
      background: var(--canvas-toolbar-btn-hover, rgba(255,255,255,0.2));
    }

    #canvas-autosave-indicator {
      position: absolute;
      bottom: 8px;
      right: 12px;
      font-size: 11px;
      color: var(--canvas-main-canvas-text-color, #aaa);
      opacity: 0.6;
      z-index: 50;
      pointer-events: none;
      transition: opacity 0.3s;
    }

    .canvas-header-row {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 4px;
    }

    .canvas-header-field {
      background: transparent;
      border: 1px solid transparent;
      color: var(--canvas-main-canvas-text-color, #e0e0e0);
      font-size: 16px;
      font-weight: bold;
      padding: 4px 8px;
      border-radius: 4px;
      outline: none;
      transition: border-color 0.2s;
    }

    .canvas-header-field:hover {
      border-color: var(--canvas-toolbar-border, rgba(255,255,255,0.2));
    }

    .canvas-header-field:focus {
      border-color: var(--canvas-draggable-selected, cornflowerblue);
    }

    .canvas-header-field.category-field {
      font-size: 13px;
      font-weight: normal;
      opacity: 0.7;
    }
  `;
    document.head.appendChild(style);
};
