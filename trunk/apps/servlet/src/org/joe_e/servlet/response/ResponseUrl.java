package org.joe_e.servlet.response;

import java.net.MalformedURLException;

public class ResponseUrl {

	public static final int HTTP = 0;
	public static final int HTTPS = 1;
	
	private String url;
	
	public ResponseUrl(int protocol, String host, String port, String path, String query) {
		if (protocol == HTTP) {
				url = "http://"+ host +":"+port+"/"+path;
		} else if (protocol == HTTPS) {
			url = "https://"+ host +":"+port+"/"+path;
		}
		if (query != null) {
			url += "?"+query;
		}
	}
	
	public ResponseUrl(String path, String query) throws MalformedURLException {
		if (path.charAt(0) != '/' && path.substring(0, 2).equals("./") && path.substring(0,3).equals("../")) {
			throw new MalformedURLException("illegal url");
		} else {
			url = path;
		}
		if (query != null) {
			url += "?"+query;
		}
	}
	
	public String getURL() {
		return url;
	}
	
}
