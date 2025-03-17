package it.polito.wa2.g04

import it.polito.wa2.g04.config.ConfigLoader
import it.polito.wa2.g04.models.DataReport
import it.polito.wa2.g04.models.Geofence
import it.polito.wa2.g04.services.RouteAnalyzerService
import it.polito.wa2.g04.utils.CSVParser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonProvider {
    val json: Json = Json { prettyPrint = true }
}

fun main(args: Array<String>) {

    val waypointsFilePath = args[0]
    val customParametersFilePath = args[1]

    val csvParser = CSVParser()
    val configLoader = ConfigLoader()

    val waypoints = csvParser.parseWaypoints(waypointsFilePath)
    val config = configLoader.loadCustomParameters(customParametersFilePath)

    val routeAnalyzerService = RouteAnalyzerService(config)

    val geofence = Geofence(config.geofenceCenterLatitude, config.geofenceCenterLongitude, config.geofenceRadiusKm)

    val maxDistance = routeAnalyzerService.calculateMaxDistanceFromStart(waypoints)
    val mostFrequented = routeAnalyzerService.findMostFrequentedArea(waypoints)
    val waypointsOutside = routeAnalyzerService.countWaypointsOutsideGeofence(waypoints, geofence)
    val report = DataReport(
        maxDistanceFromStart = maxDistance,
        mostFrequentedArea = mostFrequented,
        waypointsOutsideGeofence = waypointsOutside
    )
    val jsonString = JsonProvider.json.encodeToString(report)
    println(jsonString)
}

