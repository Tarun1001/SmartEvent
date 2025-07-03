package com.example.smarteventsdk.sdk

import android.content.Context
import com.example.smarteventsdk.sdk.model.Event
import com.example.smarteventsdk.sdk.network.MockServer
import com.example.smarteventsdk.sdk.storage.EventStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.Executors

object SmartEvent {

    private var isInitialized = false
    private lateinit var eventStorage: EventStorage
    private lateinit var mockServer: MockServer

    private var eventFilter: ((String, Map<String, Any>?) -> Boolean)? = null
    private var eventListener: SmartEventListener? = null

    // Use single-threaded executor for sequential operations
    private val executor = Executors.newSingleThreadExecutor()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context) {
        if (isInitialized) return

        eventStorage = EventStorage(context.applicationContext)
        mockServer = MockServer()
        isInitialized = true
    }

    fun setEventFilter(filter: (String, Map<String, Any>?) -> Boolean) {
        eventFilter = filter
    }

    fun setEventListener(listener: SmartEventListener?) {
        eventListener = listener
    }

    fun log(eventName: String, properties: Map<String, Any>? = null) {
        if (!isInitialized) {
            throw IllegalStateException("SmartEvent not initialized. Call initialize() first.")
        }

        // Apply filter if set
        if (eventFilter?.invoke(eventName, properties) == false) {
            return
        }

        val event = Event(
            id = UUID.randomUUID().toString(),
            name = eventName,
            properties = properties,
            timestamp = System.currentTimeMillis(),
            isSynced = false
        )

        // Execute on background thread
        executor.execute {
            val success = eventStorage.insertEvent(event)
            if (success) {
                eventListener?.onEventStored(event.id)
            }
        }
    }

    fun flush() {
        if (!isInitialized) {
            throw IllegalStateException("SmartEvent not initialized. Call initialize() first.")
        }

        coroutineScope.launch {
            val unsyncedEvents = eventStorage.getUnsyncedEvents()

            if (unsyncedEvents.isEmpty()) {
                eventListener?.onFlushCompleted(0, 0)
                return@launch
            }

            try {
                val response = mockServer.uploadEvents(unsyncedEvents)

                if (response.success) {
                    val eventIds = unsyncedEvents.map { it.id }
                    eventStorage.markEventsSynced(eventIds)
                    eventListener?.onFlushCompleted(response.processedCount, 0)
                } else {
                    eventListener?.onFlushCompleted(0, unsyncedEvents.size)
                }
            } catch (e: Exception) {
                eventListener?.onFlushCompleted(0, unsyncedEvents.size)
            }
        }
    }

    // Helper method for testing
    internal fun getAllEvents(): List<Event> {
        return if (isInitialized) {
            eventStorage.getAllEvents()
        } else {
            emptyList()
        }
    }

    // Clean up resources
    fun shutdown() {
        coroutineScope.cancel()
        executor.shutdown()
        isInitialized = false
    }
}
