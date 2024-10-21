// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json            = Json { allowStructuredMapKeys = true }
// val edgeOCRResponse = json.parse(EdgeOCRResponse.serializer(), jsonString)

package com.nefrock.edgeocrintent.io.response

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class EdgeOCRResponse (
    val allScanResult: AllScanResult? = null,
    val continuousScanResult: ContinuousScanResult? = null,
    val formScanResult: FormScanResult? = null,
    val images: List<Image>? = null,
    val multiScanResult: MultiScanResult? = null,

    @SerialName("requestId")
    val requestID: String? = null,

    val result: Result,
    val scanModeType: ScanModeType,

    @SerialName("scenarioId")
    val scenarioID: String? = null,

    val singleScanResult: SingleScanResult? = null,
    val version: Version
)

@Serializable
data class AllScanResult (
    val contents: List<Content>
)

@Serializable
data class Content (
    val barcodePattern: BarcodePattern? = null,
    val bbox: BBox,
    val confidence: Double,

    @SerialName("fullImageId")
    val fullImageID: Long? = null,

    @SerialName("scanDefinitionId")
    val scanDefinitionID: String,

    val scanDefinitionName: String,
    val scannedAt: String,

    @SerialName("targetImageId")
    val targetImageID: Long? = null,

    val text: String,
    val type: Type
)

@Serializable
enum class BarcodePattern(val value: String) {
    @SerialName("AZTEC") Aztec("AZTEC"),
    @SerialName("CODABAR") Codabar("CODABAR"),
    @SerialName("CODE128") Code128("CODE128"),
    @SerialName("CODE39") Code39("CODE39"),
    @SerialName("CODE93") Code93("CODE93"),
    @SerialName("DATAMATRIX") Datamatrix("DATAMATRIX"),
    @SerialName("DXFILMEDGE") Dxfilmedge("DXFILMEDGE"),
    @SerialName("EAN") Ean("EAN"),
    @SerialName("ITF") Itf("ITF"),
    @SerialName("JAN") Jan("JAN"),
    @SerialName("MAXICODE") Maxicode("MAXICODE"),
    @SerialName("MICROQR") Microqr("MICROQR"),
    @SerialName("PDF417") Pdf417("PDF417"),
    @SerialName("QR") Qr("QR"),
    @SerialName("UPC") Upc("UPC");
}

@Serializable
data class BBox (
    val height: Double,
    val width: Double,
    val x: Double,
    val y: Double
)

@Serializable
enum class Type(val value: String) {
    @SerialName("BARCODE") Barcode("BARCODE"),
    @SerialName("TEXT") Text("TEXT");
}

@Serializable
data class ContinuousScanResult (
    val contentsArr: List<List<List<Content>>>
)

@Serializable
data class FormScanResult (
    val contents: List<Content>
)

@Serializable
data class Image (
    @SerialName("imageId")
    val imageID: Long,

    val uri: String
)

@Serializable
data class MultiScanResult (
    val contents: List<Content>
)

@Serializable
enum class Result(val value: String) {
    @SerialName("ERROR") Error("ERROR"),
    @SerialName("SUCCESS") Success("SUCCESS");
}

@Serializable
enum class ScanModeType(val value: String) {
    @SerialName("ALL") All("ALL"),
    @SerialName("CONTINUOUS") Continuous("CONTINUOUS"),
    @SerialName("FORM") Form("FORM"),
    @SerialName("MULTI") Multi("MULTI"),
    @SerialName("SINGLE") Single("SINGLE");
}

@Serializable
data class SingleScanResult (
    val content: Content
)

@Serializable
enum class Version(val value: String) {
    @SerialName("1-0-0") The100("1-0-0");
}
