package uk.ryanwong.gmap2ics.data.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Duration(
    val endTimestamp: String? = null,
    val startTimestamp: String? = null
)