package com.protocolToJson.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.protocolToJson.common.DataMap;
import com.protocolToJson.common.DataMapList;
import com.protocolToJson.common.StringMap;

@SuppressWarnings("unchecked")
public class JSONUtil {
    private static final Log log = LogFactory.getLog(JSONUtil.class);

	public static String[] getKeys(JSONObject jo) {
		Set<?> keySet = jo.keySet();
		String[] keys = new String[keySet.size()];
		keySet.toArray(keys);

		return keys;
	}

	public static JSONObject parse(String json_str) {
		JSONObject jo = new JSONObject();

		try {
			JSONParser parser = new JSONParser();

			jo = (JSONObject) parser.parse(json_str);
			if (jo == null)
				jo = new JSONObject();

		} catch (Exception e) {
			log.error(e);
		}

		return jo;
	}

	public static Object parseObject(String json_str) {
		Object obj = new Object();

		try {
			JSONParser parser = new JSONParser();

			obj = parser.parse(json_str);

		} catch (Exception e) {
			log.error(e);
		}

		return obj;
	}

	public static JSONObject toJSONObject(Map<String, Object> map) {
		JSONObject jo = new JSONObject();

		for (Map.Entry<String, Object> entry : map.entrySet())
			jo.put(entry.getKey(), entry.getValue());

		return jo;
	}

	public static JSONObject toJSONObject(DataMap data) {
		JSONObject jo = new JSONObject();

		String[] keys = data.getKeys();
		for( String key : keys) {
			Object value = data.get(key);
			if( value == null )
				value = (String)"";
			if( "DataMapList".equalsIgnoreCase(value.getClass().getSimpleName()) )
				jo.put(key, toJSONArray((DataMapList)value));
			else
				jo.put(key, value);
		}

		return jo;
	}

	public static JSONObject toJSONObject(StringMap data) {
		JSONObject jo = new JSONObject();

		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(data);
		jo = toJSONObject(map);

		return jo;
	}

	public static JSONArray toJSONArray(List<Map<String, Object>> list) {
		JSONArray ja = new JSONArray();

		for (Map<String, Object> map : list)
			ja.add(toJSONObject(map));

		return ja;
	}

	public static JSONArray toJSONArray(DataMapList list) {
		JSONArray ja = new JSONArray();

		for (DataMap map : list)
			ja.add(toJSONObject(map));

		return ja;
	}
	
	public static DataMapList toDataMapList(JSONArray ja) {
		DataMapList dml = new DataMapList();
		
		for(int i=0; i < ja.size(); i++) {
			DataMap dm = JSONUtil.toMap((JSONObject)ja.get(i));
			dml.add(dm);
		}
		
		return dml;
	}

	public static String toJSONString(List<Map<String, Object>> list) {
		return toJSONArray(list).toJSONString();
	}

	public static String toJSONString(DataMapList list) {
		return toJSONArray(list).toJSONString();
	}

	public static String toJSONString(Map<String, Object> map) {
		return toJSONObject(map).toJSONString();
	}

	public static String toJSONString(DataMap map) {
		return toJSONObject(map).toJSONString();
	}

	public static DataMap toMap(String json_str) {
		return toMap(parse(json_str));
	}

	public static DataMap toMap(JSONObject jo) {
		DataMap data = new DataMap();
		toMap(jo, data);

		return data;
	}

	public static void toMap(JSONObject jo, DataMap data) {
		if (jo == null)
			return;

		String[] keys = getKeys(jo);

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Object obj = jo.get(key);
			if (obj == null)
				obj = (String)"";
			if ("JSONObject".equals(obj.getClass().getSimpleName())) {
				data.put(key, toMap((JSONObject) obj));
			} else if ("JSONArray".equals(obj.getClass().getSimpleName())) {
				DataMapList dml = new DataMapList();
				JSONArray array = (JSONArray) obj;
				for (int j = 0; j < array.size(); j++) {
					Object obj2 = array.get(j);
					if( obj2 == null )
						obj2 = "";
					if ("JSONObject".equals(obj2.getClass().getSimpleName()))
						dml.add(toMap((JSONObject) obj2));
				}
				data.put(key, dml);
			} else {
				data.put(key, obj);
			}
		}
	}

	
	// JSONObject -> StringMap
	public static StringMap toStringMap(JSONObject jo) {
		return toStringMap(jo.toJSONString(), "");
	}

	// JSON -> StringMap
	public static StringMap toStringMap(String json) {
		return toStringMap(json, "");
	}

	// JSON -> StringMap
	public static StringMap toStringMap(String json, String prefix) {
		StringMap data = new StringMap();

		if (prefix != null && prefix.length() > 0)
			prefix += ".";
		else
			prefix = "";

		try {
			JSONParser parser = new JSONParser();

			Object obj = parser.parse(json);
			if (obj == null)
				return data;

			if ("JSONObject".equals(obj.getClass().getSimpleName()))
				toStringMap((JSONObject) obj, data, prefix);
			else if ("JSONArray".equals(obj.getClass().getSimpleName())) {
				JSONArray array = (JSONArray) obj;
				for (int j = 0; j < array.size(); j++)
					toStringMap((JSONObject) array.get(j), data, prefix + ((j > 0) ? "[" + j + "]" : ""));
				if (array.size() > 1)
					data.put("#count", array.size());
			}
		} catch (Exception e) {
			log.error(e);
			data.put("#error", "toStringMap error");
		}

		return data;
	}

	public static void toStringMap(JSONObject node, StringMap data, String parent) {
		if (node == null)
			return;

		String prefix = parent;
		if (prefix != null && prefix.length() > 0) {
			if (prefix.charAt(prefix.length() - 1) != '.')
				prefix += ".";
		} else
			prefix = "";

		Set<?> keySet = node.keySet();
		String[] keys = new String[keySet.size()];
		keySet.toArray(keys);

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Object obj = node.get(key);
			if( obj == null )
				obj = "";

			if ("JSONObject".equals(obj.getClass().getSimpleName()))
				toStringMap((JSONObject) obj, data, prefix + key);
			else if ("JSONArray".equals(obj.getClass().getSimpleName())) {
				JSONArray array = (JSONArray) obj;
				for (int j = 0; j < array.size(); j++) {
					Object obj2 = array.get(j);
					if( obj2 == null )
						obj2 = "";
					if ("JSONObject".equals(obj2.getClass().getSimpleName()))
						toStringMap((JSONObject) obj2, data, prefix + key + ((j > 0) ? "[" + j + "]" : ""));
					else
						data.put(prefix + key + "[" + j + "]", obj2 + "");
				}
				if (array.size() > 1)
					data.put(prefix + key + ".#count", array.size());
			} else {
				data.put(prefix + key, obj + "");
			}
		}
	}


	public static String doPost(String url, String json) throws Exception {
		return doPost(url, json, 60, "utf-8", null);
	}

	public static String doPost(String url, String json, int timeout) throws Exception {
		return doPost(url, json, timeout, "utf-8", null);
	}

	public static String doPost(String url, String json, String charset) throws Exception {
		return doPost(url, json, 60, charset, null);
	}

	public static String doPost(String url, String json, StringMap headers) throws Exception {
		return doPost(url, json, 60, "utf-8", headers);
	}

	public static String doPost(String url, String json, int timeout, StringMap headers) throws Exception {
		return doPost(url, json, timeout, "utf-8", headers);
	}

	public static String doPost(String url, String json, String charset, StringMap headers) throws Exception {
		return doPost(url, json, 60, charset, headers);
	}

	public static String doPost(String url, String json, int timeout, String charset, StringMap headers) throws Exception {
		URL u = new URL(url);
		HttpURLConnection uc = null;

		if (u.getProtocol().toLowerCase().startsWith("https")) {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());
			uc = (HttpsURLConnection) u.openConnection();
		} else
			uc = (HttpURLConnection) u.openConnection();

		uc.setReadTimeout(timeout * 1000);
		uc.setUseCaches(false);

		uc.setRequestProperty("Content-Type", "application/json; charset=" + charset);

		// Headers
		if( headers != null && headers.size() > 0 ) {
			String[] keys = headers.getKeys();
			for(int i=0; i < keys.length; i++)
				uc.setRequestProperty(keys[i], headers.get(keys[i],""));
		}

		uc.setDoOutput(true);

		OutputStreamWriter os = new OutputStreamWriter(uc.getOutputStream(), charset);

		try {
			os.write(json);
			os.flush();
		}
		finally {
			os.close();
		}

		BufferedInputStream is = new BufferedInputStream(uc.getInputStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int length;
		try {
			while ((length = is.read(buffer)) != -1)
				baos.write(buffer, 0, length);
		}
		finally {
			is.close();
			baos.close();
		}

		return new String(baos.toByteArray(), charset);
	}

//	public static DataMap requestToMap(HttpServletRequest req) {
//		DataMap map = new DataMap();
//
//		Enumeration<?> e = req.getParameterNames();
//		while (e.hasMoreElements()) {
//			String name = (String) e.nextElement();
//			String[] values = req.getParameterValues(name);
//
//			for (int i = 0; i < values.length; i++)
//				map.put(name + ((i > 0) ? "[" + i + "]" : ""), values[i]);
//
//			if (values.length > 1)
//				map.put(name + ".count", values.length);
//		}
//
//		return map;
//	}
//
	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		trustAllCerts[0] = new SSLTrustManager();
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public static class SSLHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}

	}

	public static class SSLTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

}