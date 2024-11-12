package com.protocoltojson.txproxy;

import com.protocoltojson.common.DataMap;
import com.protocoltojson.util.HttpConnection;
import com.protocoltojson.util.JSONUtil;
import com.protocoltojson.common.LogMgr;

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
