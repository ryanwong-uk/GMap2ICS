package uk.ryanwong.gmap2ics.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaceVisitSegment(
    val placeVisitCandidate: List<PlaceVisitCandidate>? = null,
    val location: Location? = null
)