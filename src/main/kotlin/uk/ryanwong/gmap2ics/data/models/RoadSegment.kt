package uk.ryanwong.gmap2ics.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RoadSegment(
    val duration: String? = null,
    val placeId: String? = null,
)