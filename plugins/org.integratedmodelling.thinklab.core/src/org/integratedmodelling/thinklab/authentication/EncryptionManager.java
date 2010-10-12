/**
 * EncryptionManager.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabAuthenticationPlugin.
 * 
 * ThinklabAuthenticationPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabAuthenticationPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.authentication;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.integratedmodelling.thinklab.exception.ThinklabEncryptionException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptionManager {

	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

	public static final String DES_ENCRYPTION_SCHEME = "DES";
	public static final String AES_ENCRYPTION_SCHEME = "AES";

	public static final String DEFAULT_ENCRYPTION_KEY = 
		"m'illumino \n d'immenso";

	private KeySpec keySpec;

	private SecretKeyFactory keyFactory;

	private Cipher cipher;

	private static final String UNICODE_FORMAT = "UTF8";

	public EncryptionManager() throws ThinklabEncryptionException {
		this(AES_ENCRYPTION_SCHEME, DEFAULT_ENCRYPTION_KEY);
	}
	
	public EncryptionManager(String encryptionScheme)
			throws ThinklabEncryptionException {
		this(encryptionScheme, DEFAULT_ENCRYPTION_KEY);
	}

	public EncryptionManager(String encryptionScheme, String encryptionKey)
			throws ThinklabEncryptionException {

		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		
		if (encryptionKey == null)
			throw new IllegalArgumentException("encryption key was null");
		if (encryptionKey.trim().length() < 24)
			throw new IllegalArgumentException(
					"encryption key was less than 24 characters");

		try {
			byte[] keyAsBytes = encryptionKey.getBytes(UNICODE_FORMAT);

			if (encryptionScheme.equals(DESEDE_ENCRYPTION_SCHEME)) {
				keySpec = new DESedeKeySpec(keyAsBytes);
			} else if (encryptionScheme.equals(DES_ENCRYPTION_SCHEME)) {
				keySpec = new DESKeySpec(keyAsBytes);
			} else {

				keySpec = new SecretKeySpec(keyAsBytes, encryptionKey);

//				throw new IllegalArgumentException(
//						"Encryption scheme not supported: " + encryptionScheme);
			}

			keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
			cipher = Cipher.getInstance(encryptionScheme);

		} catch (InvalidKeyException e) {
			throw new ThinklabEncryptionException(e);
		} catch (UnsupportedEncodingException e) {
			throw new ThinklabEncryptionException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new ThinklabEncryptionException(e);
		} catch (NoSuchPaddingException e) {
			throw new ThinklabEncryptionException(e);
		}

	}

	public String encrypt(String unencryptedString) throws ThinklabEncryptionException {
		if (unencryptedString == null || unencryptedString.trim().length() == 0)
			throw new IllegalArgumentException(
					"unencrypted string was null or empty");

		try {
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cleartext = unencryptedString.getBytes(UNICODE_FORMAT);
			byte[] ciphertext = cipher.doFinal(cleartext);

			BASE64Encoder base64encoder = new BASE64Encoder();
			return base64encoder.encode(ciphertext);
		} catch (Exception e) {
			throw new ThinklabEncryptionException(e);
		}
	}

	public String decrypt(String encryptedString) throws ThinklabEncryptionException {
		if (encryptedString == null || encryptedString.trim().length() <= 0)
			throw new IllegalArgumentException(
					"encrypted string was null or empty");

		try {
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.DECRYPT_MODE, key);
			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] cleartext = base64decoder.decodeBuffer(encryptedString);
			byte[] ciphertext = cipher.doFinal(cleartext);

			return bytes2String(ciphertext);
		} catch (Exception e) {
			throw new ThinklabEncryptionException(e);
		}
	}

	private static String bytes2String(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			stringBuffer.append((char) bytes[i]);
		}
		return stringBuffer.toString();
	}
}