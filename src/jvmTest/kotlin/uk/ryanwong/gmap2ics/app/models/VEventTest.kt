/*
 * Copyright (c) 2022. Ryan Wong (hello@ryanwong.co.uk)
 */

package uk.ryanwong.gmap2ics.app.models

import com.esri.core.geometry.Polygon
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockActivityEndSegmentPlaceDetails
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockActivityFirstSegmentPlaceDetails
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockActivityLastSegmentPlaceDetails
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockActivitySegment
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockActivityStartSegmentPlaceDetails
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockChildVisit
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockChildVisitPlaceDetails
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockPlaceVisit
import uk.ryanwong.gmap2ics.app.models.VEventTestData.mockPlaceVisitPlaceDetails
import uk.ryanwong.gmap2ics.app.models.timeline.LatLng
import uk.ryanwong.gmap2ics.app.models.timeline.Location
import us.dustinj.timezonemap.TimeZone
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

    init {
        "from" - {
            "ActivitySegment" - {
                "Should convert ActivitySegment with PlaceDetails to VEvent correctly" {
                    // 🔴 Given
                    val activitySegment = mockActivitySegment
                    val shouldShowMiles = false
                    val firstPlaceDetails = mockActivityFirstSegmentPlaceDetails
                    val lastPlaceDetails = mockActivityLastSegmentPlaceDetails
                    val startPlaceDetails = mockActivityStartSegmentPlaceDetails
                    val endPlaceDetails = mockActivityEndSegmentPlaceDetails
                    val eventTimeZone = TimeZone(zoneId = "Asia/Tokyo", region = Polygon())

                    // 🟡 When
                    val vEvent = VEvent.from(
                        activitySegment = activitySegment,
                        shouldShowMiles = shouldShowMiles,
                        firstPlaceDetails = firstPlaceDetails,
                        lastPlaceDetails = lastPlaceDetails,
                        startPlaceDetails = startPlaceDetails,
                        endPlaceDetails = endPlaceDetails,
                        eventTimeZone = eventTimeZone
                    )

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-end-location-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "🚐 7.9km (some-start-segment-name ➡ some-end-segment-name)",
                        location = "some-last-segment-formatted-address",
                        geo = LatLng(latitude = 26.33933, longitude = 127.85),
                        description = "Start Location: some-start-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-start-location-place-id\\n\\nEnd Location: some-end-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-end-location-place-id\\n\\nFirst segment: some-first-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-first-segment-place-id\\n\\nLast segment: some-last-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-last-segment-place-id\\n\\n",
                        url = "https://www.google.com/maps/place/?q=place_id:some-end-location-place-id",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should convert ActivitySegment with null endLocation.placeId to VEvent correctly" {
                    // 🔴 Given
                    val activitySegment = mockActivitySegment.copy(
                        endLocation = Location(
                            address = null,
                            latitudeE7 = 263393300,
                            longitudeE7 = 1278500000,
                            name = null,
                            placeId = null
                        )
                    )
                    val shouldShowMiles = false
                    val firstPlaceDetails = mockActivityFirstSegmentPlaceDetails
                    val lastPlaceDetails = mockActivityLastSegmentPlaceDetails
                    val startPlaceDetails = mockActivityStartSegmentPlaceDetails
                    val endPlaceDetails = mockActivityEndSegmentPlaceDetails
                    val eventTimeZone = TimeZone(zoneId = "Asia/Tokyo", region = Polygon())

                    // 🟡 When
                    val vEvent = VEvent.from(
                        activitySegment = activitySegment,
                        shouldShowMiles = shouldShowMiles,
                        firstPlaceDetails = firstPlaceDetails,
                        lastPlaceDetails = lastPlaceDetails,
                        startPlaceDetails = startPlaceDetails,
                        endPlaceDetails = endPlaceDetails,
                        eventTimeZone = eventTimeZone
                    )

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = null,
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "🚐 7.9km (some-start-segment-name ➡ some-end-segment-name)",
                        location = "some-last-segment-formatted-address",
                        geo = LatLng(latitude = 26.33933, longitude = 127.85),
                        description = "Start Location: some-start-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-start-location-place-id\\n\\nEnd Location: some-end-segment-formatted-address\\n\\n\\nFirst segment: some-first-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-first-segment-place-id\\n\\nLast segment: some-last-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-last-segment-place-id\\n\\n",
                        url = "https://maps.google.com?q=26.33933,127.85",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should use UTC timezone to represent time in VEvent if eventTimeZone is null" {
                    // 🔴 Given
                    val activitySegment = mockActivitySegment
                    val shouldShowMiles = false
                    val firstPlaceDetails = mockActivityFirstSegmentPlaceDetails
                    val lastPlaceDetails = mockActivityLastSegmentPlaceDetails
                    val startPlaceDetails = mockActivityStartSegmentPlaceDetails
                    val endPlaceDetails = mockActivityEndSegmentPlaceDetails
                    val eventTimeZone = null

                    // 🟡 When
                    val vEvent = VEvent.from(
                        activitySegment = activitySegment,
                        shouldShowMiles = shouldShowMiles,
                        firstPlaceDetails = firstPlaceDetails,
                        lastPlaceDetails = lastPlaceDetails,
                        startPlaceDetails = startPlaceDetails,
                        endPlaceDetails = endPlaceDetails,
                        eventTimeZone = eventTimeZone
                    )

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-end-location-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T111111",
                        dtEnd = "20111111T112222",
                        dtTimeZone = "UTC",
                        summary = "🚐 7.9km (some-start-segment-name ➡ some-end-segment-name)",
                        location = "some-last-segment-formatted-address",
                        geo = LatLng(latitude = 26.33933, longitude = 127.85),
                        description = "Start Location: some-start-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-start-location-place-id\\n\\nEnd Location: some-end-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-end-location-place-id\\n\\nFirst segment: some-first-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-first-segment-place-id\\n\\nLast segment: some-last-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-last-segment-place-id\\n\\n",
                        url = "https://www.google.com/maps/place/?q=place_id:some-end-location-place-id",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should convert kilometres to miles in VEvent if shouldShowMiles is true" {
                    // 🔴 Given
                    val activitySegment = mockActivitySegment
                    val shouldShowMiles = true
                    val firstPlaceDetails = mockActivityFirstSegmentPlaceDetails
                    val lastPlaceDetails = mockActivityLastSegmentPlaceDetails
                    val startPlaceDetails = mockActivityStartSegmentPlaceDetails
                    val endPlaceDetails = mockActivityEndSegmentPlaceDetails
                    val eventTimeZone = TimeZone(zoneId = "Asia/Tokyo", region = Polygon())

                    // 🟡 When
                    val vEvent = VEvent.from(
                        activitySegment = activitySegment,
                        shouldShowMiles = shouldShowMiles,
                        firstPlaceDetails = firstPlaceDetails,
                        lastPlaceDetails = lastPlaceDetails,
                        startPlaceDetails = startPlaceDetails,
                        endPlaceDetails = endPlaceDetails,
                        eventTimeZone = eventTimeZone
                    )

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-end-location-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "🚐 4.9mi (some-start-segment-name ➡ some-end-segment-name)",
                        location = "some-last-segment-formatted-address",
                        geo = LatLng(latitude = 26.33933, longitude = 127.85),
                        description = "Start Location: some-start-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-start-location-place-id\\n\\nEnd Location: some-end-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-end-location-place-id\\n\\nFirst segment: some-first-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-first-segment-place-id\\n\\nLast segment: some-last-segment-formatted-address\\nhttps://www.google.com/maps/place/?q=place_id:some-last-segment-place-id\\n\\n",
                        url = "https://www.google.com/maps/place/?q=place_id:some-end-location-place-id",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should convert ActivitySegment without PlaceDetails to VEvent correctly" {
                    // 🔴 Given
                    val activitySegment = mockActivitySegment
                    val shouldShowMiles = false
                    val firstPlaceDetails = null
                    val lastPlaceDetails = null
                    val startPlaceDetails = null
                    val endPlaceDetails = null
                    val eventTimeZone = TimeZone(zoneId = "Asia/Tokyo", region = Polygon())

                    // 🟡 When
                    val vEvent = VEvent.from(
                        activitySegment = activitySegment,
                        shouldShowMiles = shouldShowMiles,
                        firstPlaceDetails = firstPlaceDetails,
                        lastPlaceDetails = lastPlaceDetails,
                        startPlaceDetails = startPlaceDetails,
                        endPlaceDetails = endPlaceDetails,
                        eventTimeZone = eventTimeZone
                    )

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-end-location-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "🚐 7.9km ",
                        location = "26.33933,127.85",
                        geo = LatLng(latitude = 26.33933, longitude = 127.85),
                        description = "Start Location: 26.33833,127.8\\nhttps://maps.google.com?q=26.33833,127.8\\n\\nEnd Location: 26.33933,127.85\\nhttps://maps.google.com?q=26.33933,127.85\\n\\n",
                        url = "https://www.google.com/maps/place/?q=place_id:some-end-location-place-id",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }
            }

            "PlaceVisit" - {
                "Should convert PlaceVisit with PlaceDetails to VEvent correctly" {
                    // 🔴 Given
                    val placeVisit = mockPlaceVisit
                    val placeDetails = mockPlaceVisitPlaceDetails

                    // 🟡 When
                    val vEvent = VEvent.from(placeVisit = placeVisit, placeDetails = placeDetails)

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-place-visit-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "🏞 some-place-details-name",
                        location = "some-place-details-formatted-address",
                        geo = LatLng(latitude = 26.33833, longitude = 127.8),
                        description = "Place ID:\\nsome-place-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://maps.google.com/?cid=1021876599690425051",
                        url = "https://maps.google.com/?cid=1021876599690425051",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should use UTC timezone to represent time in VEvent if eventTimeZone is null" {
                    // 🔴 Given
                    val placeVisit = mockPlaceVisit.copy(
                        eventTimeZone = null
                    )
                    val placeDetails = mockPlaceVisitPlaceDetails

                    // 🟡 When
                    val vEvent = VEvent.from(placeVisit = placeVisit, placeDetails = placeDetails)

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-place-visit-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T111111",
                        dtEnd = "20111111T112222",
                        dtTimeZone = "UTC",
                        summary = "🏞 some-place-details-name",
                        location = "some-place-details-formatted-address",
                        geo = LatLng(latitude = 26.33833, longitude = 127.8),
                        description = "Place ID:\\nsome-place-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://maps.google.com/?cid=1021876599690425051",
                        url = "https://maps.google.com/?cid=1021876599690425051",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should convert PlaceVisit without PlaceDetails to VEvent correctly" {
                    // 🔴 Given
                    val placeVisit = mockPlaceVisit
                    val placeDetails = null

                    // 🟡 When
                    val vEvent = VEvent.from(placeVisit = placeVisit, placeDetails = placeDetails)

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-place-visit-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "📍 some-name",
                        location = "some-address",
                        geo = LatLng(latitude = 26.33833, longitude = 127.8),
                        description = "Place ID:\\nsome-place-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://www.google.com/maps/place/?q=place_id:some-place-visit-place-id",
                        url = "https://www.google.com/maps/place/?q=place_id:some-place-visit-place-id",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }
            }

            "ChildVisit" - {
                "Should convert ChildVisit with PlaceDetails to VEvent correctly" {
                    // 🔴 Given
                    val childVisit = mockChildVisit
                    val placeDetails = mockChildVisitPlaceDetails

                    // 🟡 When
                    val vEvent = VEvent.from(childVisit = childVisit, placeDetails = placeDetails)

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-child-visit-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "🏞 some-place-details-name",
                        location = "some-place-details-formatted-address",
                        geo = LatLng(latitude = 26.33833, longitude = 127.8),
                        description = "Place ID:\\nsome-child-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://maps.google.com/?cid=1021876599690425051",
                        url = "https://maps.google.com/?cid=1021876599690425051",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should use UTC timezone to represent time in VEvent if eventTimeZone is null" {
                    // 🔴 Given
                    val childVisit = mockChildVisit.copy(
                        eventTimeZone = null
                    )
                    val placeDetails = mockChildVisitPlaceDetails

                    // 🟡 When
                    val vEvent = VEvent.from(childVisit = childVisit, placeDetails = placeDetails)

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-child-visit-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T111111",
                        dtEnd = "20111111T112222",
                        dtTimeZone = "UTC",
                        summary = "🏞 some-place-details-name",
                        location = "some-place-details-formatted-address",
                        geo = LatLng(latitude = 26.33833, longitude = 127.8),
                        description = "Place ID:\\nsome-child-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://maps.google.com/?cid=1021876599690425051",
                        url = "https://maps.google.com/?cid=1021876599690425051",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }

                "Should convert ChildVisit without PlaceDetails to VEvent correctly" {
                    // 🔴 Given
                    val childVisit = mockChildVisit
                    val placeDetails = null

                    // 🟡 When
                    val vEvent = VEvent.from(childVisit = childVisit, placeDetails = placeDetails)

                    // 🟢 Then
                    vEvent shouldBe VEvent(
                        uid = "2011-11-11T11:22:22.222Z",
                        placeId = "some-child-visit-place-id",
                        dtStamp = "2011-11-11T11:22:22.222Z",
                        organizer = null,
                        dtStart = "20111111T201111",
                        dtEnd = "20111111T202222",
                        dtTimeZone = "Asia/Tokyo",
                        summary = "📍 some-name",
                        location = "some-address",
                        geo = LatLng(latitude = 26.33833, longitude = 127.8),
                        description = "Place ID:\\nsome-child-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://www.google.com/maps/place/?q=place_id:some-child-visit-place-id",
                        url = "https://www.google.com/maps/place/?q=place_id:some-child-visit-place-id",
                        lastModified = "2011-11-11T11:22:22.222Z"
                    )
                }
            }
        }

        "export" - {
            "Should export correct iCal string" {
                // 🔴 Given
                val vEvent = VEvent(
                    uid = "2011-11-11T11:22:22.222Z",
                    placeId = "some-child-visit-place-id",
                    dtStamp = "2011-11-11T11:22:22.222Z",
                    organizer = null,
                    dtStart = "20111111T201111",
                    dtEnd = "20111111T202222",
                    dtTimeZone = "Asia/Tokyo",
                    summary = "🏞 some-place-details-name",
                    location = "some-place-details-formatted-address",
                    geo = LatLng(latitude = 26.33833, longitude = 127.8),
                    description = "Place ID:\\nsome-child-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://maps.google.com/?cid=1021876599690425051",
                    url = "https://maps.google.com/?cid=1021876599690425051",
                    lastModified = "2011-11-11T11:22:22.222Z"
                )

                // 🟡 When
                val iCalString = vEvent.export()

                // 🟢 Then
                iCalString shouldBe "BEGIN:VEVENT\n" +
                        "TRANSP:OPAQUE\n" +
                        "DTSTART;TZID=Asia/Tokyo:20111111T201111\n" +
                        "DTEND;TZID=Asia/Tokyo:20111111T202222\n" +
                        "X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-RADIUS=147;\n" +
                        "X-TITLE=\"some-place-details-formatted-address\":geo:26.33833,127.8\n" +
                        "UID:2011-11-11T11:22:22.222Z\n" +
                        "DTSTAMP:2011-11-11T11:22:22.222Z\n" +
                        "LOCATION:some-place-details-formatted-address\n" +
                        "SUMMARY:\uD83C\uDFDE some-place-details-name\n" +
                        "DESCRIPTION:Place ID:\\nsome-child-visit-place-id\\n\\nGoogle Maps URL:\\nhttps://maps.google.com/?cid=1021876599690425051\n" +
                        "URL;VALUE=URI:https://maps.google.com/?cid=1021876599690425051\n" +
                        "STATUS:CONFIRMED\n" +
                        "SEQUENCE:1\n" +
                        "LAST-MODIFIED:2011-11-11T11:22:22.222Z\n" +
                        "CREATED:2011-11-11T11:22:22.222Z\n" +
                        "X-APPLE-TRAVEL-ADVISORY-BEHAVIOR:AUTOMATIC\n" +
                        "END:VEVENT\n"
            }
        }

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
