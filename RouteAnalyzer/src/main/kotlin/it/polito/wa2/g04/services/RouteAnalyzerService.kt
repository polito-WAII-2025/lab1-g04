package it.polito.wa2.g04.services

import kotlin.math.*
import it.polito.wa2.g04.config.Config
import it.polito.wa2.g04.models.output.*
import it.polito.wa2.g04.models.Waypoint
import it.polito.wa2.g04.models.Geofence
import com.uber.h3core.H3Core

class RouteAnalyzerService(private val config: Config) {
    fun calculateMaxDistanceFromStart(waypoints: List<Waypoint>): MaxDistanceFromStart {
        if (waypoints.isEmpty()) throw IllegalArgumentException("Waypoints list cannot be empty")

        val origin = waypoints.first()
        var maxDistance = 0.0
        var farthestWaypoint = origin

        for (waypoint in waypoints) {
            val distance = haversine(origin.latitude, origin.longitude, waypoint.latitude, waypoint.longitude)
            if (distance > maxDistance) {
                maxDistance = distance
                farthestWaypoint = waypoint
            }
        }

        return MaxDistanceFromStart(farthestWaypoint, maxDistance)
    }

    fun findMostFrequentedArea(waypoints: List<Waypoint>): MostFrequentedArea {
        if (waypoints.isEmpty()) throw IllegalArgumentException("Waypoints list cannot be empty")

        // If the most frequented area radius is not provided, calculate it
        val mostFrequentedAreaRadiusKm = config.mostFrequentedAreaRadiusKm ?: calculateMaxDistanceFromStart(waypoints).distanceKm

        val h3 = H3Core.newInstance()
        val resolution = calculateResolution(mostFrequentedAreaRadiusKm)

        // Map to count occurrences of each H3 cell
        val frequencyMap = mutableMapOf<Long, Int>()
        val waypointsInCells = mutableMapOf<Long, MutableList<Waypoint>>()

        for (waypoint in waypoints) {
            val h3Index = h3.geoToH3(waypoint.latitude, waypoint.longitude, resolution)
            frequencyMap[h3Index] = frequencyMap.getOrDefault(h3Index, 0) + 1
            waypointsInCells.computeIfAbsent(h3Index) { mutableListOf() }.add(waypoint)
        }

        // Find the most frequented H3 cell
        val mostFrequentedH3Index = frequencyMap.maxByOrNull { it.value }?.key
            ?: throw IllegalArgumentException("No waypoints provided")

        // Calculate the centroid of the most frequented H3 cell
        val centroid = h3.h3ToGeo(mostFrequentedH3Index)

        // Find the waypoint closest to the centroid in the most frequented H3 cell
        val waypointsInCell = waypointsInCells[mostFrequentedH3Index] ?: emptyList()
        val centralWaypoint = waypointsInCell.minByOrNull { waypoint ->
            haversine(centroid.lat, centroid.lng, waypoint.latitude, waypoint.longitude)
        } ?: throw IllegalArgumentException("No waypoints provided")

        val frequency = frequencyMap[mostFrequentedH3Index] ?: 0

        return MostFrequentedArea(centralWaypoint, mostFrequentedAreaRadiusKm, frequency)
    }


    fun countWaypointsOutsideGeofence(waypoints: List<Waypoint>, geofence: Geofence): WaypointsOutsideGeofence {
        val centralWaypoint = Waypoint(0.0, geofence.centerLat, geofence.centerLng)
        val areaRadiusKm = geofence.radiusKm
        val outsideWaypoints = waypoints.filter {
            haversine(it.latitude, it.longitude, centralWaypoint.latitude, centralWaypoint.longitude) > areaRadiusKm
        }

        return WaypointsOutsideGeofence(centralWaypoint, areaRadiusKm, outsideWaypoints.size, outsideWaypoints)
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

    private fun calculateResolution(mostFrequentedAreaRadiusKm: Double): Int {


        return if (mostFrequentedAreaRadiusKm < 1) {
            15
        } else {
            val radius = mostFrequentedAreaRadiusKm / 10
            when {
                radius < 1 -> 12 // 1 kilometer
                radius < 2 -> 10 // 2 kilometers
                radius < 5 -> 9 // 5 kilometers
                else -> 7
            }
        }
    }
}
