window.initToolbar = function(container) {
    let editMode = true;

    const toolbar = document.createElement('div');
    toolbar.id = 'toolbar';

    const addImageBtn = document.createElement('button');
    addImageBtn.id = 'addImage';
    addImageBtn.textContent = 'Add Image';

    const addTableBtn = document.createElement('button');
    addTableBtn.id = 'addTable';
    addTableBtn.textContent = 'Add Table';

    const addTextBtn = document.createElement('button');
    addTextBtn.id = 'addText';
    addTextBtn.textContent = 'Add Text';

    const saveLayoutBtn = document.createElement('button');
    saveLayoutBtn.id = 'saveLayout';
    saveLayoutBtn.textContent = 'Save';

    const toggleEditBtn = document.createElement('button');
    toggleEditBtn.id = 'toggleEditMode';
    toggleEditBtn.textContent = 'Edit Mode: ON';

    toolbar.appendChild(addImageBtn);
    toolbar.appendChild(addTableBtn);
    toolbar.appendChild(addTextBtn);
    toolbar.appendChild(saveLayoutBtn);
    toolbar.appendChild(toggleEditBtn);

    container.appendChild(toolbar);

    // Store these globally if needed
    window.addImageBtn = addImageBtn;
    window.addTableBtn = addTableBtn;
    window.addTextBtn = addTextBtn;
    window.saveLayoutBtn = saveLayoutBtn;
    window.toggleEditBtn = toggleEditBtn;
    window.editMode = editMode;
};