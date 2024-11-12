 package ProtocolToJson.txproxy;
 
 import java.net.ServerSocket;
 import java.net.Socket;
 import com.protocolToJson.common.Env;
 import com.protocolToJson.common.LogMgr;

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
