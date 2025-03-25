package it.polito.wa2.g04.models.output.advanced

import kotlinx.serialization.Serializable

@Serializable
data class IntersectionList (
    val intersections: List<Intersection>
)