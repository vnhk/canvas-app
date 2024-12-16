package com.bervan.canvasapp.view;

import com.bervan.common.MenuNavigationComponent;

public final class CanvasAppPageLayout extends MenuNavigationComponent {

    public CanvasAppPageLayout(boolean isEdit, String canvasName) {
        super(AbstractCanvasPagesView.ROUTE_NAME);

        addButton(menuButtonsRow, AbstractCanvasPagesView.ROUTE_NAME, "List");
        if (isEdit) {
            addButton(menuButtonsRow, AbstractCanvasView.ROUTE_NAME, canvasName);
        }

        add(menuButtonsRow);
    }
}
