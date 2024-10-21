// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json           = Json { allowStructuredMapKeys = true }
// val edgeOCRRequest = json.parse(EdgeOCRRequest.serializer(), jsonString)

package com.nefrock.edgeocrintent.io.request

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class EdgeOCRRequest (
    @SerialName("requestId")
    val requestID: String? = null,

    val scanDefinitions: List<ScanDefinition>? = null,

    @SerialName("scenarioId")
    val scenarioID: String,

    val title: String
)

@Serializable
data class ScanDefinition (
    val dictPattern: DictPattern,
    val id: String,
    val name: String
)

@Serializable
data class DictPattern (
    val contents: List<String>,
    val fuzzyMode: FuzzyMode,
    val matchingType: MatchingType
)

@Serializable
enum class FuzzyMode(val value: String) {
    @SerialName("DISABLED") Disabled("DISABLED"),
    @SerialName("LITTLE_FUZZY") LittleFuzzy("LITTLE_FUZZY"),
    @SerialName("VERY_FUZZY") VeryFuzzy("VERY_FUZZY");
}

@Serializable
enum class MatchingType(val value: String) {
    @SerialName("BACKWARD") Backward("BACKWARD"),
    @SerialName("EXACT") Exact("EXACT"),
    @SerialName("FORWARD") Forward("FORWARD"),
    @SerialName("PARTIAL") Partial("PARTIAL");
}
