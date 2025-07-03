# SmartEvent SDK Design Document

## Architecture Overview

The SmartEvent SDK follows a layered architecture with clear separation of concerns:

1. **Public API Layer** (`SmartEvent.kt`) - Main entry point
2. **Storage Layer** (`EventStorage.kt`, `DatabaseHelper.kt`) - Data persistence
3. **Network Layer** (`MockServer.kt`) - Server communication
4. **Models** (`Event.kt`) - Data structures

## Persistence Strategy

### SQLite Database
- Uses SQLite for reliable, structured data storage
- Single table design with JSON serialization for properties
- Indexes on timestamp and sync status for efficient queries
- ACID compliance ensures data integrity

### Schema Design
```sql
CREATE TABLE events (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    properties TEXT,           -- JSON serialized
    timestamp INTEGER NOT NULL,
    is_synced INTEGER NOT NULL DEFAULT 0
);