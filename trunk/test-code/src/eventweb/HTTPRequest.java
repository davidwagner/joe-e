package eventweb;

import java.util.Map;
import java.util.HashMap;

public class HTTPRequest {
	/* TODO?
	enum Method {
		GET, PUT, 
	}
	*/
	
	public String method;  // GET, PUT, etc
	public String requestURI;
	public String httpVersion;
	public Map<String, String> headers;
	// byte[] content;  // for now assume no content (must change to support PUT and POST)
	
	public HTTPRequest(String reqString)
	{
		int firstNewLine = reqString.indexOf("\r\n");
		String firstLine = reqString.substring(0, firstNewLine);
		int firstSP = firstLine.indexOf(" ");
		method = firstLine.substring(0, firstSP);
		int secondSP = firstLine.indexOf(" ", firstSP + 1);
		if (secondSP == -1) {
			requestURI = firstLine.substring(firstSP + 1);
			httpVersion = null;
		} else {
			requestURI = firstLine.substring(firstSP + 1, secondSP);
			httpVersion = firstLine.substring(secondSP + 1);
		}
		
		int start = firstNewLine + "\r\n".length();
		headers = new HashMap<String, String>();
		
		if (start < reqString.length()) {  // if any headers present
			int colonLocation = reqString.indexOf(":", start);
			int nextNewLine = reqString.indexOf("\r\n", start);
			String currentKey = reqString.substring(start, colonLocation);
			String currentValue = reqString.substring(colonLocation + 1, nextNewLine);
			
			start = nextNewLine + "\r\n".length();
			while (start < reqString.length()) {
				nextNewLine = reqString.indexOf("\r\n", start);
				if (reqString.charAt(start) == ' ' || reqString.charAt(start) == '\t') {
					currentValue += reqString.substring(start, nextNewLine);
				} else {
					headers.put(currentKey, currentValue);
					colonLocation = reqString.indexOf(":", start);
					nextNewLine = reqString.indexOf("\r\n", start);
					currentKey = reqString.substring(start, colonLocation);
					currentValue += reqString.substring(colonLocation + 1, nextNewLine); 
				}
				
				start = nextNewLine + "\r\n".length();
			}
			
			headers.put(currentKey, currentValue);
		}
	}
}
