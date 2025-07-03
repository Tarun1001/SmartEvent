package com.example.smarteventsdk.sdk

interface SmartEventListener {
    fun onEventStored(eventId: String)
    fun onFlushCompleted(successCount: Int, failedCount: Int)
}