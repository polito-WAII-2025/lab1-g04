package it.polito.wa2.g04.models.output

import it.polito.wa2.g04.models.Waypoint
import kotlinx.serialization.Serializable

@Serializable
data class MostFrequentedArea(
    val centralWaypoint: Waypoint,
    val areaRadiusKm: Double,
    val entriesCount: Int
)
