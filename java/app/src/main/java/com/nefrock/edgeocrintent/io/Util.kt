package com.nefrock.edgeocrintent.io

import com.nefrock.edgeocrintent.io.request.EdgeOCRRequest
import com.nefrock.edgeocrintent.io.response.EdgeOCRResponse
import kotlinx.serialization.json.Json

class Util {
    companion object {

        fun encodeToString(request: EdgeOCRRequest): String {
            val requestJson = Json.encodeToString(EdgeOCRRequest.serializer(), request)
            return requestJson
        }

        fun decodeFromString(responseStr: String): EdgeOCRResponse {
            return Json.decodeFromString(EdgeOCRResponse.serializer(), responseStr!!)
        }
    }
}