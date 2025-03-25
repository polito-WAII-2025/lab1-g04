package it.polito.wa2.g04.models.output.base

import it.polito.wa2.g04.models.Waypoint
import kotlinx.serialization.Serializable

/**
 * Data class representing the waypoints located outside a defined geofence.
 *
 * @property centralWaypoint The central waypoint that defines the geofence's center.
 * @property areaRadiusKm The radius of the geofence in kilometers.
 * @property count The number of waypoints that fall outside the geofence boundary.
 * @property waypoints The list of waypoints that are located outside the geofence.
 */
@Serializable
data class WaypointsOutsideGeofence(
    val centralWaypoint: Waypoint,
    val areaRadiusKm: Double,
    val count: Int,
    val waypoints: List<Waypoint>
)
