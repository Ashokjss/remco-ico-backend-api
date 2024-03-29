package com.remco.ico.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptDecrypt {

	private static String encryptionKey = "MZygpewJsCpRrfOr";

	// public EncryptDecrypt(String encryptionKey)
	// {
	// this.encryptionKey = encryptionKey;
	// }

	public static String encrypt(String plainText) throws Exception {
		Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

		return Base64.encodeBase64String(encryptedBytes);
	}

	public static String decrypt(String encrypted) throws Exception {
		Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
		byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

		return new String(plainBytes);
	}

	private static Cipher getCipher(int cipherMode) throws Exception {
		String encryptionAlgorithm = "AES";
		SecretKeySpec keySpecification = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
		Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
		cipher.init(cipherMode, keySpecification);

		return cipher;
	}

	public static String sha256(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] arguments) throws Exception {
		String encryptionKey = "2LDg6IsspVhavdzGTfTV+Q==";
//		String plainText = "20";
		EncryptDecrypt encryptDecrypt = new EncryptDecrypt();
//		String cipherText = EncryptDecrypt.encrypt(plainText);
		String decryptedCipherText = EncryptDecrypt.decrypt(encryptionKey);
//		String encrypt256 = EncryptDecrypt.sha256(plainText);
		
//		System.out.println("Encrypt with sha256---->"+encrypt256);
//
//		System.out.println("Decrypted wallt file-------->" + decryptedCipherText);

//		System.out.println("encrypt" + plainText);
//		System.out.println(cipherText);
		System.out.println("decrypt" + decryptedCipherText);
	}
}