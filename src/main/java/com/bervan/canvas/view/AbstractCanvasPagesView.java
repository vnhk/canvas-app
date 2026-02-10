package com.bervan.canvas.view;

import com.bervan.canvas.Canvas;
import com.bervan.canvas.CanvasService;
import com.bervan.common.component.BervanButton;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.model.Operator;
import com.bervan.common.search.model.SearchOperation;
import com.bervan.common.view.AbstractBervanEntityView;
import com.bervan.common.view.EmptyLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public abstract class AbstractCanvasPagesView extends AbstractBervanEntityView<UUID, Canvas> {
    public static final String ROUTE_NAME = "/canvas-app/all-canvas-pages";

    private final CanvasService canvasService;
    private VerticalLayout notebooksSection;
    private CanvasComponent canvasComponent;
    private String selectedCategory = null;
    private Div categoriesSection;

    public AbstractCanvasPagesView(@Autowired CanvasService service, BervanViewConfig bervanViewConfig) {
        super(new EmptyLayout(), service, bervanViewConfig, Canvas.class);
        this.canvasService = service;

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.addClassName("canvas-main-layout");
        mainLayout.getStyle().set("position", "relative");

        // Sidebar wrapper (collapsible)
        Div sidebarWrapper = new Div();
        sidebarWrapper.addClassName("canvas-sidebar");

        // Toggle button
        Button toggleBtn = new Button(new Icon(VaadinIcon.MENU));
        toggleBtn.addClassName("canvas-sidebar-toggle");
        toggleBtn.addClickListener(e -> {
            sidebarWrapper.getElement().executeJs(
                    "this.classList.toggle('collapsed');"
            );
        });

        // Categories section
        categoriesSection = new Div();
        categoriesSection.addClassName("canvas-categories-section");

        Span categoriesTitle = new Span("Categories");
        categoriesTitle.addClassName("canvas-section-title");
        categoriesSection.add(categoriesTitle);

        buildCategoriesList();

        // Notebooks section
        notebooksSection = new VerticalLayout();
        notebooksSection.setPadding(false);
        notebooksSection.setSpacing(false);
        notebooksSection.addClassName("canvas-notebooks-section");

        Span notebooksTitle = new Span("Notebooks");
        notebooksTitle.addClassName("canvas-section-title");
        notebooksSection.add(notebooksTitle);

        // Sidebar content
        Div sidebarContent = new Div();
        sidebarContent.addClassName("canvas-sidebar-content");
        sidebarContent.add(categoriesSection, new Hr(), notebooksSection);

        sidebarWrapper.add(toggleBtn, sidebarContent);

        // Canvas area
        canvasComponent = new CanvasComponent(service, null, this.bervanViewConfig);
        canvasComponent.setSizeFull();
        canvasComponent.addClassName("canvas-main-canvas");

        mainLayout.add(sidebarWrapper, canvasComponent);
        mainLayout.setFlexGrow(1, canvasComponent);

        add(mainLayout);
    }

    private void buildCategoriesList() {
        // Clear existing buttons (keep title)
        categoriesSection.getChildren()
                .filter(c -> c instanceof Button || c instanceof BervanButton)
                .toList()
                .forEach(categoriesSection::remove);

        Button noCategory = new Button("All / No category", e -> {
            selectedCategory = null;
            loadNotebooks(null);
        });
        noCategory.addClassName("canvas-notebook-button");
        categoriesSection.add(noCategory);

        Set<String> allCategories = canvasService.findAllCategories();
        for (String category : allCategories) {
            if (category == null || category.isBlank()) {
                continue;
            }
            BervanButton categoryButton = new BervanButton(category, e -> {
                selectedCategory = category;
                loadNotebooks(category);
            });
            categoryButton.addClassName("canvas-notebook-button");
            categoriesSection.add(categoryButton);
        }

        // Add new category inline
        Div addCategoryRow = new Div();
        addCategoryRow.addClassName("canvas-add-category-row");
        TextField newCatField = new TextField();
        newCatField.setPlaceholder("New category...");
        newCatField.setWidthFull();
        newCatField.addClassName("canvas-new-category-field");
        Button addCatBtn = new Button(new Icon(VaadinIcon.PLUS), e -> {
            String val = newCatField.getValue();
            if (val != null && !val.isBlank()) {
                Canvas c = new Canvas();
                c.setCategory(val.trim());
                c.setName("New notebook");
                canvasService.save(c);
                newCatField.clear();
                buildCategoriesList();
                selectedCategory = val.trim();
                loadNotebooks(val.trim());
            }
        });
        addCatBtn.addClassName("canvas-add-btn");
        addCategoryRow.add(newCatField, addCatBtn);
        categoriesSection.add(addCategoryRow);
    }

    private void loadNotebooks(String category) {
        notebooksSection.removeAll();

        Span notebooksTitle = new Span(category != null ? category : "All notebooks");
        notebooksTitle.addClassName("canvas-section-title");
        notebooksSection.add(notebooksTitle);

        Set<Canvas> notebooks;
        if (category == null) {
            notebooks = noCategorySearch();
        } else {
            notebooks = categorySearch(category);
        }

        newItemButton.setWidthFull();
        notebooksSection.add(newItemButton);

        for (Canvas notebook : notebooks) {
            BervanButton notebookButton = new BervanButton(notebook.getName(), click -> {
                canvasComponent.setCanvasEntity(notebook);
                canvasComponent.refresh();
            });
            notebookButton.setWidthFull();
            notebookButton.addClassName("canvas-notebook-button");
            notebooksSection.add(notebookButton);
        }
    }

    private Set<Canvas> noCategorySearch() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.addCriterion("G1", Operator.OR_OPERATOR, Canvas.class, "category", SearchOperation.EQUALS_OPERATION, "");
        searchRequest.addCriterion("G1", Canvas.class, "category", SearchOperation.IS_NULL_OPERATION, null);
        return service.load(searchRequest, Pageable.ofSize(100));
    }

    private Set<Canvas> categorySearch(String category) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.addCriterion("G1", Canvas.class, "category", SearchOperation.EQUALS_OPERATION, category);
        return service.load(searchRequest, Pageable.ofSize(100));
    }
}
