package com.protocolToJson.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.protocolToJson.common.StringMap;

public class HttpConnection {

	private URL url;
	private String cookie;
	private String spec;
	private StringMap req_headers;
	private StringMap res_headers;
	private StringMap data;
	private String boundary;
	private boolean isMulti;

	public HttpConnection(String spec, boolean isMulti) throws Exception {
		set(spec, isMulti);
	}

	public HttpConnection(String spec) throws Exception {
		set(spec);
	}

	public HttpConnection() throws Exception {
		set("");
	}

	public void set(String spec, boolean isMulti, boolean bContinue) throws Exception {
		if (spec != null && spec.length() > 0) {
			url = new URL(spec);

			if (!url.getProtocol().toLowerCase().startsWith("http"))
				throw new Exception("Only works for HTTP/HTTPS protocols");
		}

		if (!bContinue)
			cookie = new String();

		data = new StringMap();
		req_headers = new StringMap();
		res_headers = new StringMap();

		this.spec = spec;
		boundary = "----------------boundary." + new Random().nextDouble();
		this.isMulti = isMulti;
	}

	public void set(String spec, boolean isMulti) throws Exception {
		set(spec, isMulti, false);
	}

	public void set(String spec) throws Exception {
		set(spec, false, false);
	}

	public void add(Object name, Object value) {
		if (name == null || name.toString().length() == 0)
			return;

		if (value == null)
			value = "";

		data.put(name.toString(), value.toString());
	}

	public void addHeader(String name, String value) {
		if (name == null || name.toString().length() == 0)
			return;

		if (value == null)
			value = "";

		req_headers.put(name, value);
	}

	public StringMap getHeaders() {
		return res_headers;
	}

	@SuppressWarnings("deprecation")
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String[] keys = data.getKeys();

		if (isMulti) {
			for (int i = 0; i < keys.length; i++) {
				try {
					sb.append("--").append(boundary).append("\r\n").append("Content-Disposition: form-data; name=\"")
							.append(URLEncoder.encode(keys[i], "ISO-8859-1")).append("\"\r\n\r\n")
							.append(URLEncoder.encode(data.get(keys[i]), "ISO-8859-1")).append("\r\n");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			sb.append("--").append(boundary).append("--\r\n");
		} else {
			for (int i = 0; i < keys.length; i++) {
				if (i > 0)
					sb.append("&");

				sb.append(URLEncoder.encode(keys[i])).append("=")
						.append(URLEncoder.encode(data.get(keys[i]).toString()));
			}
		}

		return sb.toString();
	}

	public String getCookie() {
		return cookie;
	}

	public String post() throws Exception {
		StringBuffer sb = new StringBuffer();
		String req_data = toString();
		HttpURLConnection uc;

		if (url.getProtocol().toLowerCase().startsWith("https")) {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());
			uc = (HttpsURLConnection) url.openConnection();
		}
		else
			uc = (HttpURLConnection) url.openConnection();

		if (cookie != "")
			uc.setRequestProperty("Cookie", cookie);

		uc.setUseCaches(false);

		if (req_data != null && req_data.length() > 0) {

			String[] keys = req_headers.getKeys();
			for (int i = 0; i < keys.length; i++)
				uc.setRequestProperty(keys[i], req_headers.get(keys[i], ""));

			if (isMulti)
				uc.setRequestProperty("Content-Type", "multipart/form-data; boundary=\"" + boundary + "\"");

			uc.setDoOutput(true);
			OutputStreamWriter outStream = new OutputStreamWriter(uc.getOutputStream());
			outStream.write(req_data);

			outStream.flush();
			outStream.close();
		}


		InputStream inStream = uc.getInputStream();

		String header = "";
		String charset = "EUC-KR";
		for (int i = 1; header != null; i++) {
			String key = uc.getHeaderFieldKey(i);
			header = uc.getHeaderField(i);

			if (key != null && header != null) {
				if (key.contains("Set-cookie"))
					cookie += header.substring(0, header.indexOf(';') + 1) + " ";
				if( key.contains("Content-Type") && header.toLowerCase().contains("utf-8") )
					charset = "UTF-8";

				res_headers.put(key, header);
			}
		}

		InputStreamReader r = new InputStreamReader(inStream, charset);

		int c;
		while ((c = r.read()) != -1)
			sb.append((char) c);

		return sb.toString();
	}

	public String get() throws Exception {
		if (isMulti)
			throw new Exception("MultiPart not support GET method");

		if (spec.contains("?"))
			url = new URL(spec + "&" + toString());
		else
			url = new URL(spec + "?" + toString());

		StringBuffer sb = new StringBuffer();
		HttpURLConnection uc;

		if (url.getProtocol().toLowerCase().startsWith("https")) {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());
			uc = (HttpsURLConnection) url.openConnection();
		}
		else
			uc = (HttpURLConnection) url.openConnection();

		if (cookie != "")
			uc.setRequestProperty("Cookie", cookie);

		uc.setUseCaches(false);

		String[] keys = req_headers.getKeys();
		for (int i = 0; i < keys.length; i++)
			uc.setRequestProperty(keys[i], req_headers.get(keys[i], ""));

		InputStream inStream = uc.getInputStream();
		if (inStream == null)
			return null;

		String header = "";
		String charset = "EUC-KR";
		for (int i = 1; header != null; i++) {
			String key = uc.getHeaderFieldKey(i);
			header = uc.getHeaderField(i);

			if (key != null && header != null) {
				if (key.contains("Set-Cookie"))
					cookie += header.substring(0, header.indexOf(';') + 1) + " ";
				if( key.contains("Content-Type") && header.toLowerCase().contains("utf-8") )
					charset = "UTF-8";

				res_headers.put(key, header);
			}
		}

		InputStreamReader r = new InputStreamReader(inStream, charset);

		int c;
		while ((c = r.read()) != -1)
			sb.append((char) c);

		return sb.toString();
	}



	public String rest(String contents) throws Exception {
		return rest(contents, 60, "utf-8");
	}

	public String rest(String contents, int timeout) throws Exception {
		return rest(contents, timeout, "utf-8");
	}

	public String rest(String contents, String charset) throws Exception {
		return rest(contents, 60, charset);
	}

	public String rest(String contents, int timeout, String charset) throws Exception {
		HttpURLConnection uc = null;

		if (url.getProtocol().toLowerCase().startsWith("https")) {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());
			uc = (HttpsURLConnection) url.openConnection();
		} else
			uc = (HttpURLConnection) url.openConnection();

		uc.setReadTimeout(timeout * 1000);
		uc.setUseCaches(false);

		uc.setRequestProperty("Content-Type", "application/json; charset=" + charset);

		String[] keys = req_headers.getKeys();
		for (int i = 0; i < keys.length; i++)
			uc.setRequestProperty(keys[i], req_headers.get(keys[i], ""));

		uc.setDoOutput(true);

		OutputStreamWriter os = new OutputStreamWriter(uc.getOutputStream(), charset);
		os.write(contents);
		os.flush();
		os.close();

		String header = "";
		for (int i = 1; header != null; i++) {
			String key = uc.getHeaderFieldKey(i);
			header = uc.getHeaderField(i);

			if (key != null && header != null) {
				if (key.contains("Set-Cookie"))
					cookie += header.substring(0, header.indexOf(';') + 1) + " ";
				if( key.contains("Content-Type") && header.toLowerCase().contains("utf-8") )
					charset = "UTF-8";

				res_headers.put(key, header);
			}
		}

		BufferedInputStream is = new BufferedInputStream(uc.getInputStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int length;
		while ((length = is.read(buffer)) != -1)
			baos.write(buffer, 0, length);

		is.close();

		return new String(baos.toByteArray(), charset);
	}


	public void toFile(String filename) throws Exception {
		if (spec.contains("?"))
			url = new URL(spec + "&" + toString());
		else
			url = new URL(spec + "?" + toString());

		HttpURLConnection uc;

		if (url.getProtocol().toLowerCase().startsWith("https")) {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());

			uc = (HttpsURLConnection) url.openConnection();
		} else
			uc = (HttpURLConnection) url.openConnection();

		if (cookie != "")
			uc.setRequestProperty("Cookie", cookie);

		uc.setUseCaches(false);

		String[] keys = req_headers.getKeys();
		for (int i = 0; i < keys.length; i++)
			uc.setRequestProperty(keys[i], req_headers.get(keys[i], ""));

		
		InputStream is = uc.getInputStream();

		if (is == null)
			return;

		FileOutputStream fos = new FileOutputStream(filename);
		int c = 0;

		while ((c = is.read()) != -1)
			fos.write(c);

		fos.close();
	}

	public byte[] toBytes() throws Exception {
		if (spec.contains("?"))
			url = new URL(spec + "&" + toString());
		else
			url = new URL(spec + "?" + toString());

		HttpURLConnection uc;

		if (url.getProtocol().toLowerCase().startsWith("https")) {
			trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(new SSLHostnameVerifier());

			uc = (HttpsURLConnection) url.openConnection();
		} else
			uc = (HttpURLConnection) url.openConnection();

		if (cookie != "")
			uc.setRequestProperty("Cookie", cookie);

		uc.setUseCaches(false);

		String[] keys = req_headers.getKeys();
		for (int i = 0; i < keys.length; i++)
			uc.setRequestProperty(keys[i], req_headers.get(keys[i], ""));

		InputStream is = uc.getInputStream();

		if (is == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int read_len = 0;
		byte[] buf = new byte[4096];

		while ((read_len = is.read(buf, 0, buf.length)) != -1)
			baos.write(buf, 0, read_len);

		byte[] bytes = baos.toByteArray();

		baos.close();

		return bytes;
	}

	private void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		trustAllCerts[0] = new SSLTrustManager();
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public class SSLHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String paramString, SSLSession paramSSLSession) {
			return true;
		}

	}

	public class SSLTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

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
