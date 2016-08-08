package line.client;

import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import line.model.Provider;

public class LineClient extends LineConnector{

	String[] contacts;

	private Pattern EmailRegex = Pattern.compile("[^@]+@[^@]+\\.[^@]+");

	String[] groups;

	String profile = null;

	Provider provider;

	String[] rooms;
	
	/**
	 * 
	 * @param id
	 * @param password
	 * @throws LoginException
	 * @throws IOException
	 */
	public LineClient(String id, String password) throws LoginException, IOException {
		super();
		this.init(id, password, false, "carpedm20");
	}

	public LineClient(String id, String password, boolean isMac) throws LoginException, IOException {
		super();
		this.init(id, password, isMac, "carpedm20");
	}

	public LineClient(String id, String password, boolean isMac, String comName) throws LoginException, IOException {
		super();
		this.init(id, password, isMac, comName);
	}

	public void checkAuth() {
		// def wrapper_check_auth(*args, **kwargs):
		// if args[0]._check_auth():
		// return func(*args, **kwargs)
		// return wrapper_check_auth
	}

	/**
	 * 
	 * @param id
	 * @param password
	 * @param isMac
	 * @param comName
	 * @throws LoginException
	 */
	private void init(String id, String password, boolean isMac, String comName) throws LoginException {
		if (id != null && !id.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
			String osVersion;
			String userAgent;
			String app;
			
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb, Locale.US);
			if (isMac) {
				// os_version = "10.9.4-MAVERICKS-x64"
				osVersion = "10.9.4-MAVERICKS-x64";
				// user_agent = "DESKTOP:MAC:%s(%s)" % (os_version,self.version)
				userAgent = formatter.format("DESKTOP:MAC:%s(%s)", osVersion, this.version).toString();
				// app = "DESKTOPMAC\t%s\tMAC\t%s" % (self.version, os_version)
				app = formatter.format("DESKTOPMAC\t%s\tMAC\t%s", this.version, osVersion).toString();
			} else {
				// os_version = "5.1.2600-XP-x64"
				osVersion = "5.1.2600-XP-x64";
				// user_agent = "DESKTOP:WIN:%s(%s)" % (os_version,self.version)
				userAgent = formatter.format("DESKTOP:WIN:%s(%s)", osVersion, this.version).toString();
				// app = "DESKTOPWIN\t%s\tWINDOWS\t%s" %(self.version,os_version)
				app = formatter.format("DESKTOPWIN\t%s\tWINDOWS\t%s", this.version, osVersion).toString();
			}
			formatter.close();
			if (comName != null && !comName.trim().isEmpty()) {
				this.com_name = comName;
			}

			this.headers.put("User-Agent", userAgent);
			this.headers.put("X-Line-Application", app);
			
			Matcher matcher = EmailRegex.matcher(id);
			if (matcher.matches()) {
				this.provider  = Provider.LINE;
			} else {
				this.provider =  Provider.NAVER;
			}
			this.id = id;
			this.password = password;
			
			this.login();
			
			// TODO
			// self.revision = self._getLastOpRevision()
			// self.getProfile()
			// try:
			// self.refreshGroups()
			// except: pass
			//
			// try:
			// self.refreshContacts()
			// except: pass
			//
			// try:
			// self.refreshActiveRooms()
			// except: pass

		} else {
			throw new LoginException();
		}
	}

}
