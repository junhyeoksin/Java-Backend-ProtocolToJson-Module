# Java-Backend-ProtocolToJson-Module 프로젝트

## 프로젝트 개요
Java-Backend-ProtocolToJson-Module 클라이언트의 요청을 받아 외부 웹 서비스와 통신하는 프록시 서버입니다. 서버는 클라이언트의 요청을 받아 `RequestHandler`를 통해 외부 웹 서비스와 상호작용하고, 응답을 클라이언트에게 반환합니다.

## 프로젝트 구조

### 1. **TXProxyServer 클래스**
- **역할**: 클라이언트의 연결을 받아 새 스레드를 생성하여 요청을 처리합니다.
- **기능**:
  - 설정 파일에서 환경 변수 읽기 (`service_port`, `webservice_url`, `len_width`, `len_include` 등)
  - `ServerSocket`을 사용하여 클라이언트 연결 대기
  - 클라이언트 요청을 `TXProxyClient` 스레드에 할당하여 처리

```java
public class TXProxyServer {
    static String proxy_id = "";
    static String webservice_url = "";
    static int service_port = 0;
    static int len_width = 0;
    static boolean len_include = false;

    public static void main(String[] argv) {
        ServerSocket server = null;
        if (argv.length == 0) {
            System.out.println("Usage: TXProxyServer proxy_id");
            return;
        }

        // 설정 읽기
        proxy_id = argv[0];
        service_port = Env.getInt(proxy_id + ".service_port");
        webservice_url = Env.get(proxy_id + ".webservice_url", "");
        len_width = Env.getInt(proxy_id + ".len_width");
        len_include = Env.get(proxy_id + ".len_include", "").equalsIgnoreCase("Y");

        if (webservice_url.length() == 0 || service_port == 0 || len_width == 0) {
            LogMgr.debug("Error: Invalid Configuration");
            return;
        }

        try {
            server = new ServerSocket(service_port);
            while (true) {
                Socket socket = server.accept(); // 클라이언트 연결 대기
                TXProxyClient client = new TXProxyClient(socket); // 새로운 클라이언트 처리
                client.start(); // 클라이언트 요청을 처리하는 스레드 시작
            }
        } catch (Exception e) {
            LogMgr.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if (server != null) try { server.close(); } catch (Exception e) {}
        }
    }
}
```

### 2. **TXProxyClient 클래스**
- **역할**: 클라이언트 요청을 처리하고 응답을 반환하는 스레드입니다.
- **기능**:
  - 클라이언트로부터 요청 메시지를 읽고, 이를 RequestHandler에 전달하여 웹 서비스 요청을 처리
  - 응답 메시지를 클라이언트에게 반환
 
```java
 public class TXProxyClient extends Thread {

    private TXSocket m_ts = null;

    public TXProxyClient(Socket socket) {
        this.m_ts = new TXSocket(socket);
    }

    public void run() {
        String req_msg = null;
        String res_msg = null;
        try {
            req_msg = m_ts.read(TXProxyServer.len_width, TXProxyServer.len_include);
            RequestHandler requestHandler = new RequestHandler(req_msg);
            res_msg = requestHandler.handleRequest(); // RequestHandler로 웹 서비스 요청 처리
        } catch (Exception e) {
            LogMgr.error(e.getMessage());
            e.printStackTrace();
        } finally {
            m_ts.write(res_msg); // 응답 메시지 전송
            m_ts.disconnect(); // 연결 종료
        }
    }
}
```

### 3. **RequestHandler 클래스**
- **역할**: 요청 메시지를 처리하고 웹 서비스와의 상호작용을 담당합니다.
- **기능**:
  - 요청 메시지를 WebServiceClient에 전달하여 웹 서비스 응답을 받아옴
  - 웹 서비스 응답을 클라이언트에게 반환할 수 있도록 가공
 
```java
 public class RequestHandler {

    private String requestMessage;

    public RequestHandler(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String handleRequest() {
        try {
            DataMap requestDataMap = new DataMap();
            requestDataMap.put("msg", requestMessage);

            WebServiceClient webServiceClient = new WebServiceClient();
            String responseJson = webServiceClient.callWebService(requestDataMap);

            if (responseJson == null || responseJson.length() == 0) {
                throw new Exception("WebService Response Error");
            }

            DataMap responseDataMap = JSONUtil.toMap(responseJson);
            return responseDataMap.getString("msg");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
```


### 4. **WebServiceClient 클래스**
- **역할**: 외부 웹 서비스와의 통신을 담당합니다.
- **기능**:
- HttpConnection을 사용하여 웹 서비스 URL에 POST 요청을 보냄
- 응답을 반환함
 
```java
  public class WebServiceClient {

    public String callWebService(DataMap requestData) {
        try {
            String requestJson = JSONUtil.toJSONString(requestData);
            HttpConnection httpConnection = new HttpConnection(TXProxyServer.webservice_url);
            return httpConnection.rest(requestJson);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

```
