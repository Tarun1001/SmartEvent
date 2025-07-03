package com.example.smarteventsdk.sdk.model

data class Event(
    val id: String,
    val name: String,
    val properties: Map<String, Any>?,
    val timestamp: Long,
    val isSynced: Boolean = false
)