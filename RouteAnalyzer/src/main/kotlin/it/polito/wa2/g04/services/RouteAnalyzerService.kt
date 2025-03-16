package it.polito.wa2.g04.services

import it.polito.wa2.g04.config.Config
import it.polito.wa2.g04.models.Waypoint
import it.polito.wa2.g04.models.Geofence
import jdk.vm.ci.common.JVMCIError.unimplemented
import kotlin.math.*

class RouteAnalyzerService(private val config: Config) {
    fun calculateMaxDistanceFromStart(waypoints: List<Waypoint>): Double {
        throw unimplemented()
    }

    fun findMostFrequentedArea(waypoints: List<Waypoint>): Waypoint {
        throw unimplemented()
    }

    fun countWaypointsOutsideGeofence(waypoints: List<Waypoint>, geofence: Geofence): Int {
        throw unimplemented()
    }

    private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadiusKm = config.earthRadiusKM
        val lat1Radians = Math.toRadians(lat1)
        val lat2Radians = Math.toRadians(lat2)
        val deltaLatRadians = Math.toRadians(lat2 - lat1)
        val deltaLngRadians = Math.toRadians(lng2 - lng1)

        val a =
            sin(deltaLatRadians / 2) * sin(deltaLatRadians / 2) + cos(lat1) * cos(lat2) * sin(deltaLngRadians / 2) * sin(
                deltaLngRadians / 2
            )
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadiusKm * c

        return distance
    }
}