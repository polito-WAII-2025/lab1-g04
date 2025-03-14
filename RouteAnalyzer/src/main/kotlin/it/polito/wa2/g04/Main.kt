package it.polito.wa2.g04

import it.polito.wa2.g04.config.ConfigLoader
import it.polito.wa2.g04.services.RouteAnalyzerService
import it.polito.wa2.g04.utils.CSVParser

fun main(args: Array<String>) {

    val waypointsFilePath = args[0]
    val customParametersFilePath = args[1]

    val csvParser = CSVParser()
    val configLoader = ConfigLoader()

    val waypoints = csvParser.parseWaypoints(waypointsFilePath)
    val config = configLoader.loadCustomParameters(customParametersFilePath)

    val routeAnalyzerService = RouteAnalyzerService()

}

