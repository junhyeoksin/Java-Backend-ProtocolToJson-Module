package com.protocoltojson.txproxy;

import com.protocoltojson.common.DataMap;
import com.protocoltojson.common.LogMgr;
import com.protocoltojson.util.JSONUtil;

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
