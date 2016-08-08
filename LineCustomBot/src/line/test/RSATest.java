package line.test;

import java.io.IOException;
import java.math.BigInteger;

import javax.security.auth.login.LoginException;

import org.junit.Test;

import line.client.LineClient;

public class RSATest {

	String n = "a21dd61cf0d5479fb541fe24ebffec69f63cc28c682fe8b694fb310f721fa75d309a8f5ec68a588e6fd04a1d63dbc826dffcf75eaf0fb5d81f3a58700aecd6174e4ed5779879577761f897f2338e3e4747a32d0bdfb66fc462f6fc5c977a9f0c22713b2c9416f17b6391456f9bfc9185b20c72a966cc53b302176cf739467e77318b069f74eb48fc58eae262f5048caa7fe06d83ddb3";
	
	String e = "10001";
	
	
	@Test
	public void test() throws IOException, LoginException {
		byte[] bytes = n.getBytes();
		BigInteger bigInteger = new BigInteger(bytes);
		bigInteger = new BigInteger(n,16);
		System.out.println(bigInteger);
	}
	
}
