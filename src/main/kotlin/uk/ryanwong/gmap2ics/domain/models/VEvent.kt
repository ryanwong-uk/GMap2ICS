package uk.ryanwong.gmap2ics.domain.models

import java.time.LocalDateTime
import java.time.ZoneId


private val timeStampRegex = """(\d+)-(\d+)-(\d+)T(\d+):(\d+):(\d+)(.\d+)*Z""".toRegex()


data class VEvent(
    val uid: String,
    val dtStamp: String?,
    val organizer: String? = null,
    val dtStart: String,
    val dtEnd: String,
    val dtTimeZone: String,
    val summary: String,
    val location: String,
    val geo: LatLng?,
    val description: String? = null
) {
    data class LatLng(
        val latitude: Double,
        val longitude: Double
    )

    companion object {
        fun from(timelineObject: GMapTimelineObject): VEvent {
            return VEvent(
                uid = timelineObject.id,
                dtStamp = timelineObject.lastEditTimeStamp.googleToICalTimeStamp(),
                dtStart = timelineObject.startTimeStamp?.googleToICalTimeStamp() ?: "",
                dtEnd = timelineObject.endTimeStamp?.googleToICalTimeStamp() ?: "",
                summary = timelineObject.subject,
                geo = LatLng(
                    latitude = timelineObject.eventLatitude,
                    longitude = timelineObject.eventLongitude
                ),
                dtTimeZone = timelineObject.eventTimeZone,
                location = timelineObject.location
            )

        }
    }

    fun export(): String {
        val stringBuilder = StringBuilder()
        println(LocalDateTime.now().atZone(ZoneId.of("UTC")))

        stringBuilder.run {
            append("BEGIN:VEVENT\n")
            append("TRANSP:OPAQUE\n")
            append("DTEND;TZID=$dtTimeZone:$dtEnd\n")
            append("X-APPLE-STRUCTURED-LOCATION;VALUE=URI;X-APPLE-RADIUS=\n")
            append(" 147.4943601668639;X-TITLE=\"$location\":geo:${geo?.latitude},${geo?.longitude}\n")
            append("UID:$uid\n")
            append("DTSTAMP:$dtStamp\n")
            append("LOCATION:${location.replace(oldValue = ",",newValue="\\n")}\n")
//            geo?.let { geo ->
//                val googleMapUrl = "https://maps.google.com/?q=${geo.latitude},${geo.longitude}&ll=${geo.latitude},${geo.longitude}&z=14"
//                    .replace(oldValue = ",",newValue="\\,")
//                append("URL;VALUE=URI:$googleMapUrl\n")
//            }
            description?.let { description ->
                append("DESCRIPTION:$description\n")
            }
            append("STATUS:CONFIRMED\n")
            append("SEQUENCE:1\n")
            append("SUMMARY:$summary\n")
            append("LAST-MODIFIED:20220204T200841Z\n") // TODO: current timestamp
            append("DTSTART;TZID=$dtTimeZone:$dtStart\n")
            append("CREATED:$dtEnd\n")
            append("X-APPLE-TRAVEL-ADVISORY-BEHAVIOR:AUTOMATIC\n")
            append("END:VEVENT\n")
        }

        return stringBuilder.toString()
    }

}

fun String.googleToICalTimeStamp(): String? {
    val matchResult = timeStampRegex.find(this)

    return matchResult?.let { result ->
        val (year, month, day, hour, min, sec, _) = result.destructured
        "$year${month.padStart(2, '0')}${day.padStart(2, '0')}T${hour.padStart(2, '0')}${
            min.padStart(
                2,
                '0'
            )
        }${sec.padStart(2, '0')}"
    }
}