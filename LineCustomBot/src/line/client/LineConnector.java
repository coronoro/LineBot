package line.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.apache.commons.io.IOUtils;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.json.JSONObject;

import line.api.Api;
import line.exceptions.NoSessionKeyException;
import line.model.Provider;
import line.util.EncodingUtil;

public class LineConnector implements Api {

	final String LINE_DOMAIN = "http://gd2.line.naver.jp";

	private final String LINE_HTTP_URL = LINE_DOMAIN + "/api/v4/TalkService.do";
	private final String LINE_HTTP_IN_URL = LINE_DOMAIN + "/P4";
	private final String LINE_CERTIFICATE_URL = LINE_DOMAIN + "/Q";
	private final String LINE_SESSION_LINE_URL = LINE_DOMAIN + "/authct/v1/keys/line";
	private final String LINE_SESSION_NAVER_URL = LINE_DOMAIN + "/authct/v1/keys/naver";

	HttpURLConnection session;

	String ip = "127.0.0.1";
	protected String version = "5.1.2";
	String com_name = "";
	int revision = 0;
	String certificate = "";

	public Map<String, String> headers = new HashMap<>();

	public Provider provider = Provider.LINE;

	public String id;

	public String password;

	public LineConnector() throws IOException {
		URL urlObj = new URL(LINE_DOMAIN);
		session = (HttpURLConnection) urlObj.openConnection();
	}

	@Override
	public void ready() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean updateAuthToken() throws Exception {
		if (this.certificate != null) {
			this.login();
			this.tokenLogin();

			return true;
		} else {
			throw new Exception("You need to login first. There is no valid certificate");
		}
	}

	@Override
	public void tokenLogin() throws TTransportException {
		THttpClient client = new THttpClient(this.LINE_HTTP_URL);
		client.setCustomHeaders(this.headers);

		TCompactProtocol protocol = new TCompactProtocol(client);
		// self.protocol = TCompactProtocol.TCompactProtocol(self.transport)
		// self._client = CurveThrift.Client(self.protocol)

	}

	@Override
	public void login() {
		// get session key
		ByteBuffer encode = null;
		JSONObject json = null;
		try {
			if (this.provider.equals(Provider.LINE)) {
				json = getContentJson(new URL(this.LINE_SESSION_LINE_URL));
			} else {
				json = getContentJson(new URL(this.LINE_SESSION_NAVER_URL));
			}
			if (json != null) {
				Object sessionKey = json.get("session_key");
				if (sessionKey instanceof String) {
					String message = Character.toString((char) ((String) sessionKey).length()) + Character.toString((char) this.id.length()) + this.id
							+ Character.toString((char) this.password.length()) + this.password;
					encode = Charset.forName("UTF-8").encode(message);
				} else {
					throw new NoSessionKeyException();
				}
				Object object = json.get("rsa_key");
				String cipheredMessage = null;
				if (object instanceof String) {
					String[] split = ((String) object).split(",");
					try {
						if (split.length >= 3) {
							BigInteger n = new BigInteger(split[1], 16);
							BigInteger e = new BigInteger(split[2], 16);

							KeyFactory keyFactory = KeyFactory.getInstance("RSA");
							RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
							PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);

							Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
							cipher.init(Cipher.ENCRYPT_MODE, publicKey);
							ByteBuffer ciphered = ByteBuffer.allocate(1024 * 8);
							cipher.doFinal(encode, ciphered);

							// conversion to hex
							byte[] cipherbyte = ciphered.array();
							cipheredMessage = EncodingUtil.convertToHex(cipherbyte);
						}
					} catch (NoSuchAlgorithmException | InvalidKeySpecException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException
							| InvalidKeyException e) {
						e.printStackTrace();
					}

				}
				THttpClient client = new THttpClient(this.LINE_HTTP_URL);
				client.setCustomHeaders(this.headers);
				TCompactProtocol protocol = new TCompactProtocol(client);
				// self._client = CurveThrift.Client(self.protocol)
				
				
//		        try:
//		            with open(self.CERT_FILE,'r') as f:
//		                self.certificate = f.read()
//		                f.close()
//		        except:
//		            self.certificate = ""
//
//		        msg = self._client.loginWithIdentityCredentialForCertificate(
//		                self.id, self.password, keyname, crypto, True, self.ip,
//		                self.com_name, self.provider, self.certificate)
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * 
	 */
	public JSONObject getContentJson(URL arg) throws IOException {
		HttpURLConnection connection = connectWithHeaders(arg);
		Object content = connection.getContent();
		String string = null;
		if (content instanceof InputStream) {
			string = IOUtils.toString((InputStream) content, (String) null);
		}
		return new JSONObject(string);
	}

	/**
	 * TODO clean
	 * 
	 * @param arg
	 * @return
	 */
	protected HttpURLConnection connectWithHeaders(URL arg) {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) arg.openConnection();

			Set<String> keySet = this.headers.keySet();
			Iterator<String> iterator = keySet.iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				connection.setRequestProperty(key, this.headers.get(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;

	}

}
