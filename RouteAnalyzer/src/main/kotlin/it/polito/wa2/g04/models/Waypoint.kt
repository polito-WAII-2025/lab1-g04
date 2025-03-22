package it.polito.wa2.g04.models
import kotlinx.serialization.Serializable

/**
 * Data class representing a waypoint, which is a specific geographical point in time and space.
 *
 * @property timestamp The timestamp of when the waypoint was recorded, in seconds (or another time unit, depending on usage).
 * @property latitude The latitude of the waypoint in degrees.
 * @property longitude The longitude of the waypoint in degrees.
 */
@Serializable
data class Waypoint(
    val timestamp: Double,
    val latitude: Double,
    val longitude: Double,
)

