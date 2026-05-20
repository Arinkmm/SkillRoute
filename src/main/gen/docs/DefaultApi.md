# DefaultApi

All URIs are relative to *http://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**checkAccountPasswordField**](DefaultApi.md#checkAccountPasswordField) | **POST** /account/password/check-field | Проверка полей смены пароля |
| [**checkRegistrationField**](DefaultApi.md#checkRegistrationField) | **POST** /register/check-field | Проверка полей регистрации |
| [**checkResetPasswordField**](DefaultApi.md#checkResetPasswordField) | **POST** /password/reset/check-field | Проверка полей сброса пароля |
| [**getChatMessages**](DefaultApi.md#getChatMessages) | **GET** /chat/{id}/messages | Получение сообщений чата |
| [**searchStudentSkills**](DefaultApi.md#searchStudentSkills) | **GET** /student/skills/search | Поиск навыков по подстроке |
| [**sendChatMessage**](DefaultApi.md#sendChatMessage) | **POST** /chat/{id}/send | Отправка сообщения |


<a id="checkAccountPasswordField"></a>
# **checkAccountPasswordField**
> ValidationResponse checkAccountPasswordField(editPasswordRequest)

Проверка полей смены пароля

Валидирует форму смены пароля текущего пользователя без сохранения нового пароля.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    EditPasswordRequest editPasswordRequest = new EditPasswordRequest(); // EditPasswordRequest | 
    try {
      ValidationResponse result = apiInstance.checkAccountPasswordField(editPasswordRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#checkAccountPasswordField");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **editPasswordRequest** | [**EditPasswordRequest**](EditPasswordRequest.md)|  | |

### Return type

[**ValidationResponse**](ValidationResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Данные валидны |  -  |
| **400** | Ошибка бизнес-валидации смены пароля |  -  |
| **401** | Пользователь не авторизован |  -  |
| **500** | Внутренняя ошибка сервера |  -  |

<a id="checkRegistrationField"></a>
# **checkRegistrationField**
> ValidationResponse checkRegistrationField(registrationRequest)

Проверка полей регистрации

Валидирует данные формы регистрации без создания аккаунта. Используется для UI-подсказок.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    RegistrationRequest registrationRequest = new RegistrationRequest(); // RegistrationRequest | 
    try {
      ValidationResponse result = apiInstance.checkRegistrationField(registrationRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#checkRegistrationField");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **registrationRequest** | [**RegistrationRequest**](RegistrationRequest.md)|  | |

### Return type

[**ValidationResponse**](ValidationResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Данные валидны |  -  |
| **400** | Ошибка бизнес-валидации регистрации |  -  |
| **500** | Внутренняя ошибка сервера |  -  |

<a id="checkResetPasswordField"></a>
# **checkResetPasswordField**
> ValidationResponse checkResetPasswordField(resetPasswordRequest)

Проверка полей сброса пароля

Валидирует данные формы без обновления пароля. Используется для UI-подсказок.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(); // ResetPasswordRequest | 
    try {
      ValidationResponse result = apiInstance.checkResetPasswordField(resetPasswordRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#checkResetPasswordField");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **resetPasswordRequest** | [**ResetPasswordRequest**](ResetPasswordRequest.md)|  | |

### Return type

[**ValidationResponse**](ValidationResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Данные валидны |  -  |
| **400** | Ошибка бизнес-валидации сброса пароля |  -  |
| **500** | Внутренняя ошибка сервера |  -  |

<a id="getChatMessages"></a>
# **getChatMessages**
> ChatResponse getChatMessages(id)

Получение сообщений чата

Возвращает чат с собеседником и всеми сообщениями. Используется фронтендом для первичной загрузки и периодического обновления переписки.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 12L; // Long | ID чата
    try {
      ChatResponse result = apiInstance.getChatMessages(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#getChatMessages");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**| ID чата | |

### Return type

[**ChatResponse**](ChatResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Сообщения чата получены |  -  |
| **401** | Пользователь не авторизован |  -  |
| **403** | Пользователь не является участником чата |  -  |
| **404** | Чат не найден |  -  |
| **500** | Внутренняя ошибка сервера |  -  |

<a id="searchStudentSkills"></a>
# **searchStudentSkills**
> List&lt;StudentSkillResponse&gt; searchStudentSkills(name)

Поиск навыков по подстроке

Возвращает список навыков студента, чьи названия содержат указанный текст. Поиск регистронезависимый.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String name = ""; // String | Название или часть названия навыка
    try {
      List<StudentSkillResponse> result = apiInstance.searchStudentSkills(name);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#searchStudentSkills");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **name** | **String**| Название или часть названия навыка | [optional] [default to ] |

### Return type

[**List&lt;StudentSkillResponse&gt;**](StudentSkillResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Список навыков успешно получен |  -  |
| **401** | Пользователь не авторизован |  -  |
| **500** | Внутренняя ошибка сервера |  -  |

<a id="sendChatMessage"></a>
# **sendChatMessage**
> MessageResponse sendChatMessage(id, messageRequest)

Отправка сообщения

Добавляет новое сообщение в чат от имени текущего пользователя и возвращает созданное сообщение для моментального добавления в интерфейс.

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    Long id = 12L; // Long | ID чата
    MessageRequest messageRequest = new MessageRequest(); // MessageRequest | 
    try {
      MessageResponse result = apiInstance.sendChatMessage(id, messageRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#sendChatMessage");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **Long**| ID чата | |
| **messageRequest** | [**MessageRequest**](MessageRequest.md)|  | |

### Return type

[**MessageResponse**](MessageResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Сообщение отправлено |  -  |
| **400** | Ошибка валидации тела запроса |  -  |
| **401** | Пользователь не авторизован |  -  |
| **403** | Пользователь не является участником чата |  -  |
| **404** | Чат не найден |  -  |
| **500** | Внутренняя ошибка сервера |  -  |

