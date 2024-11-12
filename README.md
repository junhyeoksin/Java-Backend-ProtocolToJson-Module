
# 📌  Java-Backend-ProtocolToJson-Module 프로젝트

## 프로젝트 개요
####  Java-Backend-ProtocolToJson-Module 외부 서비스에 전문 형식의 동기방식 요청을 받아 JSON으로 변환 후 송신하는 프록시 서버입니다.
#### 서버는 클라이언트의 요청을 받아 `JsonWebClient`를 통해 외부 서비스와 상호작용하고, 응답을 클라이언트에게 반환합니다.

* * *

## 프로젝트 구조

| 클래스 이름               | 역할 설명                                                                                                                                 |
|--------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| ProtocolToJsonServer      | 서버 초기화 과정으로 Socket을 생성하여 클라이언트의 연결을 대기하고, 이를 통해 클라이언트와 연결된 후 처리하는 역할을 수행                |
| ProtocolClientHandler     | 클라이언트의 요청을 처리하는 스레드, 이 클래스는 서버와 연결된 클라이언트로부터 요청을 받고, 그 요청을 처리한 후 응답을 클라이언트에게 전송 |
| RequestProcessor          | 요청 처리 및 외부 서비스 호출                                                                                                           |
| JsonWebClient             | 외부 서비스와의 통신 담당                                                                                                               |

* * *
### 1. **ProtocolToJsonServer 클래스**
### **역할**: ProtocolToJsonServer 클래스는 핵심 컴포넌트로서, 설정된 포트에서 클라이언트의 전문 요청을 수신하고, 이 요청을 JSON 형식으로 변환하는데 중점을 둡니다.
### **✨ 주요 기능**

### 서버 초기화 및 설정 로드:
argv 파라미터에서 proxy_id를 받아 서버 설정을 로드합니다. proxy_id는 설정을 구분하기 위한 식별자 역할을 하며, 이를 통해 Env 클래스를 사용하여 설정값을 가져옵니다.
서버 포트 번호(service_port), 웹 서비스 URL(webservice_url), 길이 폭(len_width), 길이 포함 여부(len_include) 같은 값들이 로드되며, 설정값의 유효성을 검증합니다.

### 클라이언트 연결 수신 및 처리:
ServerSocket을 사용해 지정된 포트에서 연결을 대기합니다.
클라이언트가 연결 요청을 보내면 Socket 객체를 생성하여 클라이언트와 통신할 준비를 합니다.
새 클라이언트가 연결될 때마다 ProtocolClientHandler 클래스를 통해 각 클라이언트의 요청을 독립적으로 처리할 수 있도록 합니다.

### 프로토콜 변환 처리 (Protocol to JSON):
클라이언트가 보내는 특정 프로토콜 형식의 요청을 받아 JSON 형식으로 변환하여 처리하는 역할을 수행합니다. (구체적인 변환 로직은 ProtocolClientHandler 클래스에서 구현)

### 에러 로깅 및 안정성:
서버 설정이 잘못되었거나 예외가 발생할 경우 LogMgr 클래스의 메소드를 사용하여 디버깅 메시지나 에러를 기록합니다.
try-with-resources와 try-catch 구문을 통해 서버 소켓과 클라이언트 연결이 안정적으로 종료되도록 설계되었습니다.

### 메소드별 기능 요약
main(String[] argv): 애플리케이션의 시작점으로, 초기 설정을 확인하고 서버 소켓을 열어 클라이언트 연결을 대기합니다. 클라이언트 연결이 수신될 때마다 handleClientConnection 메소드를 호출합니다.
initializeConfig(String[] argv): proxy_id에 따라 서버 설정을 로드하고 유효성을 검증합니다. 잘못된 설정이 있을 경우 로그에 에러 메시지를 남기고 서버를 중단합니다.
handleClientConnection(ServerSocket server): 클라이언트가 연결되면 ProtocolClientHandler 객체를 생성하여 클라이언트 요청을 비동기적으로 처리합니다. 연결 과정에서 발생하는 예외를 개별적으로 로깅하여 문제를 추적할 수 있게 합니다.

* * *

```java
public class ProtocolToJsonServer {
     static final int MAX_WAIT = 60;
     static String proxy_id = "";
     static String webservice_url = "";
     static int service_port = 0;
     static int len_width = 0;
     static boolean len_include = false;

     public static void main(String[] argv) {
         if (!initializeConfig(argv)) {
             LogMgr.debug("Usage: ProtocolToJsonServer proxy_id");
             return;
         }

         LogMgr.debug(proxy_id.toUpperCase() + " ProtocolToJsonServer Started ==========================================");

         try (ServerSocket server = new ServerSocket(service_port)) {
             LogMgr.debug("Listening for connections on port " + server.getLocalPort());

             while (true) {
                 handleClientConnection(server);
             }
         } catch (Exception e) {
             LogMgr.error("Server initialization error: " + e.getMessage());
             e.printStackTrace();
         }

         LogMgr.debug(proxy_id.toUpperCase() + " ProtocolToJsonServer Ended ==========================================");
     }

     private static boolean initializeConfig(String[] argv) {
         if (argv.length == 0) {
             LogMgr.debug("Error: Missing proxy_id argument");
             return false;
         }

         proxy_id = argv[0];
         service_port = Env.getInt(proxy_id + ".service_port");
         webservice_url = Env.get(proxy_id + ".webservice_url", "");
         len_width = Env.getInt(proxy_id + ".len_width");
         len_include = Env.get(proxy_id + ".len_include", "").equalsIgnoreCase("Y");

         if (webservice_url.isEmpty() || service_port == 0 || len_width == 0) {
             LogMgr.debug("Error: Invalid Configuration");
             return false;
         }
         
         return true;
     }

     private static void handleClientConnection(ServerSocket server) {
         try {
             Socket socket = server.accept();
             LogMgr.debug("Connected from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

             ProtocolClientHandler client = new ProtocolClientHandler(socket);
             client.start();
         } catch (Exception e) {
             LogMgr.error("Client connection error: " + e.getMessage());
             e.printStackTrace();
         }
     }
 }
```
* * *

### 2. **ProtocolClientHandler 클래스**
### **역할**: 클라이언트 요청을 처리하고 응답을 반환하는 스레드입니다. ProtocolToJsonServer에서 클라이언트가 연결될 때마다 새로운 인스턴스가 생성되어, 각 클라이언트 요청을 개별 스레드에서 처리할 수 있게 설계되었습니다.
### **✨ 주요 기능**

### 클라이언트 요청 수신 (readRequest 메서드)
TXSocket을 통해 클라이언트로부터 요청 메시지를 읽어옵니다.
요청 메시지의 길이와 포함 여부를 ProtocolToJsonServer의 설정값을 기반으로 결정합니다.
네트워크 통신 오류 발생 시 예외를 처리하여 적절히 로깅합니다.

### 요청 처리 및 JSON 변환 (processRequest 메서드)
수신된 요청 메시지를 JsonRequestProcessor 객체로 전달하여 처리합니다.
요청 메시지를 JSON 형식으로 변환하거나 특정 비즈니스 로직을 수행할 수 있습니다.
요청 처리 중 오류가 발생하면, 해당 오류를 로깅하고 기본 오류 메시지를 반환하도록 되어 있습니다.

### 응답 전송 (sendResponse 메서드)
처리된 응답 메시지를 클라이언트에게 전송합니다.
전송 중 오류 발생 시 이를 로깅하여 서버 측에서 문제를 파악할 수 있도록 합니다.

### 리소스 관리 및 연결 종료 (closeConnection 메서드)
요청 처리가 완료된 후, TXSocket을 통해 클라이언트와의 연결을 종료합니다.
리소스를 안전하게 해제하기 위해 finally 블록에서 항상 호출됩니다.
* * *

```java
public class ProtocolClientHandler extends Thread {

    private TXSocket m_ts;

    public ProtocolClientHandler(Socket socket) {
        this.m_ts = new TXSocket(socket);
    }

    @Override
    public void run() {
        try {
            String reqMsg = readRequest();
            String resMsg = processRequest(reqMsg);
            sendResponse(resMsg);
        } catch (Exception e) {
            LogMgr.error("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private String readRequest() throws Exception {
        try {
            return m_ts.read(ProtocolToJsonServer.len_width, ProtocolToJsonServer.len_include);
        } catch (Exception e) {
            LogMgr.error("Failed to read request: " + e.getMessage());
            throw e;
        }
    }

    private String processRequest(String reqMsg) {
        try {
            JsonRequestProcessor requestHandler = new JsonRequestProcessor(reqMsg);
            return requestHandler.handleRequest();
        } catch (Exception e) {
            LogMgr.error("Request processing error: " + e.getMessage());
            return "Error processing request";
        }
    }

    private void sendResponse(String resMsg) {
        try {
            m_ts.write(resMsg);
        } catch (Exception e) {
            LogMgr.error("Failed to send response: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            m_ts.disconnect();
        } catch (Exception e) {
            LogMgr.error("Error closing connection: " + e.getMessage());
        }
    }
}

```
* * *
### 3. **JsonRequestProcessor 클래스**
### **역할**: 클라이언트로부터 전달받은 JSON 형식의 요청 메시지를 처리하는 역할을 합니다
### **✨ 주요 기능**

### 요청 메시지 처리
생성자로 전달받은 requestMessage를 기반으로 요청을 처리합니다. 요청 메시지는 JSON 형식으로 전달되며, 이 메시지는 이후 다른 시스템과의 상호작용에 사용됩니다.

### WebService 호출
handleRequest 메서드는 요청 메시지를 WebService에 전달하고, 그 결과를 받아오는 역할을 합니다.

### 응답 파싱
WebService로부터 받은 응답(JSON 형식)을 파싱하여 필요한 데이터를 추출하고, 그 값을 반환합니다.

### 주요 기능
요청 메시지를 DataMap에 저장: requestMessage를 DataMap 객체에 "msg"라는 키로 저장합니다. DataMap은 일반적으로 키-값 쌍으로 데이터를 처리할 수 있는 구조입니다.
WebService 호출: JsonWebClient 클래스를 사용하여 WebService에 요청을 보냅니다. 이때, DataMap 객체가 요청 데이터로 전달됩니다. 호출 후 WebService의 응답을 받습니다.
응답 메시지 파싱: 응답 메시지가 JSON 형식이므로, JSONUtil.toMap을 사용하여 응답을 DataMap 객체로 변환한 뒤, 그 안에서 "msg" 값을 추출하여 반환합니다.

### 예외 처리
WebService 호출 중 오류 발생: 호출이 실패하면 IllegalStateException 예외를 던져 적절한 오류 메시지를 반환합니다.
응답 파싱 중 오류 발생: 응답 JSON을 파싱하는 중 오류가 발생하면, 파싱 오류를 처리하여 기본 오류 메시지를 반환합니다.

* * *
```java
public class JsonRequestProcessor {
    
    private String requestMessage;
    
    public JsonRequestProcessor(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String handleRequest() {
        DataMap requestDataMap = new DataMap();
        requestDataMap.put("msg", requestMessage);
        String responseJson = callWebService(requestDataMap);
        return parseResponse(responseJson);
    }

    private String callWebService(DataMap requestDataMap) {
        try {
            JsonWebClient webServiceClient = new JsonWebClient();
            String responseJson = webServiceClient.callWebService(requestDataMap);

            if (responseJson == null || responseJson.isEmpty()) {
                throw new IllegalStateException("WebService Response is empty or null");
            }
            return responseJson;

        } catch (Exception e) {
        	LogMgr.error("Error in WebService call: " + e.getMessage());
            e.printStackTrace();
            return "{\"msg\": \"Error: WebService call failed\"}";
        }
    }

    private String parseResponse(String responseJson) {
        try {
            DataMap responseDataMap = JSONUtil.toMap(responseJson);
            return responseDataMap.getString("msg");
        } catch (Exception e) {
        	LogMgr.error("Error parsing response JSON: " + e.getMessage());
            e.printStackTrace();
            return "Error: Failed to parse response";
        }
    }
}

```

* * *
### 4. **JsonWebClient 클래스**
### **역할**: 외부 WebService와 통신하여 JSON 형식의 데이터를 주고받는 역할을 합니다. requestData를 HTTP 요청으로 변환하여 WebService에 전송하고, 그 응답을 받아옵니다.
### **✨ 주요 기능**

### WebService 호출 (callWebService 메서드):
callWebService 메서드는 DataMap 형식으로 전달된 요청 데이터를 JSON 문자열로 변환한 후, 이를 HTTP 요청으로 WebService에 전송합니다.
HttpConnection 클래스를 사용하여 실제 HTTP 요청을 보내고, 서버로부터 받은 응답을 반환합니다.
WebService 호출 후 응답이 null이거나 비어있는 경우, 예외를 발생시키고 이를 처리합니다.

### HTTP 연결 처리:
HttpConnection 객체는 ProtocolToJsonServer.webservice_url을 사용하여 WebService와의 연결을 설정합니다. 이 URL은 서버 설정에 따라 다를 수 있으며, 클라이언트는 이를 통해 요청을 보냅니다.
HTTP 연결을 통해 요청과 응답을 처리하며, WebService와의 통신이 원활하게 이루어지도록 관리합니다.

### 예외 처리 및 로깅:
WebService 호출이나 응답 처리 중 발생하는 예외를 catch 블록에서 처리하고, 예외 메시지를 로깅하여 시스템에서 발생한 오류를 추적할 수 있도록 합니다.
예외가 발생한 경우, 기본 오류 메시지(예: "Error: WebService call failed")를 JSON 형식으로 반환하여 호출자가 오류를 쉽게 파악할 수 있도록 합니다.

#####  JsonWebClient 클래스는 WebService와의 HTTP 통신을 통해 데이터를 주고받고, 발생하는 예외를 처리하여 안정적인 서비스를 제공합니다. 이 클래스는 WebService 호출을 캡슐화하여, 시스템에서 외부 API와의 통신을 처리하는 중요한 역할을 담당합니다.

* * *
```java
  public class JsonWebClient {

    public String callWebService(DataMap requestData) {
        String response = null;
        try {
            String requestJson = JSONUtil.toJSONString(requestData);
            HttpConnection httpConnection = new HttpConnection(ProtocolToJsonServer.webservice_url);
            response = httpConnection.rest(requestJson);

            if (response == null || response.isEmpty()) {
                throw new IllegalStateException("Received empty response from WebService");
            }
        } catch (Exception e) {
            LogMgr.error("Error in WebService call: " + e.getMessage());
            e.printStackTrace();
        }
        if (response == null) {
            return "{\"msg\": \"Error: WebService call failed\"}";
        }
        return response;
    }
}
```
