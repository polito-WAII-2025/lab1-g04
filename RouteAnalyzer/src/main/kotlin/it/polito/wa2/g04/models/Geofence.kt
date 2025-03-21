package it.polito.wa2.g04.models

/**
 * Data class representing a geofence, which defines a geographic boundary.
 *
 * @property centerLat The latitude of the geofence's center point.
 * @property centerLng The longitude of the geofence's center point.
 * @property radiusKm The radius of the geofence in kilometers, defining the area around the center point.
 */
data class Geofence(
    val centerLat: Double,
    val centerLng: Double,
    val radiusKm: Double
)
