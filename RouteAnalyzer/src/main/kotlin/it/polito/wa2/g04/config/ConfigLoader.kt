package it.polito.wa2.g04.config

import com.charleskorn.kaml.Yaml;
import kotlinx.serialization.decodeFromString
import java.io.File

class ConfigLoader {
    fun loadCustomParameters(filePath: String): Config {
        val ymlContent = File(filePath).readText(Charsets.UTF_8)
        val config = Yaml.default.decodeFromString<Config>(ymlContent)

        return config
    }
}