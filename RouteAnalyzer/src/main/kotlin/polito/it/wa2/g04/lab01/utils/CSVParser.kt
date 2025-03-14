package polito.it.wa2.g04.lab01.utils

import polito.it.wa2.g04.lab01.models.Waypoint
import java.io.File

class CSVParser {

    fun parseWaypoints(filePath: String): List<Waypoint> {
        val waypoints = mutableListOf<Waypoint>()
        File(filePath).useLines { lines ->
            lines.forEach { line ->
                val values = line.split(";")
                val timestamp = values[0].toDouble()
                val latitude = values[1].toDouble()
                val longitude = values[2].toDouble()
                waypoints.add(Waypoint(timestamp, latitude, longitude))
            }
        }
        return waypoints
    }
}