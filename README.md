# 自動販売機アプリケーション

Spring Bootを使用したシンプルな自動販売機のバックエンドアプリケーションです。

## 機能

- **コーラ購入**: 100円でコーラを1本購入できます
- **ヘルスチェック**: アプリケーションの状態を確認できます

## 技術スタック

- Java 17
- Spring Boot 3.2.0
- Maven
- Spring Web
- Spring Boot Actuator

## ビルドと実行

### 前提条件
- Java 17以上
- Maven 3.6以上

### ビルド
```bash
mvn clean compile
```

### テスト実行
```bash
mvn test
```

### アプリケーション起動
```bash
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## API エンドポイント

### コーラ購入
```
POST /api/purchase
Content-Type: application/json

{
  "item": "cola",
  "amount": 100
}
```

### ヘルスチェック
```
GET /api/health
```

## プロジェクト構成

```
src/
├── main/
│   ├── java/dev/training/
│   │   ├── VendingMachineApplication.java
│   │   ├── controller/
│   │   │   └── VendingMachineController.java
│   │   └── dto/
│   │       ├── PurchaseRequest.java
│   │       ├── PurchaseResponse.java
│   │       └── HealthCheckResponse.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/dev/training/
        ├── VendingMachineApplicationTests.java
        └── controller/
            └── VendingMachineControllerTest.java
```

## テスト設計・命名規則

このプロジェクトでは、テストコードの可読性と保守性を重視した設計を採用しています。

### テストメソッド命名規則

**パターン**: `機能名_正常系/エラー系_テスト条件と期待結果`

**例**:
```java
@Test
void 購入_正常系_コーラを100円で購入すると200ステータスと成功レスポンスを返す() throws Exception

@Test
void 購入_エラー系_コーラを50円で購入すると400ステータスと金額不足エラーを返す() throws Exception

@Test
void ヘルスチェック_正常系_200ステータスとOKレスポンスを返す() throws Exception
```

### テスト実装の特徴

1. **JSON文字列の直接指定**: DTOオブジェクトを使わず、実際に送信されるJSONを明示
   ```java
   .content("{\"item\":\"cola\",\"amount\":100}")
   ```

2. **日本語テストメソッド名**: 仕様書として機能し、非技術者でも理解可能

3. **正常系・エラー系の明示**: テストケースの分類を明確化

4. **ステータスコードの明記**: HTTP仕様に沿った明確な期待値設定

### メリット

- **可読性**: 何をテストしているかが一目で分かる
- **分類**: 正常系とエラー系が明確に区分される  
- **仕様書機能**: テスト名がそのままAPI仕様として機能
- **保守性**: 機能ごとにテストがグループ化される
- **デバッグ効率**: テスト失敗時の原因特定が迅速

このテスト設計により、コード品質の向上と開発効率の向上を両立しています。