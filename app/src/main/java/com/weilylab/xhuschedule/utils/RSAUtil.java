/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA 工具类。提供加密，解密，生成密钥对等方法。
 */
public class RSAUtil {
	public static final String PUBLIC_KEY = "publicKey";
	public static final String PRIVATE_KEY = "privateKey";
	private static final String CHARSET = "UTF-8";
	private static final String KEY_ALGORITHM = "RSA";

	/**
	 * * 生成密钥对 *
	 *
	 * @param keySize key size
	 * @return KeyPair 密钥对
	 * @throws NoSuchAlgorithmException NoSuchAlgorithmException
	 */
	public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(keySize, new SecureRandom());
		return keyPairGen.generateKeyPair();

	}

	/**
	 * map中的公钥和私钥都经过base64编码
	 *
	 * @param keySize size of key
	 * @return map
	 */
	public static Map<String, String> createKeys(int keySize) {
		try {
			KeyPair keyPair = RSAUtil.generateKeyPair(keySize);
			Key publicKey = keyPair.getPublic();
			String publicKeyStr = new String(Base64.getUrlEncoder().encode(publicKey.getEncoded()), CHARSET);
			Key privateKey = keyPair.getPrivate();
			String privateKeyStr = new String(Base64.getUrlEncoder().encode(privateKey.getEncoded()), CHARSET);
			Map<String, String> keyPairMap = new HashMap<>();
			keyPairMap.put(PUBLIC_KEY, publicKeyStr);
			keyPairMap.put(PRIVATE_KEY, privateKeyStr);
			return keyPairMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据以保存的私密对已加密的字符串进行解密
	 *
	 * @param plaintext  待解密的字符串
	 * @param privateKey 字符串私钥
	 * @return 返回解密后对的字符串
	 */
	public static String decryptString(String plaintext, String privateKey) {
		return RSAUtil.privateDecrypt(plaintext, RSAUtil.getPrivateKey(privateKey));
	}


	/**
	 * 根据公匙加密字符串
	 *
	 * @param plaintext 待加密的字符串
	 * @param publicKey 字符串公钥
	 * @return String
	 */
	public static String encryptString(String plaintext, String publicKey) {
		if (publicKey == null || plaintext == null) {
			return null;
		}
		return RSAUtil.publicEncrypt(plaintext, RSAUtil.getPublicKey(publicKey));
	}


	/**
	 * 得到公钥
	 *
	 * @param publicKey 密钥字符串（经过base64编码）
	 * @return RSAPublicKey
	 */
	public static RSAPublicKey getPublicKey(String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getUrlDecoder().decode(publicKey));
			return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到私钥
	 *
	 * @param privateKey 密钥字符串（经过base64编码）
	 * @return RSAPrivateKey
	 */
	public static RSAPrivateKey getPrivateKey(String privateKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getUrlDecoder().decode(privateKey));
			return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥加密
	 *
	 * @param plaintext 待加密的字符串
	 * @param publicKey 字符串公钥
	 * @return String
	 */
	public static String publicEncrypt(String plaintext, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return new String(Base64.getUrlEncoder().encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, plaintext.getBytes(CHARSET),
					publicKey.getModulus().bitLength())), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("An exception occurred while encrypting the string [" + plaintext + "]", e);
		}
	}

	/**
	 * 私钥解密
	 *
	 * @param plaintext  待加密的字符串
	 * @param privateKey 字符串公钥
	 * @return String
	 */

	public static String privateDecrypt(String plaintext, RSAPrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getUrlDecoder().decode(plaintext),
					privateKey.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("An exception occurred while decrypting the string [" + plaintext + "]", e);
		}
	}

	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			while (datas.length > offSet) {
				if (datas.length - offSet > maxBlock) {
					buff = cipher.doFinal(datas, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(datas, offSet, datas.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new RuntimeException("An exception occurred when the encryption and decryption threshold was [" + maxBlock + "]", e);
		}
		byte[] resultDatas = out.toByteArray();
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultDatas;
	}
}
