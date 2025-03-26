package it.polito.wa2.g04.models.output.advanced

import it.polito.wa2.g04.models.output.advanced.intersections.IntersectionList
import kotlinx.serialization.Serializable

@Serializable
data class DataReportAdvanced (
    val intersections: IntersectionList,
    val straightLineDistance: StraightLineDistance,
)