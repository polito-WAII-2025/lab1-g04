package it.polito.wa2.g04.services

import kotlin.math.*
import it.polito.wa2.g04.config.Config
import it.polito.wa2.g04.models.output.*
import it.polito.wa2.g04.models.Waypoint
import it.polito.wa2.g04.models.Geofence
import com.uber.h3core.H3Core
import com.uber.h3core.LengthUnit
import org.locationtech.jts.geom.*
import org.locationtech.jts.index.strtree.STRtree



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
        val mostFrequentedAreaRadiusKm =
            config.mostFrequentedAreaRadiusKm ?: calculateMaxDistanceFromStart(waypoints).distanceKm.let {
                if (it < 1) 0.1 else floor(it / 10 * 10) / 10
            }

        val h3 = H3Core.newInstance()
        val resolution = calculateResolution(mostFrequentedAreaRadiusKm,h3)

        // Map to count occurrences of each H3 cell
        val frequencyMap = mutableMapOf<Long, Int>()
        val waypointsInCells = mutableMapOf<Long, MutableList<Waypoint>>()

        for (waypoint in waypoints) {
            val h3Index = h3.latLngToCell(waypoint.latitude, waypoint.longitude, resolution)
            frequencyMap[h3Index] = frequencyMap.getOrDefault(h3Index, 0) + 1
            waypointsInCells.computeIfAbsent(h3Index) { mutableListOf() }.add(waypoint)
        }

        // Find the most frequented H3 cell
        val mostFrequentedH3Index = frequencyMap.maxByOrNull { it.value }?.key
            ?: throw IllegalArgumentException("No waypoints provided")

        // Calculate the centroid of the most frequented H3 cell
        val centroid = h3.cellToLatLng(mostFrequentedH3Index)

        // Find the waypoint closest to the centroid in the most frequented H3 cell
        val waypointsInCell = waypointsInCells[mostFrequentedH3Index] ?: emptyList()
        val centralWaypoint = waypointsInCell.minByOrNull { waypoint ->
            haversine(centroid.lat, centroid.lng, waypoint.latitude, waypoint.longitude)
        } ?: throw IllegalArgumentException("No waypoints provided")

        val frequency = frequencyMap[mostFrequentedH3Index] ?: 0

        return MostFrequentedArea(centralWaypoint, mostFrequentedAreaRadiusKm, frequency)
    }

    fun findIntersections(waypoints: List<Waypoint>) {
        val geometryFactory = GeometryFactory()

        // Creazione segmenti tra waypoint consecutivi
        val segments = waypoints.zipWithNext { p1, p2 ->
            geometryFactory.createLineString(
                arrayOf(Coordinate(p1.longitude, p1.latitude), Coordinate(p2.longitude, p2.latitude))
            )
        }

        // STRtree per indicizzare i segmenti
        val rtree = STRtree()
        segments.forEachIndexed { index, segment ->
            rtree.insert(segment.envelopeInternal, Pair(index, segment))
        }

        // Lista delle intersezioni trovate
        val intersezioni = mutableListOf<Pair<LineString, LineString>>()

        // Controlliamo le intersezioni evitando segmenti consecutivi
        for ((index, segment) in segments.withIndex()) {
            val candidates = rtree.query(segment.envelopeInternal).filterIsInstance<Pair<Int, LineString>>()
            for ((candIndex, candidate) in candidates) {
                if (candIndex <= index + 1 && candIndex >= index - 1) continue // Escludiamo segmenti consecutivi
                if (segment.intersects(candidate)) {
                    intersezioni.add(segment to candidate)
                }
            }
        }

        // Stampiamo le intersezioni trovate
        if (intersezioni.isEmpty()) {
            println("Nessuna intersezione trovata.")
        } else {
            println("Segmenti che si intersecano:")
            for ((s1, s2) in intersezioni) {
                println("${s1.coordinates.toList()} ⟷ ${s2.coordinates.toList()}")
            }
        }
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

    private fun calculateResolution(mostFrequentedAreaRadiusKm: Double, h3: H3Core): Int {


        return if (mostFrequentedAreaRadiusKm < 1 && config.mostFrequentedAreaRadiusKm == null) {
            10
        } else {
            // Calculate the desired edge length of the cell
            var bestResolution = 0
            var closestDiff = Double.MAX_VALUE

            // Check available resolutions (0 - 10)
            for (res in 0..10) {
                // Get the edge length of the cell in km for the current resolution
                val cellEdgeLength = h3.getHexagonEdgeLengthAvg(res, LengthUnit.km)
                val diff = abs(cellEdgeLength - mostFrequentedAreaRadiusKm)
                if (diff < closestDiff) {
                    closestDiff = diff
                    bestResolution = res
                }else{
                    break
                }
            }
            bestResolution
        }
    }
}
