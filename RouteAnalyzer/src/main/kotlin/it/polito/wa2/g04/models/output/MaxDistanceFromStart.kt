package it.polito.wa2.g04.models.output

import it.polito.wa2.g04.models.Waypoint
import kotlinx.serialization.Serializable

/**
 * Data class representing the waypoint that is farthest from the starting point and the corresponding distance.
 *
 * @property waypoint The waypoint that is the farthest from the starting point.
 * @property distanceKm The distance in kilometers from the starting point to the farthest waypoint.
 */
@Serializable
data class MaxDistanceFromStart(
    val waypoint: Waypoint,
    val distanceKm: Double
)
