package com.doudou.passenger.utils.secure;

import android.annotation.SuppressLint;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DES {
    private static final byte[] DESkey = "zmf1234".getBytes();// 设置密钥
	private static final byte[] DESIV = "4321zmf".getBytes();// 设置向量

	@SuppressLint("TrulyRandom")
	public static String encode(String data) throws Exception {
		DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
		AlgorithmParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
		Key key = keyFactory.generateSecret(keySpec);// 得到密钥对象

		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
		enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
		byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
		return Base64.encode(pasByte);
	}

	public static String decode(String data) throws Exception {
		DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
		AlgorithmParameterSpec iv = new IvParameterSpec(DESIV);// 设置向量
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
		Key key = keyFactory.generateSecret(keySpec);// 得到密钥对象

		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		deCipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] pasByte = deCipher.doFinal(Base64.decode(data));
		return new String(pasByte, "UTF-8");
	}
}
