package it.polito.wa2.g04.models.output.advanced.intersections

import it.polito.wa2.g04.models.Waypoint
import kotlinx.serialization.Serializable

@Serializable
data class Segment (
    val point1: Waypoint,
    val point2: Waypoint,
)