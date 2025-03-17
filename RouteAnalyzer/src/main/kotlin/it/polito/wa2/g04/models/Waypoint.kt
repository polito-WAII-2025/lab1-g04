package it.polito.wa2.g04.models
import kotlinx.serialization.Serializable

@Serializable
data class Waypoint(
    val timestamp: Double,
    val latitude: Double,
    val longitude: Double,
)

