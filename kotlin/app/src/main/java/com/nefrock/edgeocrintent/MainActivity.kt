package com.nefrock.edgeocrintent

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nefrock.edgeocrintent.io.request.DictPattern
import com.nefrock.edgeocrintent.io.request.EdgeOCRRequest
import com.nefrock.edgeocrintent.io.request.FuzzyMode
import com.nefrock.edgeocrintent.io.request.MatchingType
import com.nefrock.edgeocrintent.io.request.ScanDefinition
import com.nefrock.edgeocrintent.io.response.Content
import com.nefrock.edgeocrintent.io.response.EdgeOCRResponse
import kotlinx.serialization.json.Json


class MainActivity : AppCompatActivity() {

    private val packageName = "com.nefrock.edgeocrandroid"
    private val className = "com.nefrock.edgeocrandroid.InteractiveActivity"

    /**
     * NOTE: 各環境に合わせて設定してください
     */
    private val allScanScenarioId = "0192ad4f-bb92-72f3-8eb6-99aad3a909c5"
    private val customScanScenarioId = "0192adac-5c36-7881-804b-72bf30f2f5f5"
    private val customScanScenarioScanDefinitionId = "0192adad-13bc-740c-bc1b-b2ffdf1880ad"

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var resultTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val scanAllButton: Button = findViewById(R.id.scan_all_button);
        scanAllButton.setOnClickListener {
            onClickScanAllButton()
        }

        val scanWithCustomDefinition: Button = findViewById(R.id.scan_with_custom_definition);
        scanWithCustomDefinition.setOnClickListener {
            onClickScanWithCustomDefinition()
        }

        resultTextView = findViewById(R.id.result_text_view)
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val responseStr = result.data?.getStringExtra("EdgeOCRAppOutput")
                    val response =
                        Json.decodeFromString(EdgeOCRResponse.serializer(), responseStr!!)
                    processResponse(response)
                }
            }
    }

    @SuppressLint("QueryPermissionsNeeded", "SetTextI18n")
    private fun onClickScanAllButton() {
        val intent = Intent().apply {
            setClassName(
                packageName,
                className,
            )
        }
        if (intent.resolveActivity(this.packageManager) == null) {
            resultTextView.text = "EdgeOCRApp not found"
            return
        }

        val request = EdgeOCRRequest(title = "全読み", scenarioID = allScanScenarioId)
        val requestJson = Json.encodeToString(EdgeOCRRequest.serializer(), request)
        intent.putExtra("EdgeOCRAppInput", requestJson)
        launcher.launch(intent)
    }

    @SuppressLint("QueryPermissionsNeeded", "SetTextI18n")
    private fun onClickScanWithCustomDefinition() {
        val intent = Intent().apply {
            setClassName(
                packageName,
                className,
            )
        }
        if (intent.resolveActivity(this.packageManager) == null) {
            resultTextView.text = "EdgeOCRApp not found"
            return
        }

        val scanDefinition = ScanDefinition(
            dictPattern = DictPattern(
                contents = listOf("1234", "5678"), // ここでマスターデータを定義する
                fuzzyMode = FuzzyMode.Disabled,
                matchingType = MatchingType.Exact
            ), id = customScanScenarioScanDefinitionId, name = "マスターデータ"
        )
        val request = EdgeOCRRequest(
            title = "マスターデータ指定",
            scenarioID = customScanScenarioId,
            scanDefinitions = listOf(scanDefinition)
        )
        val requestJson = Json.encodeToString(EdgeOCRRequest.serializer(), request)
        intent.putExtra("EdgeOCRAppInput", requestJson)
        launcher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun processResponse(response: EdgeOCRResponse) {
        when (val scenarioId = response.scenarioID!!) {
            allScanScenarioId -> processScanAllResponse(response)
            customScanScenarioId -> processScanWithCustomDefinitionResponse(response)
            else -> {
                // handle other scenarios
                resultTextView.text = "Unknown scenario: $scenarioId"
            }
        }
    }

    private fun processScanAllResponse(response: EdgeOCRResponse) {
        val texts = response.allScanResult?.contents?.map { content: Content ->
            content.text
        }
        val builder = StringBuilder()
        texts?.forEach { text ->
            builder.append(text)
            builder.append("\n")
        }
        resultTextView.text = builder.toString()
    }

    private fun processScanWithCustomDefinitionResponse(response: EdgeOCRResponse) {
        response.singleScanResult?.let {
            resultTextView.text = it.content.text
            return
        }
    }
}