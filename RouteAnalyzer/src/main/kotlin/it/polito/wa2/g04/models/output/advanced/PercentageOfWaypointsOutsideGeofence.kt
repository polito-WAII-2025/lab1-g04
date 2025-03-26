package it.polito.wa2.g04.models.output.advanced

import kotlinx.serialization.Serializable

@Serializable
data class PercentageOfWaypointsOutsideGeofence(
    val waypointsOutsideGeofence: Double,
    val waypointsInsideGeofence: Double,
)
