package polito.it.wa2.g04.lab01

import polito.it.wa2.g04.lab01.config.ConfigLoader
import polito.it.wa2.g04.lab01.services.RouteAnalyzerService
import polito.it.wa2.g04.lab01.utils.CSVParser

fun main(args: Array<String>) {

    val waypointsFilePath = args[0]
    val customParametersFilePath = args[1]

    val csvParser = CSVParser()
    val configLoader = ConfigLoader()

    val waypoints = csvParser.parseWaypoints(waypointsFilePath)
    val config = configLoader.loadCustomParameters(customParametersFilePath)

    val routeAnalyzerService = RouteAnalyzerService()

}

