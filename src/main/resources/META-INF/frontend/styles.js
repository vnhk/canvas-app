window.injectStyles = function() {
    const style = document.createElement('style');
    style.textContent = `
    .container {
      padding: 10px;
      background-color: black;
    }

    #toolbar {
      background: #eee;
      padding: 10px;
      display: flex;
      gap: 10px;
      margin-bottom: 10px;
    }

    button {
      cursor: pointer;
    }

    #canvas {
      position: relative;
      width: 100%;
      height: 80vh;
      overflow: hidden;
      border: 1px solid #ccc;
    }

    .draggable {
      position: absolute;
      border: 1px solid #aaa;
      background: black;
      padding: 5px;
      cursor: move;
    }

    .draggable[data-type="image"] img {
      max-width: 200px;
      height: auto;
      display: block;
    }

    .draggable table {
      border-collapse: collapse;
    }

    .draggable table, .draggable td, .draggable th {
      border: 1px solid #555;
      padding: 5px;
    }

    .draggable[data-type="text"] div[contenteditable="true"] {
      width: 100%;
      height: 100%;
      border: none;
      outline: none;
    }

    #element-toolbar {
      position: absolute;
      background: #333;
      color: #fff;
      padding: 5px;
      display: none;
      z-index: 999;
    }

    #element-toolbar button {
      background: #555;
      color: #fff;
      margin: 0 2px;
      border: none;
      padding: 3px 5px;
      font-size: 12px;
      cursor: pointer;
    }
    #element-toolbar button:hover {
      background: #777;
    }
  `;
    document.head.appendChild(style);
};