package it.polito.wa2.g04.models.output.advanced

import kotlinx.serialization.Serializable

/**
 * Data class representing the sum of the straight-line distances between each waypoint
 *
 * @property distanceKm The straight-line distance in km
 */
@Serializable
data class StraightLineDistance (
    val distanceKm: Double,
)