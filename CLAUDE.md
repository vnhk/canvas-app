# Canvas App - Project Notes

> **IMPORTANT**: Keep this file updated when making significant changes to the codebase. This file serves as persistent memory between Claude Code sessions.

## Overview
Web-based collaborative canvas/notebook application. Users create, organize, and visually edit canvas documents with draggable/resizable elements (text, images, tables, dividers). Features categories, edit mode toggle, auto-save, and full revision history.

## Key Architecture

### Entities

#### Canvas
- `id: UUID`, `name: String` (max 100, unique per owner), `category: String`
- `content: String` (max 5MB LONGTEXT) - JSON-serialized layout
- `modificationDate`, `creationDate`, `deleted: Boolean`
- `history: Set<HistoryCanvas>` (eager-loaded)
- Annotations: `@HistorySupported`, `@ExcelIEEntity`
- Unique constraint on (name, owner.id)

#### HistoryCanvas
- `id: UUID`, `name: String` (`@HistoryField`), `content: String` (`@HistoryField`)
- `updateDate: LocalDateTime`, `canvas: Canvas` (`@HistoryOwnerEntity`, eager)
- Stores complete change history for audit/rollback

### Services

#### CanvasService
- Extends `BaseService<UUID, Canvas>`
- `loadByName(String)` - find by name with `@PostFilter` security
- `delete(Canvas)` - soft delete (sets `deleted=true`)
- `loadHistory()` - all history with access filtering
- `findAllCategories()` - distinct non-deleted categories

### Views

#### AbstractCanvasPagesView
- Route: `/canvas-app/all-canvas-pages`
- Sidebar with collapsible toggle, category browsing, canvas listing
- Integrates `CanvasComponent` for inline editing

#### CanvasComponent (Main Editor)
- Extends `AbstractBervanEntityView<UUID, Canvas>`
- 7 JavaScript modules for rich UI:
  - `styles.js`, `toolbar.js`, `canvasContainer.js`, `draggable.js`
  - `elements.js`, `layout.js`, `selection.js`
- `@ClientCallable` methods: `saveLayout(String json)`, `updateName(String)`, `updateCategory(String)`
- Auto-save: 2-second debounce on content changes

### Element Types
1. **Text** - Contenteditable with full formatting (bold, italic, headings, colors, lists)
2. **Image** - URL-based with change-source capability
3. **Table** - Editable cells, add/remove rows and columns
4. **Divider** - Horizontal separators

All elements: draggable, resizable (corner handles), selectable with context menus.

## Configuration
- `src/main/resources/autoconfig/Canvas.yml` - Column metadata for form generation
- Layout JSON stored as LONGTEXT (up to 5MB per canvas)

## Important Notes
1. Multi-tenancy: all entities owner-scoped, users see only their own canvases
2. Soft deletes via `deleted` flag
3. Revision history auto-tracked via `@HistorySupported`
4. Excel import/export via `ExcelIEEntity<UUID>`
5. Spring Security `@PostFilter` for row-level access control
6. Edit mode toggle prevents accidental modifications
7. Frontend: Vaadin 24 + Vite 5 + Workbox 7
