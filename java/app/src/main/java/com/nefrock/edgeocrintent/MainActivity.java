package com.nefrock.edgeocrintent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nefrock.edgeocrintent.io.Util;
import com.nefrock.edgeocrintent.io.request.DictPattern;
import com.nefrock.edgeocrintent.io.request.EdgeOCRRequest;
import com.nefrock.edgeocrintent.io.request.FuzzyMode;
import com.nefrock.edgeocrintent.io.request.MatchingType;
import com.nefrock.edgeocrintent.io.request.ScanDefinition;
import com.nefrock.edgeocrintent.io.response.Content;
import com.nefrock.edgeocrintent.io.response.EdgeOCRResponse;
import com.nefrock.edgeocrintent.io.response.SingleScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private final String packageName = "com.nefrock.edgeocrandroid";
    private final String className = "com.nefrock.edgeocrandroid.InteractiveActivity";

    /**
     * NOTE: 各環境に合わせて設定してください
     */
    private final String allScanScenarioId = "0192ad4f-bb92-72f3-8eb6-99aad3a909c5";
    private final String customScanScenarioId = "0192adac-5c36-7881-804b-72bf30f2f5f5";

    private ActivityResultLauncher<Intent> launcher;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button scanAllButton = findViewById(R.id.scan_all_button);
        scanAllButton.setOnClickListener(v -> onClickScanAllButton());

        Button scanWithCustomDefinition = findViewById(R.id.scan_with_custom_definition);
        scanWithCustomDefinition.setOnClickListener(v -> onClickScanWithCustomDefinition());

        resultTextView = findViewById(R.id.result_text_view);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                String responseStr = result.getData().getStringExtra("EdgeOCRAppOutput");
                if (responseStr == null) {
                    return;
                }
                EdgeOCRResponse response = Util.Companion.decodeFromString(responseStr);
                processResponse(response);
            }
        });
    }

    @SuppressLint({"QueryPermissionsNeeded", "SetTextI18n"})
    private void onClickScanAllButton() {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        if (intent.resolveActivity(getPackageManager()) == null) {
            resultTextView.setText("EdgeOCR App not found");
            return;
        }

        EdgeOCRRequest request = new EdgeOCRRequest(UUID.randomUUID().toString(), new ArrayList<>(), allScanScenarioId, "全読み");
        String requestJson = Util.Companion.encodeToString(request);
        intent.putExtra("EdgeOCRAppInput", requestJson);
        launcher.launch(intent);
    }

    @SuppressLint({"SetTextI18n", "QueryPermissionsNeeded"})
    private void onClickScanWithCustomDefinition() {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        if (intent.resolveActivity(getPackageManager()) == null) {
            resultTextView.setText("EdgeOCR App not found");
            return;
        }

        String customScanScenarioScanDefinitionId = "0192adad-13bc-740c-bc1b-b2ffdf1880ad";
        ScanDefinition scanDefinition = new ScanDefinition(
                new DictPattern(
                        Arrays.asList("1234", "5678"), // ここでマスターデータを定義する
                        FuzzyMode.Disabled,
                        MatchingType.Exact
                ), customScanScenarioScanDefinitionId, "マスターデータ"
        );


        EdgeOCRRequest request = new EdgeOCRRequest(
                UUID.randomUUID().toString(),
                Collections.singletonList(scanDefinition),
                customScanScenarioId,
                "マスターデータ指定"

        );
        String requestJson = Util.Companion.encodeToString(request);
        intent.putExtra("EdgeOCRAppInput", requestJson);
        launcher.launch(intent);
    }

    @SuppressLint("SetTextI18n")
    private void processResponse(EdgeOCRResponse response) {
        String scenarioId = response.getScenarioID();
        if (scenarioId == null) {
            return;
        }
        if (scenarioId.equals(allScanScenarioId)) {
            processScanAllResponse(response);
        } else if (scenarioId.equals(customScanScenarioId)) {
            processScanWithCustomDefinitionResponse(response);
        } else {
            resultTextView.setText("Unknown scenario: " + scenarioId);
        }
    }

    private void processScanAllResponse(EdgeOCRResponse response) {
        if (response.getAllScanResult() == null) {
            return;
        }
        List<Content> contents = response.getAllScanResult().getContents();
        StringBuilder builder = new StringBuilder();
        for (Content content : contents) {
            String text = content.getText();
            builder.append(text);
            builder.append("\n");
        }
        resultTextView.setText(builder.toString());
    }

    private void processScanWithCustomDefinitionResponse(EdgeOCRResponse response) {
        SingleScanResult result = response.getSingleScanResult();
        if (result == null) {
            return;
        }
        Content content = result.getContent();
        resultTextView.setText(content.getText());
    }
}