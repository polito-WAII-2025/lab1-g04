package it.polito.wa2.g04.models.output

import kotlinx.serialization.Serializable

/**
 * Data class representing a report that aggregates different metrics based on waypoint analysis.
 *
 * @property maxDistanceFromStart The data representing the waypoint farthest from the starting point and its distance.
 * @property mostFrequentedArea The data representing the most frequented area based on the waypoints.
 * @property waypointsOutsideGeofence The data representing waypoints that are located outside a defined geofence.
 */
@Serializable
data class DataReport(
    val maxDistanceFromStart: MaxDistanceFromStart,
    val mostFrequentedArea: MostFrequentedArea,
    val waypointsOutsideGeofence: WaypointsOutsideGeofence
)
