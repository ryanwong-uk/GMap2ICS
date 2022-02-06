package uk.ryanwong.gmap2ics.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ParkingEvent(
    val location: Location? = null,
    val locationSource: String? = null,
    val method: String? = null,
    val timestamp: String? = null
)