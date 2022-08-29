/*
 * Copyright (c) 2022. Ryan Wong (hello@ryanwong.co.uk)
 */

package uk.ryanwong.gmap2ics.data.repository.mocks

import uk.ryanwong.gmap2ics.app.models.timeline.Timeline
import uk.ryanwong.gmap2ics.data.repository.TimelineRepository

class MockTimelineRepository : TimelineRepository {
    var getTimeLineResponse: Result<Timeline>? = null
    override suspend fun getTimeLine(filePath: String): Result<Timeline> {
        return getTimeLineResponse ?: Result.failure(Exception("mock response unavailable"))
    }
}
