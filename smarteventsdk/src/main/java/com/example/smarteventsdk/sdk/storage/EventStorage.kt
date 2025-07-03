package com.example.smarteventsdk.sdk.storage

import android.content.ContentValues
import android.content.Context
import com.example.smarteventsdk.sdk.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class
EventStorage(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val gson = Gson()
    private val lock = ReentrantReadWriteLock()

    fun insertEvent(event: Event): Boolean {
        return lock.write {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_ID, event.id)
                put(DatabaseHelper.COLUMN_NAME, event.name)
                put(DatabaseHelper.COLUMN_PROPERTIES, gson.toJson(event.properties))
                put(DatabaseHelper.COLUMN_TIMESTAMP, event.timestamp)
                put(DatabaseHelper.COLUMN_IS_SYNCED, if (event.isSynced) 1 else 0)
            }

            val result = db.insert(DatabaseHelper.TABLE_EVENTS, null, values)
            db.close()
            result != -1L
        }
    }

    fun getUnsyncedEvents(): List<Event> {
        return lock.read {
            val db = dbHelper.readableDatabase
            val events = mutableListOf<Event>()

            val cursor = db.query(
                DatabaseHelper.TABLE_EVENTS,
                null,
                "${DatabaseHelper.COLUMN_IS_SYNCED} = ?",
                arrayOf("0"),
                null,
                null,
                "${DatabaseHelper.COLUMN_TIMESTAMP} ASC"
            )

            cursor.use {
                while (it.moveToNext()) {
                    val propertiesJson = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROPERTIES))
                    val properties: Map<String, Any>? = if (propertiesJson != null) {
                        val type = object : TypeToken<Map<String, Any>>() {}.type
                        gson.fromJson(propertiesJson, type)
                    } else null

                    events.add(
                        Event(
                            id = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                            properties = properties,
                            timestamp = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP)),
                            isSynced = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_SYNCED)) == 1
                        )
                    )
                }
            }

            db.close()
            events
        }
    }

    fun markEventsSynced(eventIds: List<String>): Boolean {
        return lock.write {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_IS_SYNCED, 1)
            }

            val placeholders = eventIds.joinToString(",") { "?" }
            val result = db.update(
                DatabaseHelper.TABLE_EVENTS,
                values,
                "${DatabaseHelper.COLUMN_ID} IN ($placeholders)",
                eventIds.toTypedArray()
            )

            db.close()
            result > 0
        }
    }

    fun getAllEvents(): List<Event> {
        return lock.read {
            val db = dbHelper.readableDatabase
            val events = mutableListOf<Event>()

            val cursor = db.query(DatabaseHelper.TABLE_EVENTS, null, null, null, null, null, "${DatabaseHelper.COLUMN_TIMESTAMP} DESC")

            cursor.use {
                while (it.moveToNext()) {
                    val propertiesJson = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROPERTIES))
                    val properties: Map<String, Any>? = if (propertiesJson != null) {
                        val type = object : TypeToken<Map<String, Any>>() {}.type
                        gson.fromJson(propertiesJson, type)
                    } else null

                    events.add(
                        Event(
                            id = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)),
                            properties = properties,
                            timestamp = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP)),
                            isSynced = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_SYNCED)) == 1
                        )
                    )
                }
            }

            db.close()
            events
        }
    }
}