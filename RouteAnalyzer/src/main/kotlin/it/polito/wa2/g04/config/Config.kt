package it.polito.wa2.g04.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val earthRadiusKM: Double,
    val geofenceCenterLatitude: Double,
    val geofenceCenterLongitude: Double,
    val geofenceRadiusKm: Double,
    val mostFrequentedAreaRadiusKm: Double? = null
);
