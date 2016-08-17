package lasad.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Converter {
	/**
	 * Converts String to MD5 String
	 */
	public static String toMD5(String plain){
		
		if(plain != null) {
			MessageDigest digest;
			try {
			
				digest = java.security.MessageDigest.getInstance("MD5");
			
				digest.update(plain.getBytes());
				// Convert Byte Array to Hex String and return
				return convertToHex(digest.digest());
		    
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	    return ""; // Should never be reached
	}

	/**
	 * Converts a Byte Array in to a Hex String
	 * 
	 * There is a special case for 0 and for a
	 * 
	 * @param byte[]
	 * @return String
	 */
	  private static String convertToHex(byte[] data) {
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < data.length; i++) {
	            int halfbyte = (data[i] >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do {
	                if ((0 <= halfbyte) && (halfbyte <= 9))
	                    buf.append((char) ('0' + halfbyte));
	                else
	                    buf.append((char) ('a' + (halfbyte - 10)));
	                halfbyte = data[i] & 0x0F;
	            } while(two_halfs++ < 1);
	        }
	        return buf.toString();
	    }
}
