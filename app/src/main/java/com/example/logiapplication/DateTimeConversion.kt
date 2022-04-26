package com.example.logiapplication

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import java.io.IOException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeConversions {

    val dateFormat: SimpleDateFormat = SimpleDateFormat(
        "yyyy-MM-dd"
    )
    object Deserializer : JsonDeserializer<Date>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Date {
            val str: String = p.text.trim()
            try {
                return dateFormat.parse(str)
            } catch (e: ParseException) {
                // Handle exception here
            }
            return ctxt.parseDate(str)
        }
    }
}