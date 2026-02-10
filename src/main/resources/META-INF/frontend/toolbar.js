window.initToolbar = function(container) {
    let editMode = true;

    const toolbar = document.createElement('div');
    toolbar.id = 'toolbar';

    function createBtn(id, text, title) {
        const btn = document.createElement('button');
        btn.id = id;
        btn.textContent = text;
        if (title) btn.title = title;
        return btn;
    }

    function createGroup() {
        const g = document.createElement('div');
        g.className = 'toolbar-group';
        return g;
    }

    function createSeparator() {
        const s = document.createElement('div');
        s.className = 'toolbar-separator';
        return s;
    }

    // Elements group
    const elemGroup = createGroup();
    const addTextBtn = createBtn('addText', '+ Text', 'Add text element');
    const addImageBtn = createBtn('addImage', '+ Image', 'Add image element');
    const addTableBtn = createBtn('addTable', '+ Table', 'Add table element');
    const addDividerBtn = createBtn('addDivider', '+ Divider', 'Add horizontal divider');
    elemGroup.append(addTextBtn, addImageBtn, addTableBtn, addDividerBtn);

    // Text formatting group
    const fmtGroup = createGroup();
    const boldBtn = createBtn('fmtBold', 'B', 'Bold');
    boldBtn.style.fontWeight = 'bold';
    const italicBtn = createBtn('fmtItalic', 'I', 'Italic');
    italicBtn.style.fontStyle = 'italic';
    const underlineBtn = createBtn('fmtUnderline', 'U', 'Underline');
    underlineBtn.style.textDecoration = 'underline';
    const strikeBtn = createBtn('fmtStrike', 'S', 'Strikethrough');
    strikeBtn.style.textDecoration = 'line-through';
    fmtGroup.append(boldBtn, italicBtn, underlineBtn, strikeBtn);

    // Headings group
    const headGroup = createGroup();
    const h1Btn = createBtn('fmtH1', 'H1', 'Heading 1');
    const h2Btn = createBtn('fmtH2', 'H2', 'Heading 2');
    const h3Btn = createBtn('fmtH3', 'H3', 'Heading 3');
    headGroup.append(h1Btn, h2Btn, h3Btn);

    // Font size
    const sizeGroup = createGroup();
    const fontSizeSelect = document.createElement('select');
    fontSizeSelect.id = 'fmtFontSize';
    fontSizeSelect.title = 'Font size';
    [1,2,3,4,5,6,7].forEach(s => {
        const opt = document.createElement('option');
        opt.value = s;
        opt.textContent = 'Size ' + s;
        if (s === 3) opt.selected = true;
        fontSizeSelect.appendChild(opt);
    });
    sizeGroup.append(fontSizeSelect);

    // Color group
    const colorGroup = createGroup();
    const textColor = document.createElement('input');
    textColor.type = 'color';
    textColor.id = 'fmtTextColor';
    textColor.title = 'Text color';
    textColor.value = '#ffffff';
    const bgColor = document.createElement('input');
    bgColor.type = 'color';
    bgColor.id = 'fmtBgColor';
    bgColor.title = 'Background color';
    bgColor.value = '#000000';
    colorGroup.append(textColor, bgColor);

    // Lists group
    const listGroup = createGroup();
    const ulBtn = createBtn('fmtUL', '• List', 'Bullet list');
    const olBtn = createBtn('fmtOL', '1. List', 'Numbered list');
    listGroup.append(ulBtn, olBtn);

    // Actions group
    const actGroup = createGroup();
    const deleteSelBtn = createBtn('deleteSelected', 'Delete', 'Delete selected element');
    const saveLayoutBtn = createBtn('saveLayout', 'Save', 'Save layout');
    const toggleEditBtn = createBtn('toggleEditMode', 'Edit: ON', 'Toggle edit mode');
    actGroup.append(deleteSelBtn, saveLayoutBtn, toggleEditBtn);

    toolbar.append(
        elemGroup, createSeparator(),
        fmtGroup, createSeparator(),
        headGroup, createSeparator(),
        sizeGroup, colorGroup, createSeparator(),
        listGroup, createSeparator(),
        actGroup
    );

    container.appendChild(toolbar);

    // Store globals
    window.addImageBtn = addImageBtn;
    window.addTableBtn = addTableBtn;
    window.addTextBtn = addTextBtn;
    window.addDividerBtn = addDividerBtn;
    window.saveLayoutBtn = saveLayoutBtn;
    window.toggleEditBtn = toggleEditBtn;
    window.deleteSelectedBtn = deleteSelBtn;
    window.boldBtn = boldBtn;
    window.italicBtn = italicBtn;
    window.underlineBtn = underlineBtn;
    window.strikeBtn = strikeBtn;
    window.h1Btn = h1Btn;
    window.h2Btn = h2Btn;
    window.h3Btn = h3Btn;
    window.fontSizeSelect = fontSizeSelect;
    window.textColorInput = textColor;
    window.bgColorInput = bgColor;
    window.ulBtn = ulBtn;
    window.olBtn = olBtn;
    window.editMode = editMode;
};
