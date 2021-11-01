package com.netease.util;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

public final class SecretUtil {

	public static void main(String[] args) throws Exception{
		System.out.println(saltyMd5("aaaa", "afadfa"));
	}

	public static String saltyMd5(final String salt, final String data) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			// 一次MD5
			byte[] dataBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
			// 二次MD5
			byte[] siteBytes = salt.getBytes(StandardCharsets.UTF_8);
			for (int i = 0; i < dataBytes.length && i < siteBytes.length; i++) {
				dataBytes[i] = (byte) (dataBytes[i] ^ siteBytes[i]);
			}
			return Hex.encodeHexString(dataBytes);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String genLoginToken(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
