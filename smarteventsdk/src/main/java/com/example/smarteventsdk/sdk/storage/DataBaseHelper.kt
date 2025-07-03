package com.example.smarteventsdk.sdk.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "smart_events.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EVENTS = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PROPERTIES = "properties"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IS_SYNCED = "is_synced"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EVENTS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PROPERTIES TEXT,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_IS_SYNCED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }
}