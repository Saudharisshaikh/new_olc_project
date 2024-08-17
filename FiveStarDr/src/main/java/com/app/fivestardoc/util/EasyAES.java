package com.app.fivestardoc.util;

import android.util.Base64;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES åŠ è§£å¯†
 *
 * @author pkuoliver
 * @see Base64
 */
public class EasyAES {
 
	//-----ç±»åˆ«å¸¸æ•°-----
	/**
	 * é¢„è®¾çš„Initialization Vectorï¼Œä¸º16 Bitsçš„0
	 */
	private static final IvParameterSpec DEFAULT_IV = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
	/**
	 * åŠ å¯†æ¼”ç®—æ³•ä½¿ç”¨AES
	 */
	private static final String ALGORITHM = "AES";
	/**
	 * AESä½¿ç”¨CBCæ¨¡å¼�ä¸ŽPKCS5Padding
	 */
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
 
	//-----æˆ�å‘˜å�˜é‡�-----
	/**
	 * å�–å¾—AESåŠ è§£å¯†çš„ç§˜é’¥
	 */
	private Key key;
	/**
	 * AES CBCæ¨¡å¼�ä½¿ç”¨çš„Initialization Vector
	 */
	private IvParameterSpec iv;
	/**
	 * Cipher ç‰©ä»¶
	 */
	private Cipher cipher;
 
	/**
	 * æž„é€ å‡½æ•°ï¼Œä½¿ç”¨128 Bitsçš„AESç§˜é’¥(è®¡ç®—ä»»æ„�é•¿åº¦ç§˜é’¥çš„MD5)å’Œé¢„è®¾IV
	 *
	 * @param key ä¼ å…¥ä»»æ„�é•¿åº¦çš„AESç§˜é’¥
	 */
	public EasyAES(final String key) {
		this(key, 128);
	}
 
	/**
	 * æž„é€ å‡½æ•°ï¼Œä½¿ç”¨128 Bitsæˆ–æ˜¯256 Bitsçš„AESç§˜é’¥(è®¡ç®—ä»»æ„�é•¿åº¦ç§˜é’¥çš„MD5æˆ–æ˜¯SHA256)å’Œé¢„è®¾IV
	 *
	 * @param key ä¼ å…¥ä»»æ„�é•¿åº¦çš„AESç§˜é’¥
	 * @param bit ä¼ å…¥AESç§˜é’¥é•¿åº¦ï¼Œæ•°å€¼å�¯ä»¥æ˜¯128ã€�256 (Bits)
	 */
	public EasyAES(final String key, final int bit) {
		this(key, bit, null);
	}
 
	/**
	 * æž„é€ å‡½æ•°ï¼Œä½¿ç”¨128 Bitsæˆ–æ˜¯256 Bitsçš„AESç§˜é’¥(è®¡ç®—ä»»æ„�é•¿åº¦ç§˜é’¥çš„MD5æˆ–æ˜¯SHA256)ï¼Œç”¨MD5è®¡ç®—IVå€¼
	 *
	 * @param key ä¼ å…¥ä»»æ„�é•¿åº¦çš„AESç§˜é’¥
	 * @param bit ä¼ å…¥AESç§˜é’¥é•¿åº¦ï¼Œæ•°å€¼å�¯ä»¥æ˜¯128ã€�256 (Bits)
	 * @param iv ä¼ å…¥ä»»æ„�é•¿åº¦çš„IVå­—ä¸²
	 */
	public EasyAES(final String key, final int bit, final String iv) {
		if (bit == 256) {
			this.key = new SecretKeySpec(getHash("SHA-256", key), ALGORITHM);
		} else {
			this.key = new SecretKeySpec(getHash("MD5", key), ALGORITHM);
		}
		if (iv != null) {
			this.iv = new IvParameterSpec(getHash("MD5", iv));
		} else {
			this.iv = DEFAULT_IV;
		}
 
		init();
	}
 
	//-----ç‰©ä»¶æ–¹æ³•-----
	/**
	 * å�–å¾—å­—ä¸²çš„Hashå€¼
	 *
	 * @param algorithm ä¼ å…¥æ•£åˆ—ç®—æ³•
	 * @param text ä¼ å…¥è¦�æ•£åˆ—çš„å­—ä¸²
	 * @return ä¼ å›žæ•£åˆ—å�Žå…§å®¹
	 */
	private static byte[] getHash(final String algorithm, final String text) {
		try {
			return getHash(algorithm, text.getBytes("UTF-8"));
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
 
	/**
	 * å�–å¾—èµ„æ–™çš„Hashå€¼
	 *
	 * @param algorithm ä¼ å…¥æ•£åˆ—ç®—æ³•
	 * @param data ä¼ å…¥è¦�æ•£åˆ—çš„èµ„æ–™
	 * @return ä¼ å›žæ•£åˆ—å�Žå…§å®¹
	 */
	private static byte[] getHash(final String algorithm, final byte[] data) {
		try {
			final MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(data);
			return digest.digest();
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
 
	/**
	 * åˆ�å§‹åŒ–
	 */
	private void init() {
		try {
			cipher = Cipher.getInstance(TRANSFORMATION);
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
 
	/**
	 * åŠ å¯†æ–‡å­—
	 *
	 * @param str ä¼ å…¥è¦�åŠ å¯†çš„æ–‡å­—
	 * @return ä¼ å›žåŠ å¯†å�Žçš„æ–‡å­—
	 */
	public String encrypt(final String str) {
		try {
			return encrypt(str.getBytes("UTF-8"));
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}
 
	/**
	 * åŠ å¯†èµ„æ–™
	 *
	 * @param data ä¼ å…¥è¦�åŠ å¯†çš„èµ„æ–™
	 * @return ä¼ å›žåŠ å¯†å�Žçš„èµ„æ–™
	 */
	public String encrypt(final byte[] data) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			final byte[] encryptData = cipher.doFinal(data);
			return new String(Base64.encode(encryptData, Base64.DEFAULT), "UTF-8");
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
 
	/**
	 * è§£å¯†æ–‡å­—
	 *
	 * @param str ä¼ å…¥è¦�è§£å¯†çš„æ–‡å­—
	 * @return ä¼ å›žè§£å¯†å�Žçš„æ–‡å­—
	 */
	public String decrypt(final String str) {
		try {
			return decrypt(Base64.decode(str, Base64.DEFAULT));
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
 
	/**
	 * è§£å¯†æ–‡å­—
	 *
	 * @param data ä¼ å…¥è¦�è§£å¯†çš„èµ„æ–™
	 * @return ä¼ å›žè§£å¯†å�Žçš„æ–‡å­—
	 */
	public String decrypt(final byte[] data) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			final byte[] decryptData = cipher.doFinal(data);
			return new String(decryptData, "UTF-8");
		} catch (final Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public static String encryptString(String content) {
		//è¿™é‡Œå¡«å†™å¯†ç �å’Œivå­—ç¬¦ä¸²ï¼Œæ³¨æ„�è¦�ç¡®ä¿�16ä½�çš„
		//EasyAES ea = new EasyAES("****************", 128, "################");
		EasyAES ea = new EasyAES("olc2017", 256);
		//EasyAES ea = new EasyAES("olc2017");
		return ea.encrypt(content);
	}
	
	public static String decryptString(String content) {
        //String result = null;
		String result = "";
		try {
			//è¿™é‡Œå¡«å†™å¯†ç �å’Œivå­—ç¬¦ä¸²ï¼Œæ³¨æ„�è¦�ç¡®ä¿�16ä½�çš„
			//EasyAES ea = new EasyAES("****************", 128, "################");
			EasyAES ea = new EasyAES("olc2017", 256);
			//EasyAES ea = new EasyAES("olc2017");
			result = ea.decrypt(content);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	
	/*public static void main(String[] args) {
		String original = "Hi zohaib kaise ho? ye mera email hai mustafa@gmail.com . JBL bilo. ";
        DATA.print("Before Encryption   : " + original);
        String encrypted = encryptString(original);
        DATA.print("After Encryption   : " + encrypted);
        String decrypted = decryptString(encrypted);
        DATA.print("After Decryption   : " + decrypted);
	}*/
}