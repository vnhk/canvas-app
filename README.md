# canvas-app

Web-based collaborative canvas/notebook. Users create documents with draggable, resizable elements (text, images, tables, dividers), organized by category with full revision history and auto-save.

## Features

- **Element types**: Text (rich formatting), Image (URL-based), Table (editable), Divider
- **Editing**: Drag & resize all elements, context menus, edit-mode toggle to prevent accidental changes
- **Auto-save**: 2-second debounce on any content change
- **History**: Full audit trail via `@HistorySupported` (rollback supported)
- **Multi-tenancy**: Each user sees only their own canvases
- **Import/Export**: Excel via `@ExcelIEEntity`

## Key Entities

| Entity | Description |
|--------|-------------|
| `Canvas` | Document with name, category, JSON layout (up to 5 MB) |
| `HistoryCanvas` | Snapshot of name + content per change |

## Architecture

- `CanvasService` — CRUD with `@PostFilter` row-level security
- `CanvasComponent` — main editor backed by 7 JS modules (`toolbar.js`, `draggable.js`, `elements.js`, etc.)
- `@ClientCallable` methods: `saveLayout(json)`, `updateName()`, `updateCategory()`
- React frontend: `my-tools-react/src/pages/canvas/`

## Build

```bash
mvn clean install -DskipTests
```

Part of the `my-tools` multi-module Maven project (`com.bervan:my-tools`). Requires `common` to be built first.
