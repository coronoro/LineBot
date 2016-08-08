package line.util;

public class EncodingUtil {

	private EncodingUtil(){}
	
	public static String convertToHex(byte cipherbyte[]){
	    StringBuffer stringbuffer = new StringBuffer(cipherbyte.length * 2);
	    for (int i = 0; i < cipherbyte.length; i++) {
	        if ((cipherbyte[i] & 0xff) < 0x10 ) {
	            stringbuffer.append("0");
	        }
	        stringbuffer.append(Long.toString(cipherbyte[i] & 0xff, 16));
	    }
	    String ciphertext = stringbuffer.toString();
	    return ciphertext;
	}
	
}
