package eu.nk2.springcraft.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.util.HashMap
import java.math.BigDecimal


class NaturalDeserializer : JsonDeserializer<Any> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Any? =
        when {
            json.isJsonNull -> null
            json.isJsonPrimitive -> handlePrimitive(json.asJsonPrimitive)
            json.isJsonArray -> handleArray(json.asJsonArray, context)
            else -> handleObject(json.asJsonObject, context)
        }

    private fun handlePrimitive(json: JsonPrimitive): Any {
        when {
            json.isBoolean -> return json.asBoolean
            json.isString -> return json.asString
            else -> {
                val bigDec = json.asBigDecimal
                // Find out if it is an int type
                try {
                    bigDec.toBigIntegerExact()
                    try {
                        return bigDec.intValueExact()
                    } catch (e: ArithmeticException) {
                    }

                    return bigDec.toLong()
                } catch (e: ArithmeticException) {
                }

                // Just return it as a double
                return bigDec.toDouble()
            }
        }
    }

    private fun handleArray(json: JsonArray, context: JsonDeserializationContext): Any {
        val array = arrayOfNulls<Any>(json.size())
        for (i in array.indices)
            array[i] = context.deserialize(json.get(i), Any::class.java)
        return array
    }

    private fun handleObject(json: JsonObject, context: JsonDeserializationContext): Any {
        val map = HashMap<String, Any>()
        for ((key, value) in json.entrySet())
            map[key] = context.deserialize(value, Any::class.java)
        return map
    }
}
