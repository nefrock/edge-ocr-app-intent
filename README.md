# edge-ocr-app-intent

EdgeOCRAppをインテントで呼び出すサンプルです。
Kotlin、Javaのサンプルがそれぞれkotlin、javaディレクトリに入っています。
Android Studioでそれぞれのディレクトリを開いてください。

## 使用方法

Kotlinを例にして説明します。

画面には２つのボタンがあり、1つは「全読モード」のシナリオを呼び出すサンプルで、もう1つは「シングルモード」のシナリオを呼び出すサンプルです。

### 全読モード

まずは設定で全読モードのシナリオを作成してください。
そのシナリオIDを [MainActivity.kt](kotlin/app/src/main/java/com/nefrock/edgeocrintent/MainActivity.kt) に定義されている `allScanScenarioId`  に代入します。

```kotlin
private val allScanScenarioId = "シナリオID"
```

#### EdgeOCRAppの呼び出し

呼び出し部分は [MainActivity.kt](kotlin/app/src/main/java/com/nefrock/edgeocrintent/MainActivity.kt) の下記に定義されています。

Intentに `EdgeOCRAppInput` というキーで、`EdgeOCRRequest` を Json にシリアライズした値をセットしています。

```kotlin  
private fun onClickScanAllButton() {
    ...

    val request = EdgeOCRRequest(title = "全読み", scenarioID = allScanScenarioId)
    val requestJson = Json.encodeToString(EdgeOCRRequest.serializer(), request)
    intent.putExtra("EdgeOCRAppInput", requestJson)
    launcher.launch(intent)
}
```

EdgeOCRRequest の詳細は [ここ]()を参照してください。

#### EdgeOCRAppからの結果を受け取り

受け取り部分も同様に [MainActivity.kt](kotlin/app/src/main/java/com/nefrock/edgeocrintent/MainActivity.kt) に定義されており、具体的には下記に定義されています。

```kotlin
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
```

全読モードの返り値は `EdgeOCRResponse.allScanResult` に定義されています。
この例では allScanResult から、すべてのスキャンされたテキストを取り出して画面に表示しています。

EdgeOCRResponse の詳細は [ここ]()を参照してください。

### シングルモード

次にシングルモードの説明をします。
まずは設定でシングルモードのシナリオを作成してください。

そのシナリオIDを [MainActivity.kt](kotlin/app/src/main/java/com/nefrock/edgeocrintent/MainActivity.kt) に定義されている `customScanScenarioId` に代入します。

```kotlin
private val customScanScenarioId = "シナリオID"
```

EdgeOCRApp では、スキャン定義を動的に呼び出しアプリから変更することもできます。この例では動的に変更する方法を紹介いたします。
EdgeOCRApp からスキャン定義 ID を取得し、下記のように代入してください。

```kotlin
private val customScanScenarioScanDefinitionId = "スキャン定義ID"
```

#### EdgeOCRAppの呼び出し

呼び出し部分は [MainActivity.kt](kotlin/app/src/main/java/com/nefrock/edgeocrintent/MainActivity.kt) の下記に定義されています。


```kotlin  
private fun onClickScanWithCustomDefinition() {
    ...

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
    ...
   
}
```

全読モードとの違いは、 EdgeOCRRequest に `ScanDefinition` を定義している点です。
ScanDefinition の詳細は、[ここ]()を参照してください。

#### EdgeOCRAppからの結果を受け取り

受け取り部分も同様に [MainActivity.kt](kotlin/app/src/main/java/com/nefrock/edgeocrintent/MainActivity.kt) に定義されており、具体的には下記に定義されています。

```kotlin
private fun processScanWithCustomDefinitionResponse(response: EdgeOCRResponse) {
    response.singleScanResult?.let {
        resultTextView.text = it.content.text
        return
    }
}
```

シングルモードの返り値は `EdgeOCRResponse.singleScanResult` に定義されています。詳細は[ここ]()を参照してください。


## Json Schemaからクラスの自動生成(オプション) 
リクエスト・レスポンスのクラスを Json Schema ファイルから自動生成する方法です。
ほとんどのユースケースでこの操作は必要ありません。生成済みのファイルを使用してください。


### Kotlin
リクエスト

```bash
cat schema/input.schema.json | quicktype -s schema -l kotlin --package com.nefrock.edgeocrintent.io.request --framework kotlinx --top-level EdgeOCRRequest > kotlin/app/src/main/java/com/nefrock/edgeocrintent/io/request/EdgeOCRRequest.kt
```

レスポンス

```bash
cat schema/output.schema.json | quicktype -s schema -l kotlin --package com.nefrock.edgeocrintent.io.response --framework kotlinx --top-level EdgeOCRResponse > kotlin/app/src/main/java/com/nefrock/edgeocrintent/io/response/EdgeOCRResponse.kt
```


### Java
リクエスト

```bash
cat schema/input.schema.json | quicktype -s schema -l kotlin --package com.nefrock.edgeocrintent.io.request --framework kotlinx --top-level EdgeOCRRequest > java/app/src/main/java/com/nefrock/edgeocrintent/io/request/EdgeOCRRequest.kt
```

レスポンス

```bash
cat schema/output.schema.json | quicktype -s schema -l kotlin --package com.nefrock.edgeocrintent.io.response  --framework kotlinx --top-level EdgeOCRResponse > java/app/src/main/java/com/nefrock/edgeocrintent/io/response/EdgeOCRResponse.kt
```
