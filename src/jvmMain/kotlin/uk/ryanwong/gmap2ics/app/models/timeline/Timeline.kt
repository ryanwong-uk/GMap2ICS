/*
 * Copyright (c) 2022. Ryan Wong (hello@ryanwong.co.uk)
 */

package uk.ryanwong.gmap2ics.app.models.timeline

import uk.ryanwong.gmap2ics.app.models.timeline.activity.ActivitySegment
import uk.ryanwong.gmap2ics.app.models.timeline.placevisit.PlaceVisit
import uk.ryanwong.gmap2ics.data.models.timeline.TimelineObjects
import uk.ryanwong.gmap2ics.app.utils.timezonemap.TimeZoneMapWrapper

data class Timeline(
    val timelineEntries: List<TimelineEntry>
) {
    companion object {
        fun from(
            timelineObjects: TimelineObjects, timeZoneMap: TimeZoneMapWrapper
        ): Timeline {
            return Timeline(
                timelineEntries = timelineObjects.timelineObjects?.map { timelineObject ->
                    TimelineEntry(
                        activitySegment = timelineObject.activitySegment?.let { activitySegment ->
                            ActivitySegment.from(
                                activitySegmentDataModel = activitySegment,
                                timeZoneMap = timeZoneMap
                            )
                        },
                        placeVisit = timelineObject.placeVisit?.let {
                            PlaceVisit.from(placeVisitDataModel = it, timeZoneMap = timeZoneMap)
                        }
                    )
                } ?: emptyList()
            )
        }
    }
}
