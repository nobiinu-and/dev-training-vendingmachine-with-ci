# Spring Boot コントローラー・テスト解説

このドキュメントは、Spring Boot初学者向けに`VendingMachineController`と`VendingMachineControllerTest`のコードを詳しく解説します。

## 目次
1. [VendingMachineController の解説](#vendingmachinecontroller-の解説)
2. [VendingMachineControllerTest の解説](#vendingmachinecontrollertest-の解説)
3. [重要な概念の説明](#重要な概念の説明)

## VendingMachineController の解説

### 基本構造

```java
@RestController
@RequestMapping("/api")
public class VendingMachineController {
    // メソッド定義...
}
```

#### アノテーションの説明

##### @RestController
- **目的**: このクラスがREST APIのコントローラーであることを示す
- **動作**: `@Controller` + `@ResponseBody` の組み合わせ
- **効果**: メソッドの戻り値が自動的にJSONに変換されてHTTPレスポンスとして返される
- **公式ドキュメント**: [Spring Framework Reference - @RestController](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller)

##### @RequestMapping("/api")
- **目的**: クラス全体のベースURLパスを指定
- **動作**: このコントローラーの全エンドポイントが `/api` で始まる
- **効果**: URLの共通部分を一箇所で管理できる
- **公式ドキュメント**: [Spring Framework Reference - @RequestMapping](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-requestmapping)

### 購入エンドポイント

```java
@PostMapping("/purchase")
public ResponseEntity<PurchaseResponse> executeColaProductPurchaseProcessingMethodForVendingMachineSystem(@RequestBody PurchaseRequest a) {
    // 処理内容...
}
```

#### アノテーションの説明

##### @PostMapping("/purchase")
- **目的**: HTTP POSTメソッドでの `/api/purchase` エンドポイントを定義
- **動作**: `@RequestMapping(method = RequestMethod.POST, path = "/purchase")` の短縮形
- **効果**: POST リクエストのみを受け付ける
- **公式ドキュメント**: [Spring Framework Reference - @PostMapping](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-requestmapping-composed)

##### @RequestBody
- **目的**: HTTPリクエストのボディ（JSON）をJavaオブジェクトに変換
- **動作**: Jackson（JSONライブラリ）が自動的にJSONをPOJOに変換
- **効果**: `{"item":"cola","amount":100}` → `PurchaseRequest` オブジェクト
- **公式ドキュメント**: [Spring Framework Reference - @RequestBody](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-requestbody)

#### 戻り値の説明

##### ResponseEntity<PurchaseResponse>
- **目的**: HTTPステータスコードとレスポンスボディを同時に制御
- **利点**: 
  - 成功時: `ResponseEntity.ok(response)` → HTTP 200
  - エラー時: `ResponseEntity.badRequest()` → HTTP 400
- **公式ドキュメント**: [Spring Framework Reference - ResponseEntity](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-responseentity)

### ヘルスチェックエンドポイント

```java
@GetMapping("/health")
public ResponseEntity<HealthCheckResponse> healthCheck() {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    return ResponseEntity.ok(new HealthCheckResponse("OK", timestamp));
}
```

#### アノテーションの説明

##### @GetMapping("/health")
- **目的**: HTTP GETメソッドでの `/api/health` エンドポイントを定義
- **動作**: `@RequestMapping(method = RequestMethod.GET, path = "/health")` の短縮形
- **効果**: GET リクエストのみを受け付ける
- **公式ドキュメント**: [Spring Framework Reference - @GetMapping](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-requestmapping-composed)

## VendingMachineControllerTest の解説

### テストクラスの基本構造

```java
@WebMvcTest(VendingMachineController.class)
class VendingMachineControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    // テストメソッド...
}
```

#### アノテーションの説明

##### @WebMvcTest(VendingMachineController.class)
- **目的**: Spring MVC のコントローラーレイヤーのみをテストする
- **動作**: 
  - 指定したコントローラーのみをSpringコンテキストに読み込む
  - MockMvc を自動設定
  - 他の @Component や @Service は読み込まない（軽量）
- **効果**: 高速なユニットテスト実行
- **公式ドキュメント**: [Spring Boot Reference - @WebMvcTest](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications.spring-mvc-tests)

##### @Autowired
- **目的**: Spring管理のオブジェクトを自動注入
- **動作**: Springコンテナから該当する型のBeanを取得して注入
- **効果**: `MockMvc` インスタンスがテストクラスで使用可能になる
- **公式ドキュメント**: [Spring Framework Reference - @Autowired](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-autowired-annotation)

##### MockMvc
- **目的**: 実際のHTTPサーバーを起動せずにSpring MVCをテスト
- **機能**:
  - HTTPリクエストのシミュレーション
  - レスポンスの検証
  - JSON パスでのレスポンス内容確認
- **公式ドキュメント**: [Spring Framework Reference - MockMvc](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework)

### テストメソッドの構造

```java
@Test
void 購入_正常系_コーラを100円で購入すると200ステータスと成功レスポンスを返す() throws Exception {
    mockMvc.perform(post("/api/purchase")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"item\":\"cola\",\"amount\":100}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("コーラをご購入いただきありがとうございます！"));
}
```

#### アノテーションと要素の説明

##### @Test
- **目的**: このメソッドがJUnitテストメソッドであることを示す
- **動作**: JUnitテストランナーがこのメソッドを実行対象として認識
- **効果**: `mvn test` でこのメソッドが実行される
- **公式ドキュメント**: [JUnit 5 User Guide - @Test](https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations)

##### MockMvc メソッドチェーン

###### .perform()
- **目的**: HTTPリクエストをシミュレーション
- **使用法**: `mockMvc.perform(requestBuilder)`

###### post("/api/purchase")
- **目的**: POST リクエストを `/api/purchase` に送信
- **インポート**: `import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;`

###### .contentType(MediaType.APPLICATION_JSON)
- **目的**: リクエストの Content-Type ヘッダーを設定
- **効果**: `Content-Type: application/json` でリクエスト送信

###### .content()
- **目的**: リクエストボディを設定
- **効果**: JSON文字列がリクエストボディとして送信される

###### .andExpect()
- **目的**: レスポンスの検証条件を設定
- **使用法**: 複数の検証条件をチェーンできる

###### status().isOk()
- **目的**: HTTPステータスコードが 200 OK であることを検証
- **インポート**: `import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;`

###### jsonPath()
- **目的**: JSONレスポンスの特定フィールドを検証
- **記法**: 
  - `"$.success"` → ルートオブジェクトの success フィールド
  - `"$.message"` → ルートオブジェクトの message フィールド
- **インポート**: `import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;`
- **公式ドキュメント**: [JsonPath Documentation](https://github.com/json-path/JsonPath)

## 重要な概念の説明

### REST API の基本概念

#### HTTPメソッド
- **GET**: データの取得（副作用なし）
- **POST**: データの作成・処理（副作用あり）
- **PUT**: データの更新・作成
- **DELETE**: データの削除

#### HTTPステータスコード
- **200 OK**: 正常処理完了
- **400 Bad Request**: クライアント側のリクエストエラー
- **404 Not Found**: リソースが見つからない
- **500 Internal Server Error**: サーバー側エラー

### JSON とオブジェクトマッピング

#### リクエスト例
```json
{
  "item": "cola",
  "amount": 100
}
```

#### レスポンス例
```json
{
  "message": "コーラをご購入いただきありがとうございます！",
  "success": true
}
```

### 学習のポイント

1. **アノテーションの役割**: 各アノテーションが何を自動化しているかを理解する
2. **HTTP の基本**: RESTful API の設計原則を学ぶ
3. **テストの重要性**: 動作を保証するテストの書き方を身につける
4. **JSON 操作**: Web API では JSON が標準的なデータ交換形式
5. **Spring Boot の哲学**: 設定より規約（Convention over Configuration）

### 参考リンク

- [Spring Boot公式ガイド](https://spring.io/guides/gs/spring-boot/)
- [Spring Framework リファレンス](https://docs.spring.io/spring-framework/docs/current/reference/html/)
- [Spring Boot リファレンス](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [JUnit 5 ユーザーガイド](https://junit.org/junit5/docs/current/user-guide/)
- [REST API 設計ガイド](https://restfulapi.net/)

このドキュメントを参考に、実際にコードを動かしながら学習を進めることをお勧めします。
