/**
 * This file is part of Port@l
 * Port@l 3.0 - Portal Engine and Management System
 * Copyright (C) 2010  Isotrol, SA.  http://www.isotrol.com
 *
 * Port@l is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Port@l is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Port@l.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.isotrol.impe3.tomcat7;


import java.io.IOException;
import java.io.InputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


/**
 * Encryption helper class.
 * @author Andres Rodriguez.
 */
public final class Encrypter {
	private static final String KEY = "encrypter.key";
	private static final byte[] SALT = {(byte) 0xB9, (byte) 0x56, (byte) 0xC8, (byte) 0x23, (byte) 0x66, (byte) 0x37,
		(byte) 0xE4, (byte) 0x02};
	private static final int ITERATION_COUNT = 19;

	private static Support support = null;

	private static String loadKey() throws IOException {
		final InputStream is = Encrypter.class.getResourceAsStream("encrypter.properties");
		try {
			Properties p = new Properties();
			p.load(is);
			String key = p.getProperty(KEY);
			if (key == null || key.length() == 0) {
				throw new RuntimeException("Unable to find encrypter key");
			}
			return key;
		} catch (IOException e) {
			throw new IOException("Unable to load encrypter key", e);
		}
	}

	private static Support init() throws Exception {
		if (support == null) {
			support = new Support(loadKey());
		}
		return support;
	}

	static synchronized String encrypt(String message) throws Exception {
		return init().encrypt(message);
	}

	static synchronized String decrypt(String message) throws Exception {
		return init().decrypt(message);
	}

	public static void main(String[] args) throws Exception {
		final StringBuilder b = new StringBuilder("Tokens:");
		for (int i = 0; i < args.length; i++) {
			b.append(' ').append('[').append(encrypt(args[i])).append(']');
		}
		System.out.printf("%s\n", b.toString());
	}

	private static class Support {
		private final Cipher ecipher;
		private final Cipher dcipher;

		Support(String password) throws Exception {
			try {
				// Create the key
				PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
				ecipher = Cipher.getInstance(key.getAlgorithm());
				dcipher = Cipher.getInstance(key.getAlgorithm());

				// Prepare the parameter to the ciphers
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

				// Create the ciphers
				ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
				dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			} catch (Exception e) {
				throw new Exception("Unable to create ciphers", e);
			}
		}

		@SuppressWarnings("restriction")
		public String encrypt(String str) throws Exception {
			try {
				byte[] utf8 = str.getBytes("UTF8");
				byte[] enc = ecipher.doFinal(utf8);
				return new sun.misc.BASE64Encoder().encode(enc);
			} catch (Exception e) {
				throw new Exception("Unable to perform encryption", e);
			}
		}

		@SuppressWarnings("restriction")
		public String decrypt(String str) throws Exception {
			try {
				// Decode base64 to get bytes
				byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

				// Decrypt
				byte[] utf8 = dcipher.doFinal(dec);

				// Decode using utf-8
				return new String(utf8, "UTF8");
			} catch (Exception e) {
				throw new Exception("Unable to perform encryption", e);
			}

		}

	}

}
