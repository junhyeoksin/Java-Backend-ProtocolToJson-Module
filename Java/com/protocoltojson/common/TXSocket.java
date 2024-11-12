package com.protocoltojson.common;

 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.protocoltojson.crypto.SEED;



public class TXSocket implements  AutoCloseable {

    private String m_server = "localhost";
    private int m_port = 0;
    private int m_timeout = 60;
    private String m_charset = null;
    private static boolean debug = true;

    private static final int BUFFER_SIZE = 4096;

    private Socket m_socket = null;
    private BufferedInputStream m_is = null;
    private BufferedOutputStream m_os = null;

    private static String error_msg = null;

    public TXSocket(String server, int port) {
    	setServer(server, port, m_timeout, m_charset);
    }

    public TXSocket(String server, int port, String charset) {
    	setServer(server, port, m_timeout, charset);
    }

    public TXSocket(String server, int port, int timeout) {
    	setServer(server, port, timeout, m_charset);
    }

    public TXSocket(String server, int port, int timeout, String charset) {
    	setServer(server, port, timeout, charset);
    }


    // 기연결된 소켓으로 초기화
    public TXSocket(Socket socket) {
    	try {
            m_socket = socket;

            if( m_socket != null ) {
	            m_server = socket.getInetAddress().getHostAddress();
	            m_port = socket.getPort();
	            m_timeout = socket.getSoTimeout() / 1000;
            }

            m_is = new BufferedInputStream(m_socket.getInputStream(), BUFFER_SIZE);
            m_os = new BufferedOutputStream(m_socket.getOutputStream(), BUFFER_SIZE);
        }
        catch( IOException e ) {
            LogMgr.error(e);
            error_msg = e.getMessage();
        }
    }


    // 서버 접속정보를 설정
    public void setServer(String server, int port) {
    	setServer(server, port, m_timeout, m_charset);
    }

    public void setServer(String server, int port, String charset) {
    	setServer(server, port, m_timeout, charset);
    }

    public void setServer(String server, int port, int timeout) {
    	setServer(server, port, timeout, m_charset);
    }

    public void setServer(String server, int port, int timeout, String charset) {
        this.m_server = server;
        this.m_port = port;
        this.m_timeout = timeout;
        this.m_charset = charset;

        if( debug )
            LogMgr.info("SocketMgr Initialized to " + server + ":" + port + " (Timeout: " + timeout + " sec)");
    }


    // 서버접속 타임아웃 설정
    public void setTimeout(int timeout) {
        this.m_timeout = timeout;
    }

    // 한글 인코딩 설정
    public void setCharset(String charset) {
        this.m_charset = charset;
    }


    // 디버깅 모드 설정
    public void setDebug(boolean _debug) {
        debug = _debug;
    }


    // 오류메시지 리턴
    public String getErrorMsg() {
        return error_msg;
    }


    public boolean connect() {
        return connect(30);        // Default Connection Timeout : 30 sec
    }

    // 소켓 연결
    public boolean connect(int conn_timeout) {
        try {
            m_socket = new Socket();
            InetSocketAddress addr = new InetSocketAddress(this.m_server, this.m_port);

            m_socket.connect(addr, conn_timeout*1000);
            m_socket.setSoTimeout(m_timeout * 1000);

            m_is = new BufferedInputStream(m_socket.getInputStream(), BUFFER_SIZE);
            m_os = new BufferedOutputStream(m_socket.getOutputStream(), BUFFER_SIZE);

            if( debug )
                LogMgr.info("Connected " + m_socket.getLocalPort() + " to " + m_socket.getRemoteSocketAddress());
        }
        catch( IOException e ) {
            LogMgr.error(e);
            error_msg = e.getMessage();
            return false;
        }

        return true;
    }


    // 소켓 종료
    public void disconnect() {
        try {
            if( debug )
                LogMgr.info("Disconnected " + m_socket.getLocalPort() + " from " + m_socket.getRemoteSocketAddress());

            if( m_is != null ) {
                m_is.close();
                m_is = null;
            }

            if( m_os != null ) {
                m_os.flush();
                m_os.close();
                m_os = null;
            }

            if( m_socket != null ) {
                m_socket.close();
                m_socket = null;
            }

        }
        catch( IOException e ) {
            LogMgr.error(e);
            error_msg = e.getMessage();
        }
    }


    // Write String
    public boolean write(String send_msg) {
        return write(m_os, send_msg);
    }


    // Write Byte Array
    public boolean write(byte[] bytes) {
        return write(m_os, bytes);
    }


    // Read String
    public String read() {
        return read(m_is);
    }

    // Read String with Length-Width
    public String read(int wlen) {
        return read(m_is, wlen, false);
    }

    public String read(int wlen, boolean b_include) {
        return read(m_is, wlen, b_include);
    }


    // Read String in length
    public String readInLength(int len) {
        return readInLength(m_is, len);
    }


    // Read Byte Array
    public byte[] readBytes() {
        return readBytes(m_is);
    }

    // Read Byte Array with Length-Width
    public byte[] readBytes(int wlen) {
        return readBytes(m_is, wlen, false);
    }

    // Read Byte Array with Length-Width
    public byte[] readBytes(int wlen, boolean b_include) {
        return readBytes(m_is, wlen, b_include);
    }


    // Read Byte Array in length
    public byte[] readBytesInLength(int len) {
        return readBytesInLength(m_is, len);
    }


    public int available() {
    	return available(m_is);
    }


    // 1회성 전문 송수신(연결-송신-수신-종료)
    public String doMsg(String send_msg) {

        if( !connect() )
            return null;

        write(send_msg);
        String read_msg = read();

        disconnect();

        return read_msg;
    }


    // 1회성 전문 송수신(길이필드)
    public String doMsg(String send_msg, int wlen) {
        return doMsg(send_msg, wlen, false);
    }

    public String doMsg(String send_msg, int wlen, String charset) {
        return doMsg(send_msg, wlen, false);
    }

    public String doMsg(String send_msg, int wlen, boolean b_include) {

        if( !connect() )
            return null;

        write(send_msg);
        String read_msg = read(wlen, b_include);

        disconnect();

        return read_msg;
    }


    // 1회성 Byte Array 송수신(연결-송신-수신-종료)
    public byte[] doMsg(byte[] send_bytes) {

        if( !connect() )
            return null;

        write(send_bytes);
        byte[] read_bytes = readBytes();

        disconnect();

        return read_bytes;
    }

    // 1회성 Byte Array 송수신(길이필드)
    public byte[] doMsg(byte[] send_bytes, int wlen) {
        return doMsg(send_bytes, wlen, false);
    }

    // 1회성 Byte Array 송수신(길이필드,포함여부)
    public byte[] doMsg(byte[] send_bytes, int wlen, boolean b_include) {

        if( !connect() )
            return null;

        write(send_bytes);
        byte[] read_bytes = readBytes(wlen, b_include);

        disconnect();

        return read_bytes;
    }


    public byte[] doMsg(byte[]...listBytes) {

        if( !connect() )
            return null;

        for( byte[] bytes : listBytes )
        	write(bytes);

        byte[] read_bytes = readBytes();

        disconnect();

        return read_bytes;
    }


    public byte[] doMsg(int wlen, byte[]...listBytes) {
    	return doMsg(wlen, false, listBytes);
    }

    public byte[] doMsg(int wlen, boolean b_include, byte[]...listBytes) {

        if( !connect() )
            return null;

        for( byte[] bytes : listBytes )
        	write(bytes);

        byte[] read_bytes = readBytes(wlen, b_include);

        disconnect();

        return read_bytes;
    }
    

    public String doIFSMsg(String msg) {
        return doIFSMsg(msg, null);
    }

    public String doIFSMsg(String send_msg, String cypher_key) {

    	if( debug )
            LogMgr.debug("SEND:[" + send_msg + "]");

    	int head_len = UtilMgr.to_int(send_msg.substring(8, 16));
        String head_msg = UtilMgr.substring(send_msg, 0, head_len);        // 공통부 (비암호화)
        String body_msg = UtilMgr.substring(send_msg, head_len);            // 데이터부 (암호화)

        String enc_body = SEED.Encrypt(body_msg, cypher_key);        // 데이터부 메시지 암호화
        int total_len = head_len + UtilMgr.length(enc_body);        // 암호화후 전체길이

        String smsg = UtilMgr.msg_int(total_len, 8, true, true) + UtilMgr.substring(head_msg, 8) + enc_body;    // 헤더부분 전체길이값 설정

        String rmsg = doMsg(smsg, 8, true);            // 전문 송수신
        String read_msg = null;

        if( rmsg != null && rmsg.length() > 0 ) {
            head_len = UtilMgr.to_int(rmsg.substring(8, 16));
            head_msg = UtilMgr.substring(rmsg, 0, head_len);	// 응답전문 비암호화 부분
            enc_body = UtilMgr.substring(rmsg, head_len);		// 응답전문 암호화 부분

            body_msg = SEED.Decrypt(enc_body, cypher_key);
            total_len = head_len + UtilMgr.length(body_msg);

            read_msg = head_msg + body_msg;
        }
        else
            error_msg = "Read Timeout";

        if( debug )
            LogMgr.debug("RECV:[" + read_msg + "]");

        return read_msg;
    }



    // Static IO Routines ==============================================================

	// Write String
	public static boolean write(OutputStream os, String send_msg) {
		return write(os, send_msg, "EUC-KR");
	}

	public static boolean write(OutputStream os, String send_msg, String charset) {

		byte[] bytes = UtilMgr.getBytes(send_msg, charset);

		if( debug )
			LogMgr.debug("TXSocket.write():[" + send_msg + "]");

		return write(os, bytes);
	}


	// Write Byte Array
	public static boolean write(OutputStream os, byte[] bytes) {

		if( os == null )
			return false;

		try {
			os.write(bytes);
			os.flush();
		}
		catch( IOException e ) {
			LogMgr.error(e);
			error_msg = e.getMessage();
			return false;
		}

		return true;
	}


	// Read String
	public static String read(InputStream is) {
		return read(is, "EUC-KR");
	}

	public static String read(InputStream is, String charset) {

		if( is == null )
			return null;

		byte[] bytes = readBytes(is);

		if( bytes == null )
			return null;

		String read_msg = UtilMgr.toString(bytes, charset);

		if( debug )
			LogMgr.debug("TXSocket.read():[" + read_msg + "]");

		return read_msg;
	}

	
	// Read String with Length-Width
	public static String read(InputStream is, int wlen, boolean b_include) {
		return read(is, wlen, b_include, "EUC-KR");
	}

	public static String read(InputStream is, int wlen, boolean b_include, String charset) {

		if( is == null )
			return null;

		byte[] bytes = readBytes(is, wlen, b_include);

		if( bytes == null )
			return null;

		String read_msg = UtilMgr.toString(bytes, charset);

		if( debug )
			LogMgr.debug("TXSocket.read():[" + read_msg + "]");

		return read_msg;
	}


	// Read String in Length
	public static String readInLength(InputStream is, int len) {
		return readInLength(is, len, "EUC-KR");
	}

	public static String readInLength(InputStream is, int len, String charset) {

		byte[] bytes = readBytesInLength(is, len);
		
		if( bytes == null )
			return null;

		String read_msg = UtilMgr.toString(bytes, charset);

		if( debug )
			LogMgr.debug("TXSocket.readInLength(" + len + "):[" + read_msg + "]");

		return read_msg;
	}



	// Read Byte Array
	public static byte[] readBytes(InputStream is) {

		if( is == null )
			return null;

		byte[] buf = new byte[BUFFER_SIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nread = 0;

		try {

			while( (nread = is.read(buf)) != -1 ) {
				baos.write(buf, 0, nread);
			}

		}
		catch( IOException e ) {
			LogMgr.error(e);
			error_msg = e.getMessage();
		}

		return baos.toByteArray();
	}

	// Read Byte Array with Length-Width
	public static byte[] readBytes(InputStream is, int wlen, boolean b_include) {

		if( is == null )
			return null;

		byte[] buf = new byte[BUFFER_SIZE];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nread = 0;
		int read_len = 0;
		int msglen = 0;

		try {

			while( (nread = is.read(buf)) != -1 ) {
				baos.write(buf, 0, nread);
				read_len += nread;

				if( msglen == 0 && read_len >= wlen ) {
					String lenstr = baos.toString().substring(0, wlen);
					msglen = UtilMgr.to_int(lenstr) + (b_include ? 0 : wlen);
				}

				if( msglen > 0 && read_len >= msglen )
					break;
			}

		}
		catch( IOException e ) {
			LogMgr.error(e);
			error_msg = e.getMessage();
		}

		return baos.toByteArray();
	}

	// Read Byte Array in Length
	public static byte[] readBytesInLength(InputStream is, int len) {

		if( is == null )
			return null;

		byte[] buf = new byte[len];
		int nread = 0;
		int readlen = 0;

		try {

			while( (nread = is.read(buf, readlen, len - readlen)) != -1 ) {
				readlen += nread;

				if( readlen >= len )
					break;
			}

		}
		catch( IOException e ) {
			LogMgr.error(e);
			error_msg = e.getMessage();
		}

		return buf;
	}


	public static int available(InputStream is) {

		if( is == null )
			return -1;

		int result = -1;

		try {
			result = is.available();
		}
		catch( IOException e ) {
			LogMgr.error(e);
			error_msg = e.getMessage();
			return -1;
		}

		return result;
	}
	
    @Override
    public void close() {
        try {
            if (m_os != null) {
                m_os.close();
            }
            if (m_is != null) {
                m_is.close();
            }
            if (m_socket != null && !m_socket.isClosed()) {
                m_socket.close();
            }
        } catch (IOException e) {
            LogMgr.error(e);
        }
    }


}
