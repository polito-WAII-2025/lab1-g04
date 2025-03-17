package it.polito.wa2.g04.models.output

import kotlinx.serialization.Serializable

@Serializable
data class DataReport(
    val maxDistanceFromStart: MaxDistanceFromStart,
    val mostFrequentedArea: MostFrequentedArea,
    val waypointsOutsideGeofence: WaypointsOutsideGeofence
)
