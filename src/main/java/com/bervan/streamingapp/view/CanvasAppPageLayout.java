package com.bervan.streamingapp.view;

import com.bervan.common.MenuNavigationComponent;

public final class CanvasAppPageLayout extends MenuNavigationComponent {

    public CanvasAppPageLayout(boolean isEdit, String canvasName) {
        super(AbstractCanvasPagesView.ROUTE_NAME);

        addButtonIfVisible(menuButtonsRow, AbstractCanvasPagesView.ROUTE_NAME, "List");
        if (isEdit) {
            addButtonIfVisible(menuButtonsRow, CanvasView.ROUTE_NAME, canvasName);
        }

        add(menuButtonsRow);
    }
}
