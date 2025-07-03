package com.example.smartevent

import SmartEventDemoScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartevent.ui.theme.SmartEventTheme
import com.example.smarteventsdk.sdk.SmartEvent
import com.example.smarteventsdk.sdk.SmartEventListener
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*
class MainActivity : ComponentActivity(), SmartEventListener {
    private var eventLog by mutableStateOf("SDK initialized. Ready to log events...")
    private var eventCounter = 0
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SmartEvent SDK
        SmartEvent.initialize(this)
        SmartEvent.setEventListener(this)

        // Set up initial event filter
        setupEventFilter()

        setContent {
            SmartEventTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartEventDemoScreen(
                        eventLog = eventLog,
                        onLogEvent = { logSimpleEvent() },
                        onLogEventWithProperties = { logEventWithProperties() },
                        onLogDebugEvent = { logDebugEvent() },
                        onFlushEvents = { flushEvents() },
                        onFilterToggle = { enabled -> toggleEventFilter(enabled) }
                    )
                }
            }
        }
    }

    private fun setupEventFilter() {
        SmartEvent.setEventFilter { eventName, properties ->
            val shouldLog = eventName != "debug_event"
            if (!shouldLog) {
                appendToLog("🚫 Filtered out: $eventName")
            }
            shouldLog
        }
    }

    private fun logSimpleEvent() {
        eventCounter++
        SmartEvent.log("button_click", mapOf(
            "button_name" to "log_event",
            "counter" to eventCounter
        ))
    }

    private fun logEventWithProperties() {
        eventCounter++
        SmartEvent.log("user_action", mapOf(
            "action_type" to "button_press",
            "screen" to "main",
            "timestamp" to System.currentTimeMillis(),
            "user_id" to "demo_user_123",
            "counter" to eventCounter
        ))
    }

    private fun logDebugEvent() {
        eventCounter++
        SmartEvent.log("debug_event", mapOf(
            "debug_info" to "This should be filtered",
            "counter" to eventCounter
        ))
    }

    private fun flushEvents() {
        appendToLog("🚀 Initiating flush...")
        SmartEvent.flush()
    }

    private fun toggleEventFilter(enabled: Boolean) {
        if (enabled) {
            setupEventFilter()
            appendToLog("🔍 Debug filter enabled")
        } else {
            SmartEvent.setEventFilter { _, _ -> true }
            appendToLog("🔍 Debug filter disabled")
        }
    }

    private fun appendToLog(message: String) {
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] $message"

        eventLog = if (eventLog.isEmpty()) {
            logEntry
        } else {
            "$eventLog\n$logEntry"
        }
    }

    override fun onEventStored(eventId: String) {
        appendToLog("✅ Event stored: ${eventId.take(8)}...")
    }

    override fun onFlushCompleted(successCount: Int, failedCount: Int) {
        if (failedCount > 0) {
            appendToLog("❌ Flush completed: $successCount success, $failedCount failed")
        } else {
            appendToLog("✅ Flush completed: $successCount events uploaded")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SmartEvent.shutdown()
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartEventTheme {
        Greeting("Android")
    }
}