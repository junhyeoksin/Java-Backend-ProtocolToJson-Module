package com.protocoltojson.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.protocoltojson.crypto.AES256;


public class UtilMgr {


	public static String to_8859(String s) {
		return to_8859(s, "EUC-KR");
	}

	public static String to_8859(String str, String from_enc) {
		String t = null;

		try {
			t = new String(str.getBytes(from_enc), "8859_1");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}


	public static String to_KR(String s) {
		return to_KR(s, "EUC-KR");
	}

	public static String to_KR(String s, String to_enc) {
		if (Env.get("system.encoding", "").equalsIgnoreCase(to_enc))
			return s;

		String t = null;

		try {
			t = new String(s.getBytes(), to_enc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}


	public static String replace(String str, String pattern, String replace) {

		if (str == null || str.length() < 1)
			return "";

		int s = 0;
		int e = 0;

		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}

		result.append(str.substring(s));

		return result.toString();
	}


	public static String commaFormat(String str) {

		DecimalFormat df = new DecimalFormat("###,###,###,###.##");

		if (str == null)
			return "";
		Double num;

		try {
			num = new Double(str);
		} catch (NumberFormatException nfe) {
			return str;
		} catch (NullPointerException npe) {
			return str;
		}

		df.format(num);

		return df.format(num);
	}


	public static String[] explode(String str, String divisor) {
		return split(str, divisor);
	}

	public static String[] split(String str, String divisor) {

		if (str == null || str.length() == 0)
			return null;

		return str.split("[" + divisor + "]");
	}





	public static String[] split(String str, int length) {

		if( str == null || str.length() == 0 )
			return null;

		int len = length(str);
		StringList list = new StringList();

		for(int i=0; i < len; i+=length) {
			if( len-i > length )
				list.add(substring(str,i,i+length));
			else
				list.add(substring(str,i));
		}

		String[] arr = new String[list.size()];
		list.toArray(arr);

		return arr;
	}




	public static String merge(String[] field, int[] width, boolean[] format) {
		int nfield = field.length;
		String msg = "";

		if (nfield == 0)
			return msg;

		for (int i = 0; i < nfield; i++) {
			if (field[i] == null)
				field[i] = "";

			if (format[i])
				msg += msg_number(field[i], width[i], true, true);
			else
				msg += msg_string(field[i], width[i], false);
		}

		return msg;
	}



	public static String[] parse(String str, int[] width) {

		if (str == null || str.length() == 0)
			return null;

		int len = str.length();
		int cur = 0;
		StringList list = new StringList();

		for (int i = 0; i < width.length; i++) {
			int next = cur + width[i];

			if (next >= len) {
				list.add(str.substring(cur));
				cur = len;
				break;
			} else {
				list.add(str.substring(cur, next));
				cur = next;
			}

		}

		if (cur < len)
			list.add(str.substring(cur));

		return list.toArray();
	}



	public static String[] parse(String str, int[] width, int[] type) {

		if (str == null || str.length() == 0)
			return null;

		int len = str.length();
		int i, cur = 0;
		String field = "";
		StringList list = new StringList();

		for (i = 0; i < width.length; i++) {
			int next = cur + width[i];

			if (next >= len) {
				field = str.substring(cur);
				cur = len;
			} else {
				field = str.substring(cur, next);
				cur = next;
			}

			if (type[i] > 0)
				field = Integer.parseInt(field) + "";

			list.add(field);

			if (cur == len)
				break;
		}

		if (cur < len) {
			field = str.substring(cur);

			if (type[i] > 0)
				field = Integer.parseInt(field) + "";

			list.add(field);
		}

		return list.toArray();
	}



	public static String[] nparse(String str, int[] width) {

		if (str == null || str.length() == 0)
			return null;

		int len = str.length();
		int wlen = width.length;
		int cur = 0;
		StringList list = new StringList();

		if (width[0] < len) {
			cur = width[0];
			int repeat = Integer.parseInt(str.substring(0, cur));
			list.add(str.substring(0, cur));

			for (int i = 0; i < repeat; i++) {

				for (int j = 1; j < wlen; j++) {
					int next = cur + width[j];

					if (next >= len) {
						list.add(str.substring(cur));
						cur = len;
						break;
					} else {
						list.add(str.substring(cur, next));
						cur = next;
					}

				}

				if (cur == len)
					break;
			}

		}

		if (cur < len)
			list.add(str.substring(cur));

		return list.toArray();
	}



	public static String trim(String str) {
		if (str == null)
			return null;

		str = replace(str, "　", "  ");

		return str.trim();
	}


	public static int to_int(String str) {
		if (str == null || str.length() == 0)
			return 0;

		String nstr = "";
		String chars = "-0123456789";

		for (int i = 0; i < str.length(); i++) {
			if (chars.indexOf(str.charAt(i)) != -1)
				nstr += str.charAt(i);
		}

		if (nstr.length() == 0)
			return 0;

		int sign = 1;

		if (nstr.indexOf("-") >= 0) {
			sign = -1;
			nstr = nstr.substring(nstr.indexOf("-") + 1);

			if (nstr.length() == 0)
				return 0;
		}

		return sign * Integer.parseInt(nstr);
	}


	public static long to_long(String str) {
		if (str == null || str.length() == 0)
			return 0;

		String nstr = "";
		String chars = "-0123456789";

		for (int i = 0; i < str.length(); i++) {
			if (chars.indexOf(str.charAt(i)) != -1)
				nstr += str.charAt(i);
		}

		if (nstr.length() == 0)
			return 0;

		int sign = 1;

		if (nstr.indexOf("-") >= 0) {
			sign = -1;
			nstr = nstr.substring(nstr.indexOf("-") + 1);

			if (nstr.length() == 0)
				return 0;
		}

		return sign * Long.parseLong(nstr);
	}



	public static float to_float(String str) {
		if (str == null)
			return 0;

		str = trim(str);

		if (str.length() == 0)
			return 0;

		int sign = 1;

		if (str.indexOf("-") >= 0) {
			sign = -1;
			str = str.substring(str.indexOf("-") + 1);
		}

		return sign * Float.parseFloat(str);
	}



	public static double to_double(String str) {
		if (str == null)
			return 0;

		str = trim(str);

		if (str.length() == 0)
			return 0;

		int sign = 1;

		if (str.substring(0, 1).equals("-")) {
			sign = -1;
			str = str.substring(1);
		}

		return sign * Double.parseDouble(str);
	}



	public static String get_date() {
		return get_date("", 0, 0);
	}

	public static String get_date(String divisor) {
		return get_date(divisor, 0, 0);
	}

	public static String get_date(int month_offset) {
		return get_date("", month_offset, 0);
	}

	public static String get_date(int month_offset, int day_offset) {
		return get_date("", month_offset, day_offset);
	}

	public static String get_date(String divisor, int month_offset) {
		return get_date(divisor, month_offset, 0);
	}

	public static String get_date(String divisor, int month_offset, int day_offset) {
		Calendar c = Calendar.getInstance();

		if (month_offset != 0)
			c.add(Calendar.MONTH, month_offset);

		if (day_offset != 0)
			c.add(Calendar.DATE, day_offset);

		if (divisor == null)
			divisor = "";

		StringBuffer sb = new StringBuffer();

		sb.append(c.get(Calendar.YEAR)).append(divisor);
		sb.append(UtilMgr.msg_int(c.get(Calendar.MONTH) + 1, 2, true, true)).append(divisor);
		sb.append(UtilMgr.msg_int(c.get(Calendar.DAY_OF_MONTH), 2, true, true));

		return sb.toString();
	}


	public static String get_time() {
		return get_time("", false);
	}

	public static String get_time(String divisor) {
		return get_time(divisor, false);
	}

	public static String get_time(boolean bMsec) {
		return get_time("", bMsec);
	}

	public static String get_time(String divisor, boolean bMsec) {
		Calendar c = Calendar.getInstance();

		if (divisor == null)
			divisor = "";

		StringBuffer sb = new StringBuffer();

		sb.append(msg_int(c.get(Calendar.HOUR_OF_DAY), 2, true, true)).append(divisor);
		sb.append(msg_int(c.get(Calendar.MINUTE), 2, true, true)).append(divisor);
		sb.append(msg_int(c.get(Calendar.SECOND), 2, true, true));

		if (bMsec) {
			if (divisor.length() > 0)
				sb.append(".");
			sb.append(msg_int(c.get(Calendar.MILLISECOND), 3, true, true));
		}

		return sb.toString();
	}

	public static int get_week() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_WEEK);
	}


	public static String get_filler(int l) {
		return get_filler(l, " ");
	}

	public static String get_filler(int l, String filler) {
		StringBuffer sb = new StringBuffer();

		if (filler == null || filler.length() == 0)
			filler = " ";

		for (int i = 0; i < l; i++)
			sb.append(filler);

		return sb.toString();
	}

	public static StringMap loadXML(String path, String filename) {
		return loadXML(new File(path, filename));
	}

	public static StringMap loadXML(String filename) {
		return loadXML(new File(filename));
	}

	public static StringMap loadXML(File f) {

		StringMap data = new StringMap();

		if (!f.exists())
			return data;

		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(f);
			Element root = doc.getDocumentElement();

			if (root.hasAttributes()) {
				NamedNodeMap attrs = root.getAttributes();

				for (int i = 0; i < attrs.getLength(); i++) {
					Attr attr = (Attr) attrs.item(i);

					data.put(attr.getName().trim().toLowerCase(), attr.getValue().trim());
				}

			}

			if (root.hasChildNodes()) {
				NodeList nodes = root.getChildNodes();

				for (int i = 0; i < nodes.getLength(); i++)
					readNode(nodes.item(i), data, "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static void readNode(Node node, StringMap data, String parent) {

		// 코멘트 노드는 생략
		if (node.getNodeType() == Node.COMMENT_NODE)
			return;

		// 텍스트/데이타 노드면 상위노드의 값으로 설정
		if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE) {
			String value = node.getNodeValue();

			if (value != null && value.length() > 0)
				value = value.replace('\n', ' ').replace('\t', ' ').trim();

			if (value != null && value.length() > 0)
				data.put(parent, value);

			return;
		}

		// 자신의 노드명을 추가
		String myself = parent;

		if (parent == null || parent.length() == 0)
			myself = node.getNodeName().trim().toLowerCase();
		else
			myself += "." + node.getNodeName().trim().toLowerCase();

		// 자신의 동일노드가 있을때 카운트 추가
		int count = data.getInt(myself + ".#count");
		data.put(myself + ".#count", count + 1);

		// 카운트가 0보다 크면 인덱스 추가
		if (count > 0)
			myself += "[" + count + "]";

		// 속성값 추가
		if (node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();

			for (int i = 0; i < attrs.getLength(); i++) {
				Attr attr = (Attr) attrs.item(i);

				data.put(myself + "." + attr.getName().trim().toLowerCase(), attr.getValue().trim());
			}

		}

		// 하위노드가 있으면 재귀호출
		if (node.hasChildNodes()) {
			NodeList childs = node.getChildNodes();

			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);

				readNode(child, data, myself);
			}

		}

	}

	// JSON -> StringMap
	public static StringMap parseJSON(String json) {
		return parseJSON(json, "");
	}

	// JSON -> StringMap
	public static StringMap parseJSON(String json, String prefix) {
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
				parseJSON((JSONObject) obj, data, prefix);
			else if ("JSONArray".equals(obj.getClass().getSimpleName())) {
				JSONArray array = (JSONArray) obj;
				for (int j = 0; j < array.size(); j++)
					parseJSON((JSONObject) array.get(j), data, prefix + ((j > 0) ? "[" + j + "]" : ""));
				if (array.size() > 1)
					data.put("#count", array.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			data.put("#error", e.getMessage());
		}

		return data;
	}

	public static void parseJSON(JSONObject node, StringMap data, String parent) {
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
			if ("JSONObject".equals(obj.getClass().getSimpleName()))
				parseJSON((JSONObject) obj, data, prefix + key);
			else if ("JSONArray".equals(obj.getClass().getSimpleName())) {
				JSONArray array = (JSONArray) obj;
				for (int j = 0; j < array.size(); j++) {
					Object obj2 = array.get(j);
					if ("JSONObject".equals(obj2.getClass().getSimpleName()))
						parseJSON((JSONObject) obj2, data, prefix + key + ((j > 0) ? "[" + j + "]" : ""));
					else
						data.put(prefix + key + "[" + j + "]", obj2 + "");
				}
				if (array.size() > 1)
					data.put(prefix + key + ".#count", array.size());
			} else {
				if (obj != null && !"".equals(obj))
					data.put(prefix + key, obj + "");
			}
		}
	}

	public static String doScrap(String src, String last, String find1, String find2, String find3) {
		String find = null;

		int start = src.indexOf(find1);
		if (start == -1)
			return null;

		start += find1.length();

		if (find2 != null && find2.length() > 0) {
			int start2 = src.indexOf(find2, start);
			if (start2 == -1)
				return null;

			start = start2 + find2.length();
		}

		if (find3 != null && find3.length() > 0) {
			int start3 = src.indexOf(find3, start);
			if (start3 == -1)
				return null;

			start = start3 + find3.length();
		}

		if (last != null && last.length() > 0) {
			int end = src.indexOf(last, start);
			if (end == -1)
				return null;

			find = src.substring(start, end);
		} else
			find = src.substring(start);

		return find.trim();
	}

	public static String doScrap(String src, String last, String find1, String find2) {
		return doScrap(src, last, find1, find2, null);
	}

	public static String doScrap(String src, String last, String find1) {
		return doScrap(src, last, find1, null, null);
	}

	public static String toString(byte[] bytes) {
		return new String(bytes);
	}

	public static String toString(byte[] bytes, String charset) {
		String s = null;

		try {
			if (charset == null || charset.length() == 0)
				s = new String(bytes);
			else
				s = new String(bytes, charset);
		} catch (UnsupportedEncodingException e) {
			LogMgr.error(UtilMgr.getStackTraceFromThrowable(e));
		}

		return s;
	}

	public static String toString(String str, String charset) {
		String s = null;

		try {
			if (charset == null || charset.length() == 0)
				s = new String(str.getBytes());
			else
				s = new String(str.getBytes(), charset);
		} catch (UnsupportedEncodingException e) {
			LogMgr.error(UtilMgr.getStackTraceFromThrowable(e));
		}

		return s;
	}

	public static String fromString(String str, String from_charset) {
		String s = null;

		if (from_charset == null || from_charset.length() == 0)
			return str;

		try {
			s = new String(str.getBytes(from_charset));
		} catch (UnsupportedEncodingException e) {
			LogMgr.error(UtilMgr.getStackTraceFromThrowable(e));
		}

		return s;
	}

	public static String toString(String str, String from_charset, String to_charset) {
		String s = null;

		try {
			if (to_charset == null || to_charset.length() == 0)
				s = new String(str.getBytes(from_charset));
			else
				s = new String(str.getBytes(from_charset), to_charset);
		} catch (UnsupportedEncodingException e) {
			LogMgr.error(UtilMgr.getStackTraceFromThrowable(e));
		}

		return s;
	}


	public static byte[] getBytes(String s) {
		return s.getBytes();
	}

	public static byte[] getBytes(String s, String charset) {
		byte[] bytes = null;

		try {
			if (charset == null || charset.length() == 0)
				bytes = s.getBytes();
			else
				bytes = s.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			LogMgr.error(UtilMgr.getStackTraceFromThrowable(e));
		}

		return bytes;
	}


	public static String substring(String str, int beginIndex, int endIndex) {
		return toString(Arrays.copyOfRange(getBytes(str,"EUC-KR"), beginIndex, endIndex), "EUC-KR");
	}

	public static String substring(String str, int beginIndex) {
		byte[] bytes = getBytes(str,"EUC-KR");
		return toString(Arrays.copyOfRange(bytes, beginIndex, bytes.length), "EUC-KR");
	}

	public static String substring(byte[] bytes, int beginIndex, int endIndex) {
		return toString(Arrays.copyOfRange(bytes, beginIndex, endIndex), "EUC-KR");
	}

	public static String substring(byte[] bytes, int beginIndex) {
		return toString(Arrays.copyOfRange(bytes, beginIndex, bytes.length), "EUC-KR");
	}


	public static int length(String str) {
		return getBytes(str,"EUC-KR").length;
	}

	public static String msg_string(String str, int width, boolean align) {
		return msg_string(str, width, align, " ");
	}

	public static String msg_string(String str, int width, boolean align, String fill_char) {
		if (width <= 0)
			return "";

		byte[] bytes = getBytes(str, "EUC-KR");
		int l = bytes.length;

		if (width == l)
			return str;
		else if (width < l)
			return substring(bytes, 0, width);

		byte[] newBytes = new byte[width];

		if (!align) {
			System.arraycopy(bytes, 0, newBytes, 0, l);
			Arrays.fill(newBytes, l, width, (byte) ' ');
		} else {
			System.arraycopy(bytes, 0, newBytes, width - l, l);
			Arrays.fill(newBytes, 0, width - l, (byte) fill_char.charAt(0));
		}

		return toString(newBytes);
	}


	public static String _msg_string(String str, int width, boolean align) {
		return _msg_string(str, width, align, " ");
	}

	public static String _msg_string(String str, int width, boolean align, String fill_char) {
		if (width <= 0)
			return "";

		int l = UtilMgr.length(str);

		if (width == l)
			return str;
		else if (width < l)
			return substring(str, 0, width);

		String filler = get_filler(width-l, fill_char);
		if (!align)
			return str + filler;

		return filler + str;
	}

	public static String msg_int(int num, int width, boolean align, boolean padding) {
		String str = Integer.toString(num);
		return msg_string(str, width, align, "0");
	}

	public static String msg_number(String str, int width, boolean align, boolean padding) {
		return msg_string(str, width, align, "0");
	}

	public static boolean isWindows() {
		String os_name = System.getProperty("os.name");

		return (os_name.indexOf("Windows") >= 0);
	}

	public static String getHostName() {
		String hostname = System.getenv(UtilMgr.isWindows() ? "COMPUTERNAME" : "HOSTNAME");

		if (hostname == null)
			hostname = System.getenv("SERVER_NAME");

		if (hostname == null)
			hostname = System.getProperty("HOSTNAME","");

		return hostname;
	}

	public static String getHostAddress() {
		String addr = "";

		try {
			addr = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
		}

		return addr;
	}

	public static String getHostID() {

		try {
			byte[] ipaddr = InetAddress.getLocalHost().getAddress();

			if (ipaddr.length == 4) {
				byte[] id = new byte[4];
				id[0] = ipaddr[1];
				id[1] = ipaddr[0];
				id[2] = ipaddr[3];
				id[3] = ipaddr[2];

				return toHexString(id);
			}

		} catch (Exception e) {
		}

		return "";
	}

	public static String toHexString(byte[] s) {
		return toHexString(s, 0);
	}

	public static String toHexString(byte[] s, int wrap_cnt) {
		StringBuffer hs = new StringBuffer();
		String h;
		int wrap_width = (s.length + "").length();

		for (int i = 0; i < s.length; i++) {

			if (wrap_cnt > 0 && i % wrap_cnt == 0) {
				if (i > 0)
					hs.append('\n');
				hs.append(msg_int(i, wrap_width, true, true)).append(": ");
			}

			byte c = s[i];
			h = Integer.toHexString(c & 0xFF).toUpperCase();

			if (h.length() == 1)
				hs.append("0");

			hs.append(h);
		}

		return hs.toString();
	}

	public static byte[] hex2bytes(String hex) {
		byte[] bytes = new byte[hex.length() / 2];

		for(int i=0; i < bytes.length; i++)
			bytes[i] = (byte)Integer.parseInt(hex.substring(i*2, i*2+2), 16);

		return bytes;
	}

	public static String[] parseTelNo(String tel_no) {
		String[] part = new String[3];

		if (tel_no == null || tel_no.length() < 9)
			return part;

		if (tel_no.length() == 9) {
			part[0] = tel_no.substring(0, 2);
			part[1] = tel_no.substring(2, 5);
			part[2] = tel_no.substring(5);
		} else if (tel_no.length() == 10) {

			if (tel_no.substring(0, 2).equals("02")) {
				part[0] = tel_no.substring(0, 2);
				part[1] = tel_no.substring(2, 6);
				part[2] = tel_no.substring(6);
			} else {
				part[0] = tel_no.substring(0, 3);
				part[1] = tel_no.substring(3, 6);
				part[2] = tel_no.substring(6);
			}

		} else {

			if (tel_no.substring(0, 4).equals("0505")) {
				part[0] = tel_no.substring(0, 4);
				part[1] = tel_no.substring(4, 7);
				part[2] = tel_no.substring(7);
			} else {
				part[0] = tel_no.substring(0, 3);
				part[1] = tel_no.substring(3, 7);
				part[2] = tel_no.substring(7);
			}

		}

		return part;
	}

	public static String getStackTraceFromThrowable(Throwable t) {
		StringBuffer sb = new StringBuffer();

		try {

			if (t != null) {
//				sb.append(t.toString()).append("\n\n");
				StringWriter swriter = new StringWriter(1024);
				PrintWriter pwriter = new PrintWriter(swriter);
				t.printStackTrace(pwriter);
				pwriter.close();
				sb.append(swriter.toString());

				try {
					swriter.close();
				} catch (IOException localIOException) {
				}

			}

		} catch (Exception localException) {
		}

		return sb.toString();
	}

	public static String makeGUID() {
		UUID uuid = UUID.randomUUID();

		return uuid.toString().replaceAll("-", "");
	}

	public static void sleep(int msec) {

		try {
			Thread.sleep(msec);
		} catch (Exception e) {
		}

	}

	public static String toArrayString(String[] list) {
		if (list == null || list.length == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.length; i++)
			sb.append((i > 0) ? "," : "").append(list[i]);

		return sb.toString();
	}

	public static byte[] bindByteArray(byte[]... list) {

		if (list == null || list.length == 0)
			return null;

		int l = 0;
		for (int i = 0; i < list.length; i++)
			l += (list[i] != null) ? list[i].length : 0;

		byte[] bytes = new byte[l];
		int idx = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null)
				continue;

			System.arraycopy(list[i], 0, bytes, idx, list[i].length);
			idx += list[i].length;
		}

		return bytes;
	}

	public static String readFile(String path, String filename) {
		return new String(readFileBytes(new File(path, filename)));
	}

	public static String readFile(String filename) {
		return new String(readFileBytes(new File(filename)));
	}

	public static String readFile(File file) {
		return new String(readFileBytes(file));
	}

	public static byte[] readFileBytes(String path, String filename) {
		return readFileBytes(new File(path, filename));
	}

	public static byte[] readFileBytes(String path, String filename, int mode) {
		return readFileBytes(new File(path, filename), mode);
	}

	public static byte[] readFileBytes(String filename) {
		return readFileBytes(new File(filename));
	}

	public static byte[] readFileBytes(String filename, int mode) {
		return readFileBytes(new File(filename), mode);
	}

	public static byte[] readFileBytes(File file) {
		return readFileBytes(file, 0);
	}

	public static byte[] readFileBytes(File file, int mode) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 1024 + 16];
		int nread = 0;

		try {
			FileInputStream is = new FileInputStream(file);

			if (mode == 1)
				return AES256.encrypt(is);
			else if (mode == 2)
				return AES256.decrypt(is);

			while ((nread = is.read(buf)) != -1)
				baos.write(buf, 0, nread);

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return baos.toByteArray();
	}

	public static long readFileStream(File file, OutputStream os) {
		return readFileStream(file, os, 0);
	}

	public static long readFileStream(File file, OutputStream os, int mode) {
		byte[] buf = new byte[1024 * 1024 + 16];
		int nread = 0;
		long total_len = 0;

		try {
			FileInputStream is = new FileInputStream(file);

			if (mode == 1)
				return AES256.encrypt(is, os);
			else if (mode == 2)
				return AES256.decrypt(is, os);

			while ((nread = is.read(buf)) != -1) {
				os.write(buf, 0, nread);
				total_len += nread;
			}

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return total_len;
		}

		return total_len;
	}

	public static boolean writeFile(String filepath, String filename, byte[] bytes) {
		return writeFile(new File(filepath, filename), bytes);
	}

	public static boolean writeFile(String filepath, String filename, byte[] bytes, int mode) {
		return writeFile(new File(filepath, filename), bytes, mode);
	}

	public static boolean writeFile(String filename, byte[] bytes) {
		return writeFile(new File(filename), bytes);
	}

	public static boolean writeFile(String filename, byte[] bytes, int mode) {
		return writeFile(new File(filename), bytes, mode);
	}

	public static boolean writeFile(File f, byte[] bytes) {
		return writeFile(f, bytes, 0);
	}

	public static boolean writeFile(File f, byte[] bytes, int mode) {

		try {
			FileOutputStream fos = new FileOutputStream(f);

			if (mode == 1)
				AES256.encrypt(bytes, fos);
			else if (mode == 2)
				AES256.decrypt(bytes, fos);
			else {
				fos.write(bytes);
				fos.flush();
			}

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean copyFile(File src_file, File dest_file) {
		return copyFile(src_file, dest_file, 0);
	}

	public static boolean copyFile(File src_file, File dest_file, int mode) {
		int nread = 0;

		try {
			FileInputStream is = new FileInputStream(src_file);
			FileOutputStream os = new FileOutputStream(dest_file);

			if (mode == 1) {
				AES256.encrypt(is, os); // Stream 암호화
			} else if (mode == 2) {
				AES256.decrypt(is, os); // Stream 복호화
			} else {
				byte[] buf = new byte[1024 * 1024];
				while ((nread = is.read(buf)) != -1)
					os.write(buf, 0, nread); // Stream 복사
			}

			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
		}
	}

}
