/*
 * Copyright (c) 2022. Ryan Wong (hello@ryanwong.co.uk)
 */

package uk.ryanwong.gmap2ics.app.models.timeline.activity

data class WaypointPath(
    val distanceMeters: Double,
    val roadSegment: List<RoadSegment>
) {
    companion object {
        fun from(waypointPathDataModel: uk.ryanwong.gmap2ics.data.models.timeline.WaypointPath): WaypointPath {
            return WaypointPath(
                distanceMeters = waypointPathDataModel.distanceMeters ?: 0.0,
                roadSegment = waypointPathDataModel.roadSegment?.mapNotNull { roadSegment ->
                    roadSegment.placeId?.let {
                        RoadSegment(
                            placeId = roadSegment.placeId
                        )
                    }
                } ?: emptyList()
            )
        }
    }
}