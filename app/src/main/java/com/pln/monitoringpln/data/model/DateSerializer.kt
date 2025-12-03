package com.pln.monitoringpln.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    private val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX", Locale.US), // Postgres default
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US),    // Milliseconds
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US),        // No millis
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") },
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)            // Fallback
    )

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(formats[0].format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        val stringValue = decoder.decodeString()
        for (format in formats) {
            try {
                return format.parse(stringValue) ?: continue
            } catch (e: Exception) {
                // Continue to next format
            }
        }
        // If all fail, return current date but log it (or print stack trace in debug)
        println("DateSerializer: Failed to parse date: $stringValue")
        return Date()
    }
}
