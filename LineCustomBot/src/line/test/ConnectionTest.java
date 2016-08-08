package line.test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.junit.Test;

import line.client.LineClient;
import line.client.LineConnector;

public class ConnectionTest {

	@Test
	public void test() throws IOException, LoginException {
		LineClient lineClient = new LineClient("streicher.tim@googlemail.com", "Line25");
		lineClient.login();
	}

}
