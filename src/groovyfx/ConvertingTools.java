package groovyfx;

import java.util.ArrayList;
import java.util.HashSet;

public class ConvertingTools {
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	/**
     * Finds the first occurrence of the pattern in the text.
     */
    public static int indexOf(byte[] data, byte[] pattern, int startPos) {
        int[] failure = computeFailure(pattern);

        int j = 0;
        if (data.length == 0) return -1;

        for (int i = startPos; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) { j++; }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static ArrayList<String> removeDuplicates(ArrayList<String> list) {

    	// Store unique items in result.
    	ArrayList<String> result = new ArrayList<>();

    	// Record encountered Strings in HashSet.
    	HashSet<String> set = new HashSet<>();

    	// Loop over argument list.
    	for (String item : list) {
    	    // If String is not is set, add it to the list and the set.
    	    if (!set.contains(item)) {
    		result.add(item);
    		set.add(item);
    	    }
    	}
    	return result;
    }
    
    public static byte[] connectByteArray(byte[] b1, byte[] b2, byte[] b3){
    	byte[] add = new byte[b1.length + b2.length + b3.length];
    	
    	System.arraycopy(b1, 0, add, 0, b1.length);
    	System.arraycopy(b2, 0, add, b1.length, b2.length);
    	System.arraycopy(b3, 0, add, b1.length+b2.length, b3.length);
    	return add;
    }

}
