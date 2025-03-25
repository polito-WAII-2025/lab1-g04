package it.polito.wa2.g04.utils

import it.polito.wa2.g04.models.output.base.DataReport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JSONProvider {
    private val json: Json = Json { prettyPrint = true }

    @Override
    fun toString(dataReport: DataReport): String {
        return json.encodeToString(dataReport)
    }
}
