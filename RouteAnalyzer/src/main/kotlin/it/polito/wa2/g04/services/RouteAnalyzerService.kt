package it.polito.wa2.g04.services

import kotlin.math.*
import it.polito.wa2.g04.config.Config
import it.polito.wa2.g04.models.Waypoint
import it.polito.wa2.g04.models.Geofence

class RouteAnalyzerService(private val config: Config) {
    fun calculateMaxDistanceFromStart(waypoints: List<Waypoint>): Double {
        return 1.0
    }

    fun findMostFrequentedArea(waypoints: List<Waypoint>): Waypoint {
        return Waypoint(1.0, 1.0, 1.0)
    }

    fun countWaypointsOutsideGeofence(waypoints: List<Waypoint>, geofence: Geofence): List<Waypoint> {
        return waypoints.filter {
            haversine(it.lat, it.lng, geofence.centerLat, geofence.centerLng) > geofence.radiusKm
        }
    }

    private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadiusKm = config.earthRadiusKM
        val lat1Radians = Math.toRadians(lat1)
        val lat2Radians = Math.toRadians(lat2)
        val deltaLatRadians = Math.toRadians(lat2 - lat1)
        val deltaLngRadians = Math.toRadians(lng2 - lng1)

        val a =
            sin(deltaLatRadians / 2) * sin(deltaLatRadians / 2) + cos(lat1Radians) * cos(lat2Radians) * sin(
                deltaLngRadians / 2
            ) * sin(
                deltaLngRadians / 2
            )
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadiusKm * c

        return distance
    }
}
