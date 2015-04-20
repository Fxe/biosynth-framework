package pt.uminho.sysbio.biosynthframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class DigestUtils {
	
	public static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
			throws NoSuchAlgorithmException, IOException {

		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			md.update(bytes, 0, numBytes);
		}
		byte[] digest = md.digest();
		String result = new String(Hex.encodeHex(digest));
		return result;
	}
	
	public static long hash(String str) {
		long hash = 1987L;
		final long prime = 2011L;
		int len = str.length();
		
		for (int i = 0; i < len; i++)
			hash += prime *hash + str.charAt(i);
		
		return hash;
	}
}
