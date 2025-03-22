package it.polito.wa2.g04

import it.polito.wa2.g04.config.ConfigLoader
import it.polito.wa2.g04.models.output.*
import it.polito.wa2.g04.models.Geofence
import it.polito.wa2.g04.services.RouteAnalyzerService
import it.polito.wa2.g04.utils.CSVParser
import it.polito.wa2.g04.utils.JSONProvider

fun main(args: Array<String>) {

    if (args.size != 2) {
        throw IllegalArgumentException("Incorrect number of arguments")
    }

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
    val dataReport = DataReport(
        maxDistanceFromStart = maxDistance,
        mostFrequentedArea = mostFrequented,
        waypointsOutsideGeofence = waypointsOutside
    )

    routeAnalyzerService.findIntersections(waypoints)

    val jsonProvider = JSONProvider()
    val jsonString = jsonProvider.toString(dataReport)
    println(jsonString)
}
