# 開発ログ: Spring Boot自動販売機テンプレート作成

このドキュメントは、Spring Boot自動販売機アプリケーションテンプレートの開発プロセスとディスカッションを記録したものです。

## プロジェクト概要

**目標**: 以下の要件を満たす軽量なSpring Bootバックエンドアプリケーションテンプレートの作成
- ビルドツール: Maven
- パッケージ名: `dev.training`
- データベース未使用
- Controllerのみの軽量実装（DTOクラス使用）
- 機能:
  - 「100円でコーラが1本買える」
  - 「ヘルスチェック（定型の応答を返すだけでよい）」
  - お釣りや在庫管理は不要

## 開発プロセス

### 1. 初期プロジェクト設定

以下の構成でMavenベースのSpring Bootプロジェクトを作成:
- Spring Boot 3.2.0
- Java 17
- 依存関係: spring-boot-starter-web, spring-boot-starter-actuator, spring-boot-starter-test

### 2. 核となる実装

#### プロジェクト構造
```
src/
├── main/java/dev/training/
│   ├── VendingMachineApplication.java          # メインアプリケーションクラス
│   ├── controller/VendingMachineController.java # RESTコントローラー
│   └── dto/                                    # データ転送オブジェクト
│       ├── PurchaseRequest.java
│       ├── PurchaseResponse.java
│       └── HealthCheckResponse.java
└── test/java/dev/training/
    ├── VendingMachineApplicationTests.java
    └── controller/VendingMachineControllerTest.java
```

#### APIエンドポイント
- `POST /api/purchase` - コーラ購入機能
- `GET /api/health` - ヘルスチェック機能

### 3. コード品質改善

#### IDEリファクタリング体験のための意図的なコード劣化
IDEのリファクタリング体験を提供するため、以下を導入してコードを意図的に読みづらくしました:

**マジックナンバー**:
```java
// 修正前: 定数の使用
private static final int COLA_PRICE = 100;
private static final String COLA_NAME = "コーラ";

// 修正後: マジックナンバー
if (a.getAmount() < 100) {  // 直接数値を使用
    // ...
}
if (!"cola".equalsIgnoreCase(a.getItem()) && !"コーラ".equals(a.getItem())) {
    // 直接文字列を使用
}
```

**不適切な変数名**:
```java
// 修正前: 意味のある名前
@RequestBody PurchaseRequest request
String timestamp = LocalDateTime.now()...

// 修正後: 意味不明な名前
@RequestBody PurchaseRequest a
String b = LocalDateTime.now()...
```

**冗長なメソッド名**:
```java
// 修正前: 簡潔な名前
public ResponseEntity<PurchaseResponse> purchaseCola(...)
public ResponseEntity<HealthCheckResponse> healthCheck()

// 修正後: 過度に冗長な名前
public ResponseEntity<PurchaseResponse> executeColaProductPurchaseProcessingMethodForVendingMachineSystem(...)
public ResponseEntity<HealthCheckResponse> performApplicationHealthStatusCheckAndReturnCurrentSystemStateInformation()
```

### 4. テスト実装の進化

#### 4.1 初期アプローチ: DTOオブジェクトの使用
```java
@Test
void testPurchaseColaSuccessfully() throws Exception {
    PurchaseRequest request = new PurchaseRequest("cola", 100);
    mockMvc.perform(post("/api/purchase")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        // アサーション...
}
```

**問題点**: 
- ObjectMapperへの依存
- 間接的なJSON表現
- より複雑なセットアップ

#### 4.2 JSON文字列の直接使用
```java
@Test
void testPurchaseColaSuccessfully() throws Exception {
    String jsonRequest = "{\"item\":\"cola\",\"amount\":100}";
    mockMvc.perform(post("/api/purchase")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonRequest))
        // アサーション...
}
```

**改善点**:
- JSONの直接的な可視性
- 依存関係の削減
- より明確なテストの意図

#### 4.3 JSON文字列のインライン化
```java
@Test
void testPurchaseColaSuccessfully() throws Exception {
    mockMvc.perform(post("/api/purchase")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"item\":\"cola\",\"amount\":100}"))
        // アサーション...
}
```

**メリット**:
- 最も簡潔な形式
- 即座のJSON可視性
- 余分な変数なし

### 5. テストメソッド命名規則の進化

#### 5.1 英語メソッド名
```java
@Test
void testPurchaseColaSuccessfully() throws Exception
```

#### 5.2 日本語ビジネス言語
```java
@Test
void 百円でコーラが1本買える() throws Exception
```

**問題**: Java識別子は数字で始めることができない

#### 5.3 漢字を使った日本語
```java
@Test
void 百円でコーラが1本買える() throws Exception
```

#### 5.4 API仕様形式
```java
@Test
void POST_api_purchase_コーラを100円で購入すると200ステータスと成功レスポンスを返す() throws Exception
```

**問題**: 冗長すぎて余計な情報が多い

#### 5.5 簡略化されたAPI形式
```java
@Test
void 購入_コーラを100円で購入すると200ステータスと成功レスポンスを返す() throws Exception
```

#### 5.6 最終形式: 正常系・エラー系の分類
```java
@Test
void 購入_正常系_コーラを100円で購入すると200ステータスと成功レスポンスを返す() throws Exception

@Test
void 購入_エラー系_コーラを50円で購入すると400ステータスと金額不足エラーを返す() throws Exception

@Test
void 購入_エラー系_ペプシを100円で購入すると400ステータスと商品未対応エラーを返す() throws Exception

@Test
void ヘルスチェック_正常系_200ステータスとOKレスポンスを返す() throws Exception
```

## 最終的なテスト命名規則

### パターン
`機能名_正常系/エラー系_テスト条件と期待結果`  
(Feature_Normal/Error_TestConditionAndExpectedResult)

### メリット
1. **可読性**: テストの目的が即座に理解できる
2. **分類**: 正常系とエラー系の明確な区別
3. **仕様書機能**: テスト名がAPI仕様として機能
4. **保守性**: 機能ごとにテストがグループ化される
5. **デバッグ効率**: 失敗原因の迅速な特定

### 実装特徴
1. **JSON文字列の直接指定**: DTOオブジェクトなしで明示的なJSON
2. **日本語テストメソッド名**: 仕様として機能し、非技術者でも理解可能
3. **正常系・エラー系の指定**: 明確なテストケース分類
4. **HTTPステータスコードの指定**: HTTP標準に従った明確な期待値

## 主な学習ポイント

### 技術的側面
- **テスト設計の重要性**: 命名における可読性と保守性のバランス
- **段階的改善プロセス**: ステップバイステップのリファクタリングアプローチ
- **日本語識別子の使用**: Javaでの日本語識別子の効果的な活用
- **API仕様の表現**: テストコードによるAPI文書化

### 開発プロセス
- **反復的改善**: ディスカッションを通じた継続的改善
- **コード品質への意識**: 命名が保守性に与える影響の理解
- **IDEリファクタリング練習**: リファクタリング体験の機会創出
- **Documentation as Code**: 生きた文書としてのテスト

## 結論

この開発プロセスは、思慮深いテスト設計と命名規則が、コード品質、保守性、チームコミュニケーションを大幅に改善できることを示しています。シンプルな英語テスト名から記述的な日本語仕様への進化は、テストコードを検証ツールと文書の両方として扱う価値を示しています。

最終的な実装は、複数の目的を果たすクリーンで保守可能なテンプレートを提供します:
- 機能検証
- API仕様文書
- 開発者トレーニング材料
- ベストプラクティスの実証

このアプローチは、即座の開発ニーズと長期的な保守成功の両方の基盤を作成します。
