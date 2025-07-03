package com.example.smarteventsdk.sdk.network

import com.example.smarteventsdk.sdk.model.Event
import kotlinx.coroutines.delay
import kotlin.random.Random

class MockServer {

    data class UploadResponse(
        val success: Boolean,
        val message: String,
        val processedCount: Int
    )

    suspend fun uploadEvents(events: List<Event>): UploadResponse {
        // Simulate network delay
        delay(Random.nextLong(500, 2000))

        // Simulate 10% failure rate
        val success = Random.nextFloat() > 0.1f

        return if (success) {
            UploadResponse(
                success = true,
                message = "Events uploaded successfully",
                processedCount = events.size
            )
        } else {
            UploadResponse(
                success = false,
                message = "Server error occurred",
                processedCount = 0
            )
        }
    }
}