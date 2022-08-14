/*
 * Copyright (c) 2022. Ryan Wong (hello@ryanwong.co.uk)
 */

package uk.ryanwong.gmap2ics.app.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import uk.ryanwong.gmap2ics.app.ActivityType
import uk.ryanwong.gmap2ics.app.models.timeline.Location
import uk.ryanwong.gmap2ics.app.models.timeline.activity.Activity
import uk.ryanwong.gmap2ics.app.models.timeline.activity.ActivitySegment
import uk.ryanwong.gmap2ics.app.models.timeline.activity.WaypointPath
import uk.ryanwong.gmap2ics.app.models.timeline.placevisit.ChildVisit
import uk.ryanwong.gmap2ics.app.models.timeline.placevisit.PlaceVisit
import uk.ryanwong.gmap2ics.app.utils.timezonemap.MockTimeZoneMap
import java.time.format.DateTimeParseException
import java.time.zone.ZoneRulesException

internal class VEventTest : FreeSpec() {

    /**
     * Test Plan -
     * 1. from() with happy flow - from ActivitySegment, PlaceVisit and ChildVisit
     * 2. from() with missing timezone dtStart, dtEnd, dtTimezone: default to UTC and conversion should be correct
     * 3. export() with happy flow - test data need to include string replacement to make sure being substituted correctly
     * 4. getLocalizedTimeStamp() - test the fixed pattern for happy flow, and negative cases
     */

    /**
     * I am doing a bit different from Greg.
     * While for some strings we can safely replace them with "some-strings", for those we expect some special formatting,
     * like timestamp, or url, I would take a balance by making them meaningless enough but keeping the format.
     */
    private val mockTimeZoneMap: MockTimeZoneMap = MockTimeZoneMap()
    private val mockActivitySegment = ActivitySegment(
        activities = listOf(
            Activity(activityType = ActivityType.WALKING, rawActivityType = "WALKING"),
            Activity(activityType = ActivityType.IN_PASSENGER_VEHICLE, rawActivityType = "IN_PASSENGER_VEHICLE"),
            Activity(activityType = ActivityType.IN_BUS, rawActivityType = "IN_BUS")
        ),
        activityType = ActivityType.FLYING,
        rawActivityType = "FLYING",
        distance = 79,
        durationEndTimestamp = "2011-11-11T11:22:22.222Z",
        durationStartTimestamp = "2011-11-11T11:11:11.111Z",
        endLocation = Location(
            address = null,
            latitudeE7 = 263393300,
            longitudeE7 = 1278500000,
            name = null,
            placeId = null
        ),
        startLocation = Location(
            address = null,
            latitudeE7 = 263383300,
            longitudeE7 = 1278000000,
            name = null,
            placeId = null,
        ),
        waypointPath = WaypointPath(
            distanceMeters = 17.61099772105995,
            roadSegmentPlaceIds = emptyList()
        ),
        lastEditedTimestamp = "2011-11-11T11:22:22.222Z",
        eventTimeZone = mockTimeZoneMap.getOverlappingTimeZone(
            degreesLatitude = 26.3383300,
            degreesLongitude = 127.8000000
        )
    )

    private val mockChildVisit = ChildVisit(
        // meaningless values just to match the format
        durationEndTimestamp = "2011-11-11T11:22:22.222Z",
        durationStartTimestamp = "2011-11-11T11:11:11.111Z",
        lastEditedTimestamp = "2011-11-11T11:22:22.222Z",
        location = Location(
            placeId = "place-id-to-be-kept",
            // meaningless values just to match the format
            latitudeE7 = 263383300,
            longitudeE7 = 1278000000,
            name = "some-name",
            address = "some-address"
        ),
        eventTimeZone = mockTimeZoneMap.getOverlappingTimeZone(
            degreesLatitude = 26.3383300,
            degreesLongitude = 127.8000000
        )
    )

    private val mockPlaceVisit = PlaceVisit(
        // meaningless values just to match the format
        durationEndTimestamp = "2011-11-11T11:22:22.222Z",
        durationStartTimestamp = "2011-11-11T11:11:11.111Z",
        lastEditedTimestamp = "2011-11-11T11:22:22.222Z",
        location = Location(
            placeId = "location-id-to-be-kept",
            // meaningless values just to match the format
            latitudeE7 = 263383300,
            longitudeE7 = 1278000000
        ),
        childVisits = emptyList(),
        eventTimeZone = mockTimeZoneMap.getOverlappingTimeZone(
            degreesLatitude = 26.3383300,
            degreesLongitude = 127.8000000
        )
    )

    init {
//        "from" - {
//            "Should convert ActivitySegment to VEvent correctly" {
//                // 🔴 Given
//                val activitySegment = mockActivitySegment
//
//                // 🟡 When
//                val vEvent = VEvent.from(activitySegment = activitySegment)
//
//                // 🟢 Then
//                vEvent shouldBe VEvent(
//                    uid = "2011-11-11T12:12:12.222Z",
//                    placeId = "some-place-id",
//                    dtStamp = "2011-11-11T12:12:12.222Z",
//                    organizer = null,
//                    dtStart = "20111111T201111",
//                    dtEnd = "20111111T211212",
//                    dtTimeZone = "Asia/Tokyo",
//                    summary = "📍 some-subject",
//                    location = "some-location",
//                    geo = LatLng(latitude = 22.4799999, longitude = 127.7999999),
//                    description = "Place ID:\nsome-place-id\n\nGoogle Maps URL:\nhttps://www.google.com/maps/place/?q=place_id:some-place-id",
//                    url = "https://www.google.com/maps/place/?q=place_id:some-place-id",
//                    lastModified = "2011-11-11T12:12:12.222Z"
//                )
//            }

//            "Should use UTC timezone to convert TimelineItem to VEvent if eventTimeZone is null" {
//                // 🔴 Given
//                val timelineItem = mockTimelineItemNoZoneId
//
//                // 🟡 When
//                val vEvent = VEvent.from(timelineItem = timelineItem)
//
//                // 🟢 Then
//                vEvent shouldBe VEvent(
//                    uid = "2011-11-11T12:12:12.222Z",
//                    placeId = "some-place-id",
//                    dtStamp = "2011-11-11T12:12:12.222Z",
//                    organizer = null,
//                    dtStart = "20111111T111111",
//                    dtEnd = "20111111T121212",
//                    dtTimeZone = "UTC",
//                    summary = "📍 some-subject",
//                    location = "some-location",
//                    geo = LatLng(latitude = 22.4799999, longitude = 127.7999999),
//                    description = "Place ID:\nsome-place-id\n\nGoogle Maps URL:\nhttps://www.google.com/maps/place/?q=place_id:some-place-id",
//                    url = "https://www.google.com/maps/place/?q=place_id:some-place-id",
//                    lastModified = "2011-11-11T12:12:12.222Z"
//                )
//            }
//        }
//
//        "export" - {
//            "Should export correct iCal string" {
//                // 🔴 Given
//                val timelineItem = mockTimelineItem
//                val vEvent = VEvent.from(timelineItem = timelineItem)
//
//                // 🟡 When
//                val iCalString = vEvent.export()
//
//                // 🟢 Then
//                iCalString shouldBe "BEGIN:VEVENT\n" +
//                        "TRANSP:OPAQUE\n" +
//                        "DTSTART;TZID=Asia/Tokyo:20111111T201111\n" +
//                        "DTEND;TZID=Asia/Tokyo:20111111T211212\n" +
//                        "X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-RADIUS=147;\n" +
//                        "X-TITLE=\"some-location\":geo:22.48,127.8\n" +
//                        "UID:2011-11-11T12:12:12.222Z\n" +
//                        "DTSTAMP:2011-11-11T12:12:12.222Z\n" +
//                        "LOCATION:some-location\n" +
//                        "SUMMARY:\uD83D\uDCCD some-subject\n" +
//                        "DESCRIPTION:Place ID:\n" +
//                        "some-place-id\n" +
//                        "\n" +
//                        "Google Maps URL:\n" +
//                        "https://www.google.com/maps/place/?q=place_id:some-place-id\n" +
//                        "URL;VALUE=URI:https://www.google.com/maps/place/?q=place_id:some-place-id\n" +
//                        "STATUS:CONFIRMED\n" +
//                        "SEQUENCE:1\n" +
//                        "LAST-MODIFIED:2011-11-11T12:12:12.222Z\n" +
//                        "CREATED:2011-11-11T12:12:12.222Z\n" +
//                        "X-APPLE-TRAVEL-ADVISORY-BEHAVIOR:AUTOMATIC\n" +
//                        "END:VEVENT\n"
//            }
//        }

        "getLocalizedTimeStamp" - {
            "Should return correct localized Time Stamp for good timestamp and timezoneId" {
                // 🔴 Given
                val timestamp = "2011-11-11T11:22:22.222Z"
                val timezoneId = "Asia/Tokyo"

                // 🟡 When
                val formattedTimeStamp = getLocalizedTimeStamp(timestamp = timestamp, timezoneId = timezoneId)

                // 🟢 Then
                formattedTimeStamp shouldBe "20111111T202222"
            }
            "Should return exception if timezoneId is invalid" {
                // 🔴 Given
                val timestamp = "2011-11-11T11:22:22.222Z"
                val timezoneId = "some-incorrect-timezoneid"

                // 🟡 When
                val exception = shouldThrow<ZoneRulesException> {
                    getLocalizedTimeStamp(
                        timestamp = timestamp,
                        timezoneId = timezoneId
                    )
                }

                // 🟢 Then
                exception.message shouldBe startWith("Unknown time-zone ID: some-incorrect-timezoneid")
            }
            "Should return exception if timestamp is invalid" {
                // 🔴 Given
                val timestamp = "some-invalid-timestamp"
                val timezoneId = "Asia/Tokyo"

                // 🟡 When
                val exception = shouldThrow<DateTimeParseException> {
                    getLocalizedTimeStamp(
                        timestamp = timestamp,
                        timezoneId = timezoneId
                    )
                }

                // 🟢 Then
                exception.message shouldBe startWith("Text 'some-invalid-timestamp' could not be parsed at index 0")
            }
        }
    }
}
