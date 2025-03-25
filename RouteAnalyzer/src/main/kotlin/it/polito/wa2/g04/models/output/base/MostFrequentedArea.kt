package it.polito.wa2.g04.models.output.base

import it.polito.wa2.g04.models.Waypoint
import kotlinx.serialization.Serializable

/**
 * Data class representing the most frequented geographical area based on waypoints.
 *
 * @property centralWaypoint The central waypoint of the most frequented area.
 * @property areaRadiusKm The radius of the area in kilometers around the central waypoint.
 * @property entriesCount The number of waypoints or visits recorded in this area.
 */
@Serializable
data class MostFrequentedArea(
    val centralWaypoint: Waypoint,
    val areaRadiusKm: Double,
    val entriesCount: Int
)
