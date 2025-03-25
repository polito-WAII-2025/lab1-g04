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



/**
 * Service that provides various route analysis functions such as calculating maximum distance from the starting point,
 * finding the most frequented area based on waypoints, and counting waypoints outside a defined geofence.
 *
 * @property config The configuration object containing parameters like Earth's radius and other route-specific settings.
 */
class RouteAnalyzerService(private val config: Config) {

    /**
     * Calculates the maximum distance from the starting point (first waypoint) to any other waypoint.
     *
     * @param waypoints A list of waypoints representing points on the route.
     * @return An instance of [MaxDistanceFromStart] containing the farthest waypoint and the calculated maximum distance in kilometers.
     * @throws IllegalArgumentException If the waypoints list is empty.
     */
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

    /**
     * Finds the most frequented area based on waypoints, using H3 indexing for spatial clustering.
     *
     * @param waypoints A list of waypoints representing points on the route.
     * @return An instance of [MostFrequentedArea] containing the central waypoint of the most frequented area,
     *         the area radius, and the frequency of visits to that area.
     * @throws IllegalArgumentException If the waypoints list is empty or no waypoints are provided.
     */
    fun findMostFrequentedArea(waypoints: List<Waypoint>): MostFrequentedArea {
        if (waypoints.isEmpty()) throw IllegalArgumentException("Waypoints list cannot be empty")

        // If the most frequented area radius is not provided, calculate it
        val mostFrequentedAreaRadiusKm =
            config.mostFrequentedAreaRadiusKm ?: calculateMaxDistanceFromStart(waypoints).distanceKm.let {
                if (it < 1) 0.1 else floor(it / 10 * 10) / 10
            }

        val h3 = H3Core.newInstance()
        val resolution = calculateResolution(mostFrequentedAreaRadiusKm, h3)

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
                arrayOf(Coordinate(p1.latitude, p1.longitude), Coordinate(p2.latitude, p2.longitude))
            )
        }


        // Creazione di un R-tree per indicizzare i segmenti
        val rtree = STRtree()
        segments.forEachIndexed { index, segment ->
            rtree.insert(segment.envelopeInternal, index to segment)
        }

        val uniqueIntersections = mutableSetOf<Set<LineString>>() // Usiamo un Set per evitare duplicati

        // Controlliamo le intersezioni evitando segmenti consecutivi
        for ((index, segment) in segments.withIndex()) {
            val candidates = rtree.query(segment.envelopeInternal).filterIsInstance<Pair<Int, LineString>>()

            for ((candIndex, candidate) in candidates) {
                if (candIndex == index || candIndex == index - 1 || candIndex == index + 1) continue // Escludiamo segmenti consecutivi

                if (segment.intersects(candidate)) {
                    val intersectionPair = setOf(segment, candidate) // Set elimina la distinzione tra (A, B) e (B, A)
                    if (uniqueIntersections.add(intersectionPair)) {
                        println("Intersection found:")
                        println("Index 1: $index, Segment 1: ${segment.toText()}")
                        println("Index 2: $candIndex, Segment 2: ${candidate.toText()}")
                        println("-----")
                    }
                }
            }
        }
    }


    /**
     * Counts the number of waypoints that lie outside a specified geofence.
     *
     * @param waypoints A list of waypoints representing points on the route.
     * @param geofence A [Geofence] object specifying the central location and radius of the geofence.
     * @return An instance of [WaypointsOutsideGeofence] containing the central waypoint of the geofence,
     *         the geofence radius, and the list of waypoints found outside the geofence.
     */
    fun countWaypointsOutsideGeofence(waypoints: List<Waypoint>, geofence: Geofence): WaypointsOutsideGeofence {
        val centralWaypoint = Waypoint(0.0, geofence.centerLat, geofence.centerLng)
        val areaRadiusKm = geofence.radiusKm
        val outsideWaypoints = waypoints.filter {
            haversine(it.latitude, it.longitude, centralWaypoint.latitude, centralWaypoint.longitude) > areaRadiusKm
        }

        return WaypointsOutsideGeofence(centralWaypoint, areaRadiusKm, outsideWaypoints.size, outsideWaypoints)
    }

    /**
     * Calculates the Haversine distance between two geographic coordinates.
     *
     * @param lat1 Latitude of the first point.
     * @param lng1 Longitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lng2 Longitude of the second point.
     * @return The distance between the two points in kilometers.
     */
    private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadiusKm = config.earthRadiusKm
        val lat1Radians = Math.toRadians(lat1)
        val lat2Radians = Math.toRadians(lat2)
        val deltaLatRadians = Math.toRadians(lat2 - lat1)
        val deltaLngRadians = Math.toRadians(lng2 - lng1)

        val a = sin(deltaLatRadians / 2) * sin(deltaLatRadians / 2) + cos(lat1Radians) * cos(lat2Radians) * sin(
            deltaLngRadians / 2
        ) * sin(
            deltaLngRadians / 2
        )
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadiusKm * c

        return distance
    }

    /**
     * Calculates the appropriate H3 resolution for the most frequented area radius.
     *
     * @param mostFrequentedAreaRadiusKm The radius of the most frequented area in kilometers.
     * @param h3 An instance of [H3Core] for spatial indexing.
     * @return The best-fitting H3 resolution for the given radius.
     */
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
                } else {
                    break
                }
            }
            bestResolution
        }
    }

    /**
     * Calculates the sum of the distances between every waypoint
     * Please note: this metric has nothing to do with the length of the path to reach every waypoint,
     * it only sums the straight-line distances between each point
     *
     * @param waypoints A list of waypoints representing points on the route.
     * @return Total distance in km between every waypoint
     */
    fun calculateStraightLineDistance(waypoints: List<Waypoint>): Double {
        if (waypoints.isEmpty()) throw IllegalArgumentException("Waypoints is empty")

        val numWaypoints = waypoints.size
        var totalDistance = 0.0

        for (i in 0..numWaypoints - 2) {
            val start = waypoints[i]
            val end = waypoints[i + 1]
            totalDistance += haversine(start.latitude, start.longitude, end.latitude, end.longitude)
        }

        return totalDistance
    }
}
