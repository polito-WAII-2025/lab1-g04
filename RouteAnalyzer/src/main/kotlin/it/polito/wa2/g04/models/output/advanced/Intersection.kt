package it.polito.wa2.g04.models.output.advanced

import it.polito.wa2.g04.models.Waypoint
import kotlinx.serialization.Serializable

@Serializable
data class Intersection (
    val intersectionPoint: Waypoint,
    val segment1: Segment,
    val segment2: Segment
)