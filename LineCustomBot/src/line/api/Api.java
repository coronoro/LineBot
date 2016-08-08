package line.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.thrift.transport.TTransportException;
import org.json.JSONObject;

public interface Api {
	
	public void ready();
	
	public boolean updateAuthToken() throws Exception;
	
	public void tokenLogin() throws TTransportException;
	
	public void login() throws MalformedURLException, IOException;
	
	public JSONObject getContentJson(URL arg) throws IOException;

}
