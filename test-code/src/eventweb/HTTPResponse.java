package eventweb;

// import org.joe_e.Incapable;

public class HTTPResponse {
	static String describe(int code) {
		switch(code) {
		case 200:
			return "OK";
		case 404:
			return "File not found.";
		default:
			return "Unknown";
		}
	}
	
	HTTPResponse(int code, byte[] content) 
	{
		this.code = code;
		this.content = content;
	}
	
	int code; // http status code
	// String optionLines;
	
	byte[] content;
}
