package com.protocolToJson.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.protocolToJson.common.Env;
import com.protocolToJson.common.UtilMgr;

public class AES256 {

	private static final String CIPHER = "AES";
	private static final String TRANSFORM = "AES/CBC/PKCS5Padding";
	private static final int BLOCK_SIZE = 1024 * 1024;
	private static final String BASE_CHARSET = "UTF-8";

	private static String DEFAULT_KEY = Env.get("cipher.aes256.key", "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456");

	// Encrypt for bytes
	public static byte[] encrypt(byte[] srcBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return encrypt(srcBytes, DEFAULT_KEY);
	}

	public static byte[] encrypt(byte[] srcBytes, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return encrypt(srcBytes, key, key.substring(0,16));
	}

	public static byte[] encrypt(byte[] srcBytes, String key, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return encrypt(srcBytes, key == null ? null : key.getBytes(), iv == null ? null : iv.getBytes());
	}

	public static byte[] encrypt(byte[] srcBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return encrypt(srcBytes, keyBytes, null);
	}

	public static byte[] encrypt(byte[] srcBytes, byte[] keyBytes, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if( srcBytes == null )
			return null;

		if( keyBytes == null || keyBytes.length == 0 )
			keyBytes = DEFAULT_KEY.getBytes();
		 
		Cipher cipher = Cipher.getInstance(TRANSFORM);

		if( ivBytes == null || ivBytes.length == 0 )
			ivBytes = Arrays.copyOfRange(keyBytes, 0, cipher.getBlockSize());

		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, CIPHER);

		cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

		return cipher.doFinal(srcBytes);
	}


	// Encrypt for String (to BASE64)
	public static String encrypt(String srcText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encrypt(srcText, DEFAULT_KEY);
	}

	public static String encrypt(String srcText, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encrypt(srcText, key, key.substring(0,16));
	}

	public static String encrypt(String srcText, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encrypt(srcText, keyBytes, null);
	}

	public static String encrypt(String srcText, String key, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return encrypt(srcText, key == null ? null : key.getBytes(), iv == null ? null : iv.getBytes());
	}

	public static String encrypt(String srcText, byte[] keyBytes, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

		if( srcText == null )
			return null;

		return Base64.encodeBase64String(encrypt(srcText.getBytes(BASE_CHARSET), keyBytes, ivBytes));
	}


	public static long encrypt(InputStream is, OutputStream os) {
		byte[] buffer = new byte[BLOCK_SIZE];
		int nread = 0;
		long total_len = 0;

		try {

			while( (nread = is.read(buffer)) != -1 ) {
				byte[] encBytes = encrypt(nread < BLOCK_SIZE ? Arrays.copyOf(buffer, nread) : buffer);
				os.write(encBytes);
				total_len += encBytes.length;
			}

			os.flush();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return total_len;
	}

	public static int encrypt(byte[] srcBytes, OutputStream os) {
		int nread = 0;
		int src_len = srcBytes.length;
		int total_len = 0;

		try {

			for(int i = 0; i < src_len; i += BLOCK_SIZE) {
				nread = ((src_len - i) < BLOCK_SIZE) ? (src_len - i) : BLOCK_SIZE;
				byte[] encBytes = encrypt(Arrays.copyOfRange(srcBytes, i, i + nread));
				os.write(encBytes);
				total_len += encBytes.length;
			}

			os.flush();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return total_len;
	}

	public static byte[] encrypt(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BLOCK_SIZE];
		int nread = 0;

		try {

			while( (nread = is.read(buffer)) != -1 ) {
				byte[] encBytes = encrypt(nread == BLOCK_SIZE ? buffer : Arrays.copyOf(buffer, nread));
				baos.write(encBytes);
			}

		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}


	// Decrypt for bytes
	public static byte[] decrypt(byte[] encBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return decrypt(encBytes, DEFAULT_KEY, null);
	}

	public static byte[] decrypt(byte[] encBytes, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return decrypt(encBytes, key, null);
	}

	public static byte[] decrypt(byte[] encBytes, String key, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return decrypt(encBytes, key == null ? null : key.getBytes(), iv == null ? null : iv.getBytes());
	}

	public static byte[] decrypt(byte[] encBytes, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		return decrypt(encBytes, keyBytes, null);
	}

	public static byte[] decrypt(byte[] encBytes, byte[] keyBytes, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		if( keyBytes == null || keyBytes.length == 0 )
			keyBytes = DEFAULT_KEY.getBytes();

		Cipher cipher = Cipher.getInstance(TRANSFORM);

		if( ivBytes == null || ivBytes.length == 0 )
			ivBytes = Arrays.copyOfRange(keyBytes, 0, cipher.getBlockSize());

		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec newKey = new SecretKeySpec(keyBytes, CIPHER);

		cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

		return cipher.doFinal(encBytes);
	}

	// Decryption for String (from BASE64)
	public static String decrypt(String encText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return decrypt(encText, DEFAULT_KEY, null);
	}

	public static String decrypt(String encText, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return decrypt(encText, key, null);
	}

	public static String decrypt(String encText, byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return decrypt(encText, keyBytes, null);
	}

	public static String decrypt(String encText, String key, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return decrypt(encText, key == null ? null : key.getBytes(), iv == null ? null : iv.getBytes());
	}

	public static String decrypt(String encText, byte[] keyBytes, byte[] ivBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		return new String(decrypt(Base64.decodeBase64(encText), keyBytes, ivBytes), BASE_CHARSET);
	}


	public static long decrypt(InputStream is, OutputStream os) {
		byte[] buffer = new byte[BLOCK_SIZE + 16];
		int nread = 0;
		long total_len = 0;

		try {

			while( (nread = is.read(buffer)) != -1 ) {
				byte[] decBytes = decrypt(nread == (BLOCK_SIZE + 16) ? buffer : Arrays.copyOf(buffer, nread));
				os.write(decBytes);
				total_len += decBytes.length;
			}

			os.flush();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return total_len;
	}

	public static long decrypt(byte[] encBytes, OutputStream os) {
		int nread = 0;
		int src_len = encBytes.length;
		int total_len = 0;

		try {

			for(int i = 0; i < src_len; i += (BLOCK_SIZE + 16)) {
				nread = ((src_len - i) < (BLOCK_SIZE + 16)) ? (src_len - i) : (BLOCK_SIZE + 16);
				byte[] decBytes = decrypt(Arrays.copyOfRange(encBytes, i, i + nread));
				os.write(decBytes);
				total_len += decBytes.length;
			}

			os.flush();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return total_len;
	}

	public static byte[] decrypt(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BLOCK_SIZE + 16];
		int nread = 0;

		try {

			while( (nread = is.read(buffer)) != -1 ) {
				byte[] decBytes = decrypt(nread == (BLOCK_SIZE + 16) ? buffer : Arrays.copyOf(buffer, nread));
				baos.write(decBytes);
			}

		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	public static String toString(byte[] bytes) {
		return new String(bytes);
	}

	public static byte[] toBytes(String text) {
		return text.getBytes();
	}

	public static void main(String[] args) throws Exception {

		try {

			File d = new File("C:/nTreeWorks64/src");
			File[] fl = d.listFiles();

			long tm0 = System.currentTimeMillis();

			for(int i = 0; i < fl.length; i++) {
				File f = fl[i];
				FileOutputStream fos = new FileOutputStream("C:/nTreeWorks64/dest/" + f.getName());

				encrypt(new FileInputStream(f), fos);

				fos.close();
			}

			System.out.println("1: " + (System.currentTimeMillis() - tm0) + "");

			tm0 = System.currentTimeMillis();

			for(int i = 0; i < fl.length; i++) {
				File f = fl[i];

				byte[] bt = UtilMgr.readFileBytes(f);
				byte[] enc = encrypt(bt);

				FileOutputStream fos = new FileOutputStream("C:/nTreeWorks64/dest2/" + f.getName());
				fos.write(enc);
				fos.flush();
				fos.close();
			}

			System.out.println("2: " + (System.currentTimeMillis() - tm0) + "");

		}
		catch( Exception e ) {
			e.printStackTrace();
		}

	}
}
