package it.polito.wa2.g04.models.output.advanced

import kotlinx.serialization.Serializable

@Serializable
class DataReportAdvanced (
    val intersections: IntersectionList,
)