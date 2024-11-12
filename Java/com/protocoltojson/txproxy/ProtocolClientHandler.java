package com.protocoltojson.txproxy;

import java.net.Socket;
import com.protocoltojson.common.LogMgr;
import com.protocoltojson.common.TXSocket;

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
